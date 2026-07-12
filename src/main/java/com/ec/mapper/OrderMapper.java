package com.ec.mapper;

import com.ec.pojo.Order;

import java.util.List;

public interface OrderMapper {
    void insert(Order order);
    void update(Order order);
    Order findById(Long id);
    List<Order> findByUserId(Long userId);
    List<Order> findAll();
    void updateStatus(Long id, String status);
}