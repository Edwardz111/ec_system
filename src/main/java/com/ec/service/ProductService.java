package com.ec.service;

import com.ec.pojo.Product;

import java.util.List;

public interface ProductService {
    
    // 查询所有商品
    List<Product> getAllProducts();
    
    // 根据ID查询商品
    Product getProductById(Long id);
    
    // 新增商品
    void addProduct(Product product);
    
    // 更新商品
    void updateProduct(Product product);
    
    // 删除商品
    void deleteProduct(Long id);
}
