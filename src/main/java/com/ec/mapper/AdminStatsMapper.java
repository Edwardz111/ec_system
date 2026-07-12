package com.ec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminStatsMapper {

    @Select("SELECT COUNT(*) FROM users")
    long countUsers();

    @Select("SELECT COUNT(*) FROM products")
    long countProducts();

    @Select("SELECT COUNT(*) FROM orders")
    long countOrders();

    @Select("SELECT COUNT(*) FROM product_reviews")
    long countReviews();

    @Select("SELECT COUNT(*) FROM orders WHERE status IN ('2','PENDING_SHIP','PAID')")
    long countPaidOrders();

    @Select("SELECT COUNT(*) FROM orders WHERE status IN ('2','PENDING_SHIP')")
    long countPendingShipOrders();

    @Select("SELECT COUNT(*) FROM product_reviews WHERE rating >= 4")
    long countGoodReviews();

    @Select("SELECT COUNT(*) FROM product_reviews WHERE (reply IS NULL OR reply = '') AND status = 'VISIBLE'")
    long countPendingReply();

    @Select("SELECT rating, COUNT(*) AS cnt FROM product_reviews GROUP BY rating ORDER BY rating")
    List<Map<String, Object>> countByRating();

    @Select("SELECT DATE(created_at) AS day, COUNT(*) AS cnt FROM orders " +
            "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 3 YEAR) " +
            "GROUP BY DATE(created_at) ORDER BY day")
    List<Map<String, Object>> orderCountLast7Days();
}
