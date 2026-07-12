package com.ec.service;

import com.ec.pojo.Product;
import com.ec.pojo.vo.PageResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** 商品目录服务（原 product 模块 JPA 能力，已转为 MyBatis） */
public interface CatalogProductService {

    List<Product> listAll();

    Product getById(Long id);

    Product create(Product product);

    Product update(Long id, Product product);

    void delete(Long id);

    PageResult<Product> searchProducts(String keyword, Long categoryId,
            BigDecimal minPrice, BigDecimal maxPrice, String sortBy, int page, int size);

    List<Product> getRecommendedProducts(Long categoryId, int limit);

    List<Map<String, Object>> listReviews(Long productId, int limit);

    Map<String, Object> reviewSummary(Long productId);
}
