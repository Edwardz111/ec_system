package com.ec.mapper;

import com.ec.pojo.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {

    List<Product> findAll();

    Product findById(Long id);

    void insert(Product product);

    void update(Product product);

    void delete(Long id);

    long countSearch(@Param("params") Map<String, Object> params);

    List<Product> search(@Param("params") Map<String, Object> params);

    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);

    long countByCategoryId(Long categoryId);

    List<Long> findIdsByPromotionId(Long promotionId);

    void linkPromotionProducts(@Param("promotionId") Long promotionId, @Param("productIds") List<Long> productIds);

    void unlinkPromotionProducts(Long promotionId);

    List<Map<String, Object>> listReviews(@Param("productId") Long productId, @Param("limit") int limit);

    Map<String, Object> reviewSummary(Long productId);
}
