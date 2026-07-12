package com.ec.mapper;

import com.ec.pojo.Promotion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PromotionMapper {

    List<Promotion> findAll();

    Promotion findById(Long id);

    int insert(Promotion promotion);

    int update(Promotion promotion);

    int delete(Long id);

    List<Long> findProductIds(Long promotionId);

    void insertPromotionProducts(@Param("promotionId") Long promotionId, @Param("productIds") List<Long> productIds);

    void deletePromotionProducts(Long promotionId);
}
