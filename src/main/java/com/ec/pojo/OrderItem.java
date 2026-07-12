package com.ec.pojo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
}
