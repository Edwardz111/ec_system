package com.ec.service.impl;

import com.ec.mapper.OrderItemMapper;
import com.ec.mapper.OrderMapper;
import com.ec.pojo.Cart;
import com.ec.pojo.Order;
import com.ec.pojo.OrderItem;
import com.ec.pojo.Product;
import com.ec.service.OrderService;
import com.ec.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductService productService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Order order, List<OrderItem> orderItems) {
        return persistOrderWithItems(order, orderItems);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrderFromCarts(Order order, List<Cart> carts) {
        List<OrderItem> orderItems = buildOrderItemsFromCarts(carts);
        return persistOrderWithItems(order, orderItems);
    }

    @Override
    public List<Cart> filterSelectedCarts(List<Cart> allCarts, String selectedItems) {
        if (allCarts == null || allCarts.isEmpty()) {
            return Collections.emptyList();
        }
        if (selectedItems == null || selectedItems.trim().isEmpty()) {
            return new ArrayList<>(allCarts);
        }
        List<Cart> selected = new ArrayList<>();
        String[] ids = selectedItems.split(",");
        for (Cart cart : allCarts) {
            for (String id : ids) {
                if (cart.getId() != null && String.valueOf(cart.getId()).equals(id.trim())) {
                    selected.add(cart);
                    break;
                }
            }
        }
        return selected;
    }

    @Override
    public void enrichCartWithProductInfo(List<Cart> carts) {
        if (carts == null) {
            return;
        }
        for (Cart cart : carts) {
            if (cart.getProductId() == null) {
                continue;
            }
            Product product = productService.getProductById(cart.getProductId());
            if (product != null) {
                cart.setProductName(product.getName());
                cart.setPrice(product.getPrice());
            }
        }
    }

    private List<OrderItem> buildOrderItemsFromCarts(List<Cart> carts) {
        if (carts == null || carts.isEmpty()) {
            return Collections.emptyList();
        }
        enrichCartWithProductInfo(carts);
        List<OrderItem> items = new ArrayList<>();
        for (Cart cart : carts) {
            if (cart.getProductId() == null) {
                continue;
            }
            int qty = cart.getQuantity() != null && cart.getQuantity() > 0 ? cart.getQuantity() : 1;
            Product product = productService.getProductById(cart.getProductId());
            if (product == null) {
                throw new IllegalArgumentException("商品不存在或已下架，无法下单（商品ID: " + cart.getProductId() + "）");
            }
            OrderItem item = new OrderItem();
            item.setProductId(cart.getProductId());
            item.setProductName(product.getName());
            item.setQuantity(qty);
            item.setPrice(product.getPrice());
            items.add(item);
        }
        return items;
    }

    private Order persistOrderWithItems(Order order, List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("订单明细不能为空，请确认购物车中有有效商品");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            if (item.getPrice() == null || item.getQuantity() == null) {
                throw new IllegalArgumentException("订单明细价格或数量无效");
            }
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        order.setTotalAmount(total);
        order.setItemCount(orderItems.size());

        orderMapper.insert(order);
        if (order.getId() == null) {
            throw new IllegalStateException("订单主键生成失败，无法写入订单明细");
        }

        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            if (orderItemMapper.insert(item) != 1) {
                throw new IllegalStateException("订单明细插入失败，商品ID: " + item.getProductId());
            }
        }
        return order;
    }

    @Override
    public Order getOrderById(Long id) {
        return orderMapper.findById(id);
    }

    @Override
    public List<Order> getOrderList(Long userId) {
        return orderMapper.findByUserId(userId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderMapper.findAll();
    }

    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        List<OrderItem> items = orderItemMapper.findByOrderId(orderId);
        return items != null ? items : Collections.emptyList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(Long id, String status) {
        orderMapper.updateStatus(id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long id) {
        orderMapper.updateStatus(id, "CANCELLED");
    }
}
