package com.ec.service.impl;

import com.ec.mapper.ProductReviewMapper;
import com.ec.pojo.ProductReview;
import com.ec.pojo.ProductReviewSummary;
import com.ec.service.ProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {

    @Autowired
    private ProductReviewMapper productReviewMapper;

    @Override
    public boolean addReview(ProductReview review) {
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            return false;
        }
        if (review.getOrderId() == null || review.getProductId() == null || review.getUserId() == null) {
            return false;
        }
        if (hasReviewed(review.getOrderId(), review.getProductId())) {
            return false;
        }
        if (review.getStatus() == null || review.getStatus().isEmpty()) {
            review.setStatus("VISIBLE");
        }
        return productReviewMapper.insert(review) > 0;
    }

    @Override
    public List<ProductReview> getReviewsByProductId(Long productId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        return productReviewMapper.selectByProductId(productId, offset, size);
    }

    @Override
    public ProductReviewSummary getAvgScore(Long productId) {
        return productReviewMapper.selectAvgScoreByProductId(productId);
    }

    @Override
    public boolean deleteReview(Long reviewId) {
        return productReviewMapper.updateStatusById(reviewId, "HIDDEN") > 0;
    }

    @Override
    public List<ProductReview> getReviewsByOrderId(Long orderId) {
        return productReviewMapper.selectByOrderId(orderId);
    }

    @Override
    public boolean hasReviewed(Long orderId, Long productId) {
        return productReviewMapper.selectByOrderIdAndProductId(orderId, productId) != null;
    }
}
