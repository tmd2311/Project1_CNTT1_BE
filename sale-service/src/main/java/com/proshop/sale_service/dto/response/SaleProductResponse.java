package com.proshop.sale_service.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SaleProductResponse {

    private Long id;
    private Long saleId;

    private Long productId;
    private UUID skuId;
    private Long categoryId;
    private Long brandId;

    private BigDecimal originalPrice;
    private BigDecimal salePrice;
    private BigDecimal discountAmount;

    private Boolean isApplied;
    private LocalDateTime appliedAt;
    private LocalDateTime revertedAt;

    private LocalDateTime createdAt;
}
