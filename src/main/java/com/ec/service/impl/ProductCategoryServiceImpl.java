package com.ec.service.impl;

import com.ec.mapper.ProductCategoryMapper;
import com.ec.mapper.ProductMapper;
import com.ec.pojo.ProductCategory;
import com.ec.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductCategoryMapper categoryMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<ProductCategory> listAll() {
        return categoryMapper.findAll();
    }

    @Override
    public List<ProductCategory> listRoots() {
        return categoryMapper.findRoots();
    }

    @Override
    public List<ProductCategory> listChildren(Long parentId) {
        return categoryMapper.findByParentId(parentId);
    }

    @Override
    public ProductCategory getById(Long id) {
        ProductCategory c = categoryMapper.findById(id);
        if (c == null) throw new IllegalArgumentException("分类不存在");
        return c;
    }

    @Override
    public ProductCategory create(String name, Long parentId) {
        ProductCategory c = new ProductCategory();
        c.setName(name);
        c.setParentId(parentId);
        categoryMapper.insert(c);
        return categoryMapper.findById(c.getCategoryId());
    }

    @Override
    public ProductCategory update(Long id, String name, Long parentId) {
        ProductCategory c = getById(id);
        c.setName(name);
        c.setParentId(parentId);
        categoryMapper.update(c);
        return categoryMapper.findById(id);
    }

    @Override
    public void delete(Long id) {
        if (productMapper.countByCategoryId(id) > 0) {
            throw new IllegalStateException("该分类下存在商品，无法删除");
        }
        categoryMapper.delete(id);
    }

    @Override
    public List<ProductCategory> listLeafCategories() {
        List<ProductCategory> all = categoryMapper.findAll();
        Set<Long> parentIds = new HashSet<>();
        for (ProductCategory c : all) {
            if (c.getParentId() != null) parentIds.add(c.getParentId());
        }
        List<ProductCategory> leaves = new ArrayList<>();
        for (ProductCategory c : all) {
            if (!parentIds.contains(c.getCategoryId())) leaves.add(c);
        }
        return leaves;
    }

    @Override
    public ProductCategory requireLeafCategory(Long categoryId) {
        if (categoryId == null) throw new IllegalArgumentException("添加商品必须选择分类");
        ProductCategory category = getById(categoryId);
        if (categoryMapper.countChildren(categoryId) > 0) {
            throw new IllegalArgumentException("只能选择最细化的分类作为商品分类");
        }
        return category;
    }
}
