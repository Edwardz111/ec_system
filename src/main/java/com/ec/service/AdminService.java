package com.ec.service;

import com.ec.mapper.AdminMapper;
import com.ec.mapper.AdminStatsMapper;
import com.ec.mapper.ProductReviewMapper;
import com.ec.mapper.UserMapper;
import com.ec.pojo.*;
import com.ec.pojo.vo.AdminDashboardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AdminStatsMapper adminStatsMapper;
    @Autowired
    private ProductReviewMapper productReviewMapper;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;

    public Admin login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) return null;
        if (user.getPassword() == null || !user.getPassword().equals(password)) return null;
        String st = user.getStatus();
        if (st == null || !"ACTIVE".equalsIgnoreCase(st)) return null;

        Admin admin = adminMapper.findByUserId(user.getId());
        if (admin == null || !admin.isManager()) return null;

        admin.setUsername(user.getUsername());
        return admin;
    }

    public List<User> findAllUsers() {
        return adminMapper.findAllUsers();
    }

    public List<User> searchUsers(String username, String status, Integer vip) {
        return adminMapper.searchUsers(username, status, vip);
    }

    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    public boolean cancelVip(Long userId) {
        return userMapper.updateVipLevel(userId, 0) > 0;
    }

    public boolean setVip(Long userId) {
        return userMapper.updateVipLevel(userId, 1) > 0;
    }

    public boolean banUser(Long userId) {
        return userMapper.updateStatus(userId, "BANNED") > 0;
    }

    public Admin getAdminRecord(Long userId) {
        return adminMapper.findByUserId(userId);
    }

    /**
     * 设为管理员（ADMIN）或超级管理员（SUPER_ADMIN）
     */
    public String grantAdminRole(Long targetUserId, String permissionLevel, Admin operator) {
        if (targetUserId == null || userMapper.selectById(targetUserId) == null) {
            return "用户不存在";
        }
        if (!"ADMIN".equals(permissionLevel) && !"SUPER_ADMIN".equals(permissionLevel)) {
            return "无效的管理员级别";
        }
        if ("SUPER_ADMIN".equals(permissionLevel) && !isSuperAdmin(operator)) {
            return "仅超级管理员可授予超级管理员权限";
        }
        if (operator != null && targetUserId.equals(operator.getUserId())) {
            return "不能修改自己的管理员权限，请联系其他超级管理员";
        }

        Admin existing = adminMapper.findByUserId(targetUserId);
        if (existing != null) {
            if (adminMapper.updatePermissionLevel(targetUserId, permissionLevel) > 0) {
                return null;
            }
            return "更新管理员权限失败";
        }
        Admin record = new Admin();
        record.setUserId(targetUserId);
        record.setPermissionLevel(permissionLevel);
        return adminMapper.insert(record) > 0 ? null : "设置管理员失败";
    }

    /** 解除管理员（删除 admin 表记录） */
    public String revokeAdminRole(Long targetUserId, Admin operator) {
        if (targetUserId == null) {
            return "用户不存在";
        }
        Admin target = adminMapper.findByUserId(targetUserId);
        if (target == null || !target.isManager()) {
            return "该用户不是管理员";
        }
        if (operator != null && targetUserId.equals(operator.getUserId())) {
            return "不能解除自己的管理员权限";
        }
        if ("SUPER_ADMIN".equals(target.getPermissionLevel()) && !isSuperAdmin(operator)) {
            return "仅超级管理员可解除超级管理员权限";
        }
        return adminMapper.deleteByUserId(targetUserId) > 0 ? null : "解除管理员失败";
    }

    public boolean isSuperAdmin(Admin admin) {
        return admin != null && "SUPER_ADMIN".equals(admin.getPermissionLevel());
    }

    public AdminDashboardVo getDashboardStats() {
        AdminDashboardVo vo = new AdminDashboardVo();
        vo.setUserCount(adminStatsMapper.countUsers());
        vo.setProductCount(adminStatsMapper.countProducts());
        vo.setOrderCount(adminStatsMapper.countOrders());
        vo.setReviewCount(adminStatsMapper.countReviews());
        vo.setPaidOrderCount(adminStatsMapper.countPaidOrders());
        vo.setPendingShipCount(adminStatsMapper.countPendingShipOrders());
        vo.setGoodReviewCount(adminStatsMapper.countGoodReviews());
        vo.setPendingReplyCount(adminStatsMapper.countPendingReply());

        int[] ratingArr = new int[5];
        List<Map<String, Object>> ratingRows = adminStatsMapper.countByRating();
        if (ratingRows != null) {
            for (Map<String, Object> row : ratingRows) {
                Object r = row.get("rating");
                Object c = row.get("cnt");
                if (r != null && c != null) {
                    int rating = ((Number) r).intValue();
                    if (rating >= 1 && rating <= 5) {
                        ratingArr[rating - 1] = ((Number) c).intValue();
                    }
                }
            }
        }
        vo.setRatingCounts(Arrays.asList(
                ratingArr[0], ratingArr[1], ratingArr[2], ratingArr[3], ratingArr[4]));

        List<String> labels = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        Map<LocalDate, Integer> dayCountMap = new HashMap<>();
        List<Map<String, Object>> trendRows = adminStatsMapper.orderCountLast7Days();
        if (trendRows != null) {
            for (Map<String, Object> row : trendRows) {
                Object month = row.get("month");
                Object cnt = row.get("cnt");
                if (month== null || cnt == null) continue;
                LocalDate ld;
                if (month instanceof java.sql.Date) {
                    ld = ((java.sql.Date) month).toLocalDate();
                } else if (month instanceof LocalDate) {
                    ld = (LocalDate) month;
                } else {
                    ld = LocalDate.parse(month.toString().substring(0, 10));
                }
                dayCountMap.put(ld, ((Number) cnt).intValue());
            }
        }
        for (int i = 6; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusDays(i);
            labels.add(d.format(fmt));
            counts.add(dayCountMap.getOrDefault(d, 0));
        }
        vo.setOrderTrendLabels(labels);
        vo.setOrderTrendCounts(counts);
        return vo;
    }

    public List<Product> listProducts() {
        return productService.getAllProducts();
    }

    public List<Order> listOrders() {
        return orderService.getAllOrders();
    }

    public List<ProductReview> listReviews() {
        return productReviewMapper.findAll();
    }

    public List<ProductReview> searchReviews(Long userId, Long orderId, Long productId,
                                             Integer rating, String status) {
        boolean hasFilter = userId != null || orderId != null || productId != null
                || rating != null || (status != null && !status.isEmpty());
        if (!hasFilter) {
            return productReviewMapper.findAll();
        }
        return productReviewMapper.searchReviews(userId, orderId, productId, rating, status);
    }

    public String replyReview(Long reviewId, String reply) {
        if (reviewId == null) {
            return "评价不存在";
        }
        ProductReview review = productReviewMapper.selectById(reviewId);
        if (review == null) {
            return "评价不存在";
        }
        if (reply == null || reply.trim().isEmpty()) {
            return "回复内容不能为空";
        }
        String trimmed = reply.trim();
        if (trimmed.length() > 1000) {
            return "回复内容不能超过1000字";
        }
        return productReviewMapper.updateReplyById(reviewId, trimmed) > 0 ? null : "回复失败";
    }

    public String toggleReviewVisibility(Long reviewId) {
        if (reviewId == null) {
            return "评价不存在";
        }
        ProductReview review = productReviewMapper.selectById(reviewId);
        if (review == null) {
            return "评价不存在";
        }
        String newStatus = "VISIBLE".equals(review.getStatus()) ? "HIDDEN" : "VISIBLE";
        return productReviewMapper.updateStatusById(reviewId, newStatus) > 0 ? null : "操作失败";
    }

    public String deleteReviewPermanently(Long reviewId) {
        if (reviewId == null) {
            return "评价不存在";
        }
        if (productReviewMapper.selectById(reviewId) == null) {
            return "评价不存在";
        }
        return productReviewMapper.deleteById(reviewId) > 0 ? null : "删除失败";
    }

    public static Long parseIdParam(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer parseRatingParam(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            int r = Integer.parseInt(value.trim());
            if (r >= 1 && r <= 5) {
                return r;
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    public static String buildReviewSearchQuery(Long userId, Long orderId, Long productId,
                                                Integer rating, String status) {
        StringBuilder q = new StringBuilder();
        appendQuery(q, "userId", userId);
        appendQuery(q, "orderId", orderId);
        appendQuery(q, "productId", productId);
        appendQuery(q, "rating", rating);
        if (status != null && !status.isEmpty()) {
            appendQuery(q, "status", status);
        }
        return q.length() > 0 ? "?" + q : "";
    }

    private static void appendQuery(StringBuilder q, String key, Object value) {
        if (value == null) {
            return;
        }
        if (q.length() > 0) {
            q.append('&');
        }
        q.append(key).append('=').append(value);
    }

    public Order getOrderDetail(Long id) {
        return orderService.getOrderById(id);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderService.getOrderItems(orderId);
    }

    public List<OrderItem> getOrderItemsWithProductName(Long orderId) {
        List<OrderItem> items = orderService.getOrderItems(orderId);
        if (items == null || items.isEmpty()) {
            return items != null ? items : java.util.Collections.emptyList();
        }
        for (OrderItem item : items) {
            if (item.getProductName() == null || item.getProductName().isEmpty()) {
                Product product = productService.getProductById(item.getProductId());
                if (product != null) {
                    item.setProductName(product.getName());
                }
            }
        }
        return items;
    }
}
