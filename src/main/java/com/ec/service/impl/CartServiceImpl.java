package com.ec.service.impl;

import com.ec.mapper.CartMapper;
import com.ec.pojo.Cart;
import com.ec.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    
    @Autowired
    private CartMapper cartMapper;
    
    @Override
    @Transactional
    public void addCart(Cart cart) {
        // 查找是否已存在相同商品的购物车项
        Cart existingCart = cartMapper.findByUserIdAndProductId(cart.getUserId(), cart.getProductId());
        if (existingCart != null) {
            // 已存在，更新数量
            existingCart.setQuantity(existingCart.getQuantity() + cart.getQuantity());
            cartMapper.update(existingCart);
            return;
        }
        // 不存在，添加新购物车项
        cartMapper.insert(cart);
    }
    
    @Override
    @Transactional
    public void updateCartQuantity(Long id, Integer quantity) {
        Cart cart = new Cart();
        cart.setId(id);
        cart.setQuantity(quantity);
        cartMapper.update(cart);
    }
    
    @Override
    @Transactional
    public void deleteCart(Long id) {
        cartMapper.delete(id);
    }
    
    @Override
    @Transactional
    public void deleteCartByProductId(Long userId, Long productId) {
        cartMapper.deleteByUserIdAndProductId(userId, productId);
    }
    
    @Override
    public List<Cart> getCartList(Long userId) {
        return cartMapper.findByUserId(userId);
    }
    
    @Override
    public List<Cart> getAllCarts() {
        return cartMapper.findAll();
    }
    
    @Override
    @Transactional
    public void batchDeleteCart(String ids) {
        if (ids != null && !ids.isEmpty()) {
            String[] idArray = ids.split(",");
            for (String id : idArray) {
                try {
                    Long cartId = Long.parseLong(id.trim());
                    cartMapper.delete(cartId);
                } catch (NumberFormatException e) {
                    // 忽略无效的ID
                }
            }
        }
    }
    
    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartMapper.deleteByUserId(userId);
    }
}
