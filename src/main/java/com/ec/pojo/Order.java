package com.ec.pojo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String orderNo;
    private Long userId;
    private Long addressId;
    private BigDecimal totalAmount;
    private String status;
    private String paymentMethod;
    private String shippingStatus;
    private Long couponId;
    private BigDecimal discountAmount;
    private String remark;
    private Integer itemCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}