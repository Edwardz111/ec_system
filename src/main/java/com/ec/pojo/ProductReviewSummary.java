package com.ec.pojo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductReviewSummary {
    private Long productId;
    private BigDecimal avgScore;
    private Long reviewCount;
}
