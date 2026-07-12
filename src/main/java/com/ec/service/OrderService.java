package com.ec.service;

import com.ec.pojo.Cart;
import com.ec.pojo.Order;
import com.ec.pojo.OrderItem;

import java.util.List;

public interface OrderService {
    Order createOrder(Order order, List<OrderItem> orderItems);

    /** 根据购物车生成订单并写入 order_items（同一事务） */
    Order createOrderFromCarts(Order order, List<Cart> carts);

    List<Cart> filterSelectedCarts(List<Cart> allCarts, String selectedItems);

    void enrichCartWithProductInfo(List<Cart> carts);
    Order getOrderById(Long id);
    List<Order> getOrderList(Long userId);
    List<Order> getAllOrders();
    List<OrderItem> getOrderItems(Long orderId);
    void updateOrderStatus(Long id, String status);
    void cancelOrder(Long id);
}