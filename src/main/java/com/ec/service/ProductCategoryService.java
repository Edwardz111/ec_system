package com.ec.service;

import com.ec.pojo.ProductCategory;

import java.util.List;

public interface ProductCategoryService {
    List<ProductCategory> listAll();
    List<ProductCategory> listRoots();
    List<ProductCategory> listChildren(Long parentId);
    ProductCategory getById(Long id);
    ProductCategory create(String name, Long parentId);
    ProductCategory update(Long id, String name, Long parentId);
    void delete(Long id);
    List<ProductCategory> listLeafCategories();
    ProductCategory requireLeafCategory(Long categoryId);
}
