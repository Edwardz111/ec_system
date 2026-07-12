package com.ec.mapper;

import com.ec.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int insert(OrderItem orderItem);

    int insertBatch(@Param("items") List<OrderItem> items);
    List<OrderItem> findByOrderId(Long orderId);
    void deleteByOrderId(Long orderId);
}
