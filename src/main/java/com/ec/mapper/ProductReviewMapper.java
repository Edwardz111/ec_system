package com.ec.mapper;

import com.ec.pojo.ProductReview;
import com.ec.pojo.ProductReviewSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductReviewMapper {

    int insert(ProductReview review);

    List<ProductReview> selectByProductId(
            @Param("productId") Long productId,
            @Param("offset") Integer offset,
            @Param("size") Integer size
    );

    ProductReviewSummary selectAvgScoreByProductId(@Param("productId") Long productId);

    int updateStatusById(@Param("id") Long id, @Param("status") String status);

    List<ProductReview> findAll();

    List<ProductReview> selectByOrderId(@Param("orderId") Long orderId);

    ProductReview selectByOrderIdAndProductId(
            @Param("orderId") Long orderId,
            @Param("productId") Long productId);

    ProductReview selectById(@Param("id") Long id);

    List<ProductReview> searchReviews(
            @Param("userId") Long userId,
            @Param("orderId") Long orderId,
            @Param("productId") Long productId,
            @Param("rating") Integer rating,
            @Param("status") String status);

    int updateReplyById(@Param("id") Long id, @Param("reply") String reply);

    int deleteById(@Param("id") Long id);
}
