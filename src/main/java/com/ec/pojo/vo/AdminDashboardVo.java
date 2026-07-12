package com.ec.pojo.vo;

import java.util.List;

public class AdminDashboardVo {
    private long userCount;
    private long productCount;
    private long orderCount;
    private long reviewCount;
    private long paidOrderCount;
    private long pendingShipCount;
    private long goodReviewCount;
    private long pendingReplyCount;
    /** 索引 0-4 对应 1-5 星 */
    private List<Integer> ratingCounts;
    private List<String> orderTrendLabels;
    private List<Integer> orderTrendCounts;

    public long getUserCount() { return userCount; }
    public void setUserCount(long userCount) { this.userCount = userCount; }
    public long getProductCount() { return productCount; }
    public void setProductCount(long productCount) { this.productCount = productCount; }
    public long getOrderCount() { return orderCount; }
    public void setOrderCount(long orderCount) { this.orderCount = orderCount; }
    public long getReviewCount() { return reviewCount; }
    public void setReviewCount(long reviewCount) { this.reviewCount = reviewCount; }
    public long getPaidOrderCount() { return paidOrderCount; }
    public void setPaidOrderCount(long paidOrderCount) { this.paidOrderCount = paidOrderCount; }
    public long getPendingShipCount() { return pendingShipCount; }
    public void setPendingShipCount(long pendingShipCount) { this.pendingShipCount = pendingShipCount; }
    public long getGoodReviewCount() { return goodReviewCount; }
    public void setGoodReviewCount(long goodReviewCount) { this.goodReviewCount = goodReviewCount; }
    public long getPendingReplyCount() { return pendingReplyCount; }
    public void setPendingReplyCount(long pendingReplyCount) { this.pendingReplyCount = pendingReplyCount; }
    public List<Integer> getRatingCounts() { return ratingCounts; }
    public void setRatingCounts(List<Integer> ratingCounts) { this.ratingCounts = ratingCounts; }
    public List<String> getOrderTrendLabels() { return orderTrendLabels; }
    public void setOrderTrendLabels(List<String> orderTrendLabels) { this.orderTrendLabels = orderTrendLabels; }
    public List<Integer> getOrderTrendCounts() { return orderTrendCounts; }
    public void setOrderTrendCounts(List<Integer> orderTrendCounts) { this.orderTrendCounts = orderTrendCounts; }
}
