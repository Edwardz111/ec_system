package com.ec.service;

import com.ec.pojo.Cart;

import java.util.List;

public interface CartService {
    void addCart(Cart cart);
    void updateCartQuantity(Long id, Integer quantity);
    void deleteCart(Long id);
    void deleteCartByProductId(Long userId, Long productId);
    void batchDeleteCart(String ids);
    List<Cart> getCartList(Long userId);
    List<Cart> getAllCarts();
    void clearCart(Long userId);
}
