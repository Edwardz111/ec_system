package com.ec.controller;

import com.ec.pojo.Address;
import com.ec.pojo.Cart;
import com.ec.pojo.Order;
import com.ec.pojo.OrderItem;
import com.ec.pojo.Product;
import com.ec.pojo.ProductReview;
import com.ec.pojo.User;
import com.ec.service.CartService;
import com.ec.service.OrderService;
import com.ec.service.ProductReviewService;
import com.ec.service.ProductService;
import com.ec.service.Userservice;
import com.ec.util.OrderStatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductReviewService productReviewService;
    
    @Autowired
    private Userservice userService;
    
    // ==================== 购物车管理 ====================
    
    // 购物车页面
    @GetMapping("/order/cart")
    public String cart(Model model, @RequestParam Long userId) {
        try {
            System.out.println("=== 开始处理购物车请求 ===");
            System.out.println("用户ID: " + userId);
            
            List<Cart> cartList = cartService.getCartList(userId);
            System.out.println("购物车记录数: " + (cartList != null ? cartList.size() : 0));
            
            BigDecimal totalAmount = BigDecimal.ZERO;
            // 预先填充商品信息到购物车对象
            if (cartList != null) {
                for (Cart cart : cartList) {
                    System.out.println("处理购物车项: ID=" + cart.getId() + ", 商品ID=" + cart.getProductId());
                    Product product = productService.getProductById(cart.getProductId());
                    if (product != null) {
                        System.out.println("找到商品: " + product.getName() + ", 价格: " + product.getPrice());
                        cart.setProductName(product.getName());
                        cart.setPrice(product.getPrice());
                        BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
                        totalAmount = totalAmount.add(itemTotal);
                    } else {
                        System.out.println("未找到商品，ID: " + cart.getProductId());
                    }
                }
            }
            
            model.addAttribute("cartList", cartList);
            model.addAttribute("userId", userId);
            model.addAttribute("totalAmount", totalAmount);
            
            System.out.println("购物车数据准备完成，返回页面");
            return "order/cart";
        } catch (Exception e) {
            System.err.println("=== 购物车页面处理错误 ===");
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    
    // 添加商品到购物车
    @PostMapping("/order/addCart")
    @ResponseBody
    public String addCart(@RequestBody Cart cart) {
        cartService.addCart(cart);
        return "success";
    }
    
    // 更新购物车商品数量
    @PostMapping("/order/updateCart")
    @ResponseBody
    public String updateCart(@RequestParam Long id, @RequestParam Integer quantity) {
        if (quantity <= 0) {
            cartService.deleteCart(id);
        } else {
            cartService.updateCartQuantity(id, quantity);
        }
        return "success";
    }
    
    // 删除购物车商品
    @PostMapping("/order/deleteCart")
    @ResponseBody
    public String deleteCart(@RequestParam Long id) {
        cartService.deleteCart(id);
        return "success";
    }
    
    // 清空购物车
    @PostMapping("/order/clearCart")
    @ResponseBody
    public String clearCart(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            cartService.clearCart(userId);
        }
        return "success";
    }
    
    // 批量删除购物车商品
    @PostMapping("/order/batchDeleteCart")
    @ResponseBody
    public String batchDeleteCart(@RequestParam String ids) {
        try {
            cartService.batchDeleteCart(ids);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
    
    // ==================== 订单管理 ====================
    
    // 订单确认页面
    @GetMapping("/order/confirm")
    public String orderConfirm(Model model, @RequestParam Long userId,
                              @RequestParam(required = false) String selectedItems,
                              @RequestParam(required = false) Long addressId) {
        List<Cart> cartList = cartService.getCartList(userId);
        orderService.enrichCartWithProductInfo(cartList);
        List<Cart> selectedCartList = orderService.filterSelectedCarts(cartList, selectedItems);

        BigDecimal totalAmount = BigDecimal.ZERO;
        StringBuilder cartIdCsv = new StringBuilder();
        for (Cart cart : selectedCartList) {
            if (cart.getId() != null) {
                if (cartIdCsv.length() > 0) {
                    cartIdCsv.append(',');
                }
                cartIdCsv.append(cart.getId());
            }
            if (cart.getPrice() != null && cart.getQuantity() != null) {
                totalAmount = totalAmount.add(cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            }
        }

        List<Address> addressList = userService.listAddress(userId);
        Long selectedAddressId = resolveSelectedAddressId(userId, addressId, addressList);

        model.addAttribute("cartList", selectedCartList);
        model.addAttribute("userId", userId);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("defaultSelectedItems", cartIdCsv.toString());
        model.addAttribute("addressList", addressList);
        model.addAttribute("selectedAddressId", selectedAddressId);
        model.addAttribute("addressManageUrl", buildAddressManageUrl(userId, cartIdCsv.toString()));
        model.addAttribute("productService", productService);
        return "order/order-confirm";
    }

    private Long resolveSelectedAddressId(Long userId, Long addressId, List<Address> addressList) {
        if (addressList == null || addressList.isEmpty()) {
            return null;
        }
        if (addressId != null) {
            Address chosen = userService.getAddressForUser(addressId, userId);
            if (chosen != null) {
                return chosen.getId();
            }
        }
        Address defaultAddr = userService.getDefaultAddress(userId);
        if (defaultAddr != null) {
            return defaultAddr.getId();
        }
        return addressList.get(0).getId();
    }

    private String buildAddressManageUrl(Long userId, String selectedItems) {
        try {
            StringBuilder redirect = new StringBuilder("/order/confirm?userId=").append(userId);
            if (selectedItems != null && !selectedItems.isEmpty()) {
                redirect.append("&selectedItems=").append(URLEncoder.encode(selectedItems, StandardCharsets.UTF_8.name()));
            }
            return "/user/address?redirect=" + URLEncoder.encode(redirect.toString(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return "/user/address";
        }
    }
    
    // 提交订单
    @PostMapping("/order/submit")
    public String submitOrder(@RequestParam Long userId, @RequestParam String paymentMethod,
                            @RequestParam Long addressId,
                            @RequestParam(required = false) String remark,
                            @RequestParam(required = false) String selectedItems,
                            Model model) {
        try {
            System.out.println("=== 开始提交订单 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("支付方式: " + paymentMethod);
            System.out.println("选中的商品ID: " + selectedItems);
            
            List<Cart> allCartList = cartService.getCartList(userId);
            List<Cart> cartList = orderService.filterSelectedCarts(allCartList, selectedItems);
            System.out.println("最终要提交的商品数量: " + cartList.size());

            if (cartList.isEmpty()) {
                model.addAttribute("error", "请选择要结算的商品");
                return "redirect:/order/cart?userId=" + userId;
            }

            Address shippingAddress = userService.getAddressForUser(addressId, userId);
            if (shippingAddress == null) {
                model.addAttribute("error", "请选择有效的收货地址");
                String back = "/order/confirm?userId=" + userId;
                if (selectedItems != null && !selectedItems.isEmpty()) {
                    back += "&selectedItems=" + selectedItems;
                }
                return "redirect:" + back;
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setAddressId(shippingAddress.getId());
            order.setStatus(OrderStatusUtil.COMPLETED);
            order.setShippingStatus(OrderStatusUtil.COMPLETED);
            order.setPaymentMethod(paymentMethod);
            order.setDiscountAmount(BigDecimal.ZERO);
            order.setRemark(remark);

            System.out.println("开始创建订单及明细...");
            Order newOrder = orderService.createOrderFromCarts(order, cartList);
            System.out.println("订单创建成功，订单ID: " + newOrder.getId()
                    + "，明细条数: " + newOrder.getItemCount());

            List<OrderItem> orderItems = orderService.getOrderItems(newOrder.getId());
            
            // 删除已提交到订单的购物车商品
            System.out.println("开始删除购物车商品...");
            for (Cart cart : cartList) {
                System.out.println("删除购物车商品: " + cart.getId());
                cartService.deleteCart(cart.getId());
            }
            
            model.addAttribute("order", newOrder);
            model.addAttribute("orderItems", orderItems);
            model.addAttribute("shippingAddress", shippingAddress);
            model.addAttribute("productService", productService);

            System.out.println("=== 订单提交成功 ===");
            return "order/order-success";
        } catch (Exception e) {
            System.err.println("=== 订单提交失败 ===");
            e.printStackTrace();
            model.addAttribute("error", "订单提交失败：" + e.getMessage());
            return "redirect:/order/cart?userId=" + userId;
        }
    }
    
    // 我的订单列表
    @GetMapping("/order/list")
    public String orderList(Model model, @RequestParam Long userId) {
        System.out.println("=== 开始获取订单列表 ===");
        System.out.println("用户ID: " + userId);
        
        List<Order> orderList = orderService.getOrderList(userId);
        System.out.println("订单数量: " + (orderList != null ? orderList.size() : 0));
        
        // 设置默认值并获取订单明细
        if (orderList != null) {
            for (Order order : orderList) {
                if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
                    order.setOrderNo("EC" + System.currentTimeMillis());
                }
                if (order.getItemCount() == null) {
                    order.setItemCount(1);
                }
                System.out.println("订单ID: " + order.getId() + ", 订单号: " + order.getOrderNo() + ", 金额: " + order.getTotalAmount());
            }
        }
        
        // 获取所有订单的订单明细
        Map<Long, List<OrderItem>> orderItemsMap = new java.util.HashMap<>();
        if (orderList != null) {
            for (Order order : orderList) {
                List<OrderItem> items = orderService.getOrderItems(order.getId());
                
                // 如果商品名称为null，从商品表查询
                if (items != null) {
                    for (OrderItem item : items) {
                        if (item.getProductName() == null || item.getProductName().isEmpty()) {
                            Product product = productService.getProductById(item.getProductId());
                            if (product != null) {
                                item.setProductName(product.getName());
                            }
                        }
                    }
                }
                
                orderItemsMap.put(order.getId(), items);
                System.out.println("订单ID " + order.getId() + " 的明细数量: " + (items != null ? items.size() : 0));
                if (items != null) {
                    for (OrderItem item : items) {
                        System.out.println("  - 商品ID: " + item.getProductId() + ", 名称: " + item.getProductName() + ", 价格: " + item.getPrice() + ", 数量: " + item.getQuantity());
                    }
                }
            }
        }
        
        model.addAttribute("orderList", orderList);
        model.addAttribute("orderItemsMap", orderItemsMap);
        model.addAttribute("userId", userId);
        System.out.println("=== 订单列表页面准备完成 ===");
        return "order/order-list";
    }
    
    // 订单详情
    @GetMapping("/order/detail")
    public String orderDetail(Model model, @RequestParam Long id) {
        try {
            Order order = orderService.getOrderById(id);
            if (order == null) {
                return "redirect:/order/list?userId=1";
            }
            List<OrderItem> items = orderService.getOrderItems(id);
            for (OrderItem item : items) {
                if (item.getProductName() == null || item.getProductName().isEmpty()) {
                    Product product = productService.getProductById(item.getProductId());
                    if (product != null) {
                        item.setProductName(product.getName());
                    }
                }
            }
            Map<Long, ProductReview> reviewMap = new HashMap<>();
            List<ProductReview> reviews = productReviewService.getReviewsByOrderId(id);
            if (reviews != null) {
                for (ProductReview review : reviews) {
                    reviewMap.put(review.getProductId(), review);
                }
            }
            model.addAttribute("order", order);
            model.addAttribute("orderItems", items);
            model.addAttribute("reviewMap", reviewMap);
            model.addAttribute("canReview", OrderStatusUtil.canReview());
            model.addAttribute("statusText", OrderStatusUtil.getStatusText());
            Address shippingAddress = null;
            if (order.getAddressId() != null) {
                shippingAddress = userService.getAddressForUser(order.getAddressId(), order.getUserId());
            }
            model.addAttribute("shippingAddress", shippingAddress);
            return "order/order-detail";
        } catch (Exception e) {
            return "redirect:/order/list?userId=1";
        }
    }
    
    // 取消订单
    @PostMapping("/order/cancel")
    @ResponseBody
    public String cancelOrder(@RequestParam Long id) {
        orderService.cancelOrder(id);
        return "success";
    }
    
    // 模拟支付
    @PostMapping("/order/pay")
    @ResponseBody
    public String payOrder(@RequestParam Long id) {
        try {
            // 将订单状态从1（待付款）改为2（待发货）
            orderService.updateOrderStatus(id, "2");
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
    
    // 更新订单状态
    @PostMapping("/order/updateStatus")
    @ResponseBody
    public String updateOrderStatus(@RequestParam Long id, @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
        return "success";
    }
    
    // ==================== 商品管理 ====================
    
    // 商品列表页面
    @GetMapping("/product/list")
    public String productList(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "product/product-list";
    }
    
    // ==================== 测试端点 ====================
    
    // 检查数据库数据
    @GetMapping("/test/db")
    @ResponseBody
    public String testDatabase() {
        try {
            // 检查购物车数据
            List<Cart> carts = cartService.getCartList(1L);
            int cartCount = carts != null ? carts.size() : 0;
            
            // 检查订单数据
            List<Order> orders = orderService.getOrderList(1L);
            int orderCount = orders != null ? orders.size() : 0;
            
            return "数据库数据检查结果：\n\n" +
                   "购物车表 (cart_items) 记录数: " + cartCount + "\n" +
                   "订单表 (orders) 记录数: " + orderCount + "\n\n" +
                   "提示：如果数据为空，请在DataGrip中执行 wbcsql.sql 脚本插入测试数据";
        } catch (Exception e) {
            return "数据库查询失败！错误信息: " + e.getMessage();
        }
    }
    
    // 查询所有表的前5条数据
    @GetMapping("/test/db/all")
    @ResponseBody
    public String testAllTables() {
        try {
            StringBuilder result = new StringBuilder();
            
            // 查询用户表
            List<User> users = userService.getAllUsers();
            result.append("=== 用户表 (users) ===").append("\n");
            result.append("总记录数: ").append(users != null ? users.size() : 0).append("\n");
            if (users != null && !users.isEmpty()) {
                for (User u : users) {
                    result.append(String.format("ID:%d, 用户名:%s, 电话:%s, 邮箱:%s\n", 
                            u.getId(), u.getUsername(), u.getPhone(), u.getEmail()));
                }
            }
            result.append("\n");
            
            // 查询商品表
            List<Product> products = productService.getAllProducts();
            result.append("=== 商品表 (products) ===").append("\n");
            result.append("总记录数: ").append(products != null ? products.size() : 0).append("\n");
            if (products != null && !products.isEmpty()) {
                int limit = Math.min(5, products.size());
                for (int i = 0; i < limit; i++) {
                    Product p = products.get(i);
                    result.append(String.format("ID:%d, 名称:%s, 价格:%.2f, 库存:%d\n", 
                            p.getId(), p.getName(), p.getPrice(), p.getStock()));
                }
            }
            result.append("\n");
            
            // 查询购物车表（所有用户）
            List<Cart> carts = cartService.getAllCarts();
            result.append("=== 购物车表 (cart_items) ===").append("\n");
            result.append("总记录数: ").append(carts != null ? carts.size() : 0).append("\n");
            if (carts != null && !carts.isEmpty()) {
                for (Cart c : carts) {
                    Product p = productService.getProductById(c.getProductId());
                    String productName = p != null ? p.getName() : "未知商品";
                    User u = userService.findUserEntityById(c.getUserId());
                    String username = u != null ? u.getUsername() : "未知用户";
                    result.append(String.format("ID:%d, 用户:%s, 商品:%s, 数量:%d\n", 
                            c.getId(), username, productName, c.getQuantity()));
                }
            }
            result.append("\n");
            
            // 查询订单表（所有用户）
            List<Order> orders = orderService.getAllOrders();
            result.append("=== 订单表 (orders) ===").append("\n");
            result.append("总记录数: ").append(orders != null ? orders.size() : 0).append("\n");
            if (orders != null && !orders.isEmpty()) {
                for (Order o : orders) {
                    User u = userService.findUserEntityById(o.getUserId());
                    String username = u != null ? u.getUsername() : "未知用户";
                    result.append(String.format("ID:%d, 用户:%s, 总金额:%.2f, 状态:%s\n", 
                            o.getId(), username, o.getTotalAmount(), o.getStatus()));
                }
            }
            result.append("\n");
            
            return result.toString();
        } catch (Exception e) {
            return "数据库查询失败！错误信息: " + e.getMessage();
        }
    }
    
    // 测试购物车逻辑
    @GetMapping("/test/cart")
    @ResponseBody
    public String testCartLogic() {
        try {
            Long userId = 1L;
            List<Cart> cartList = cartService.getCartList(userId);
            StringBuilder result = new StringBuilder();
            
            result.append("=== 购物车测试 ===").append("\n");
            result.append("用户ID: ").append(userId).append("\n");
            result.append("购物车记录数: ").append(cartList != null ? cartList.size() : 0).append("\n\n");
            
            if (cartList != null && !cartList.isEmpty()) {
                for (Cart cart : cartList) {
                    result.append("购物车项ID: ").append(cart.getId()).append("\n");
                    result.append("商品ID: ").append(cart.getProductId()).append("\n");
                    result.append("数量: ").append(cart.getQuantity()).append("\n");
                    
                    Product product = productService.getProductById(cart.getProductId());
                    if (product != null) {
                        result.append("商品名称: ").append(product.getName()).append("\n");
                        result.append("商品价格: ").append(product.getPrice()).append("\n");
                    } else {
                        result.append("商品: 未找到\n");
                    }
                    result.append("\n");
                }
            }
            
            return result.toString();
        } catch (Exception e) {
            return "购物车测试失败！错误信息: " + e.getMessage() + "\n" + getStackTrace(e);
        }
    }
    
    private String getStackTrace(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        e.printStackTrace(new java.io.PrintWriter(sw));
        return sw.toString();
    }
}