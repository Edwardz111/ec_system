package com.ec.service.impl;

import com.ec.mapper.ProductMapper;
import com.ec.pojo.Product;
import com.ec.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public List<Product> getAllProducts() {
        return productMapper.findAll();
    }
    
    @Override
    public Product getProductById(Long id) {
        return productMapper.findById(id);
    }
    
    @Override
    public void addProduct(Product product) {
        productMapper.insert(product);
    }
    
    @Override
    public void updateProduct(Product product) {
        productMapper.update(product);
    }
    
    @Override
    public void deleteProduct(Long id) {
        productMapper.delete(id);
    }
}
