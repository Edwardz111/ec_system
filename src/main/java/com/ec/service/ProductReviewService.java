package com.ec.service;

import com.ec.pojo.ProductReview;
import com.ec.pojo.ProductReviewSummary;
import java.util.List;

public interface ProductReviewService {
    boolean addReview(ProductReview review);
    List<ProductReview> getReviewsByProductId(Long productId, Integer page, Integer size);
    ProductReviewSummary getAvgScore(Long productId);
    boolean deleteReview(Long reviewId);

    List<ProductReview> getReviewsByOrderId(Long orderId);

    boolean hasReviewed(Long orderId, Long productId);
}
