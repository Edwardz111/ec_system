package com.ec.mapper;

import com.ec.pojo.Cart;

import java.util.List;

public interface CartMapper {
    void insert(Cart cart);
    void update(Cart cart);
    void delete(Long id);
    void deleteByUserIdAndProductId(Long userId, Long productId);
    Cart findByUserIdAndProductId(Long userId, Long productId);
    List<Cart> findByUserId(Long userId);
    List<Cart> findAll();
    void deleteByUserId(Long userId);
}
