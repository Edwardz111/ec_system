package com.ec.controller;

import com.ec.pojo.ProductReview;
import com.ec.pojo.ProductReviewSummary;
import com.ec.pojo.vo.Result;
import com.ec.service.ProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-reviews")
public class ProductReviewController {

    @Autowired
    private ProductReviewService productReviewService;

    @PostMapping
    public Result<Boolean> addReview(@RequestBody ProductReview review) {
        boolean success = productReviewService.addReview(review);
        return success ? Result.success(true) : Result.error("提交评价失败");
    }

    @GetMapping("/product/{productId}")
    public Result<List<ProductReview>> getReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(productReviewService.getReviewsByProductId(productId, page, size));
    }

    @GetMapping("/product/{productId}/score")
    public Result<ProductReviewSummary> getAvgScore(@PathVariable Long productId) {
        return Result.success(productReviewService.getAvgScore(productId));
    }

    @GetMapping("/order/{orderId}")
    public Result<List<ProductReview>> getReviewsByOrderId(@PathVariable Long orderId) {
        return Result.success(productReviewService.getReviewsByOrderId(orderId));
    }

    @DeleteMapping("/{reviewId}")
    public Result<Boolean> deleteReview(@PathVariable Long reviewId) {
        boolean success = productReviewService.deleteReview(reviewId);
        return success ? Result.success(true) : Result.error("删除评价失败");
    }
}
