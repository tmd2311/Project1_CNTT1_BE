package com.proshop.sale_service.dto.response;

import com.proshop.sale_service.util.enums.PromotionStatus;
import com.proshop.sale_service.util.enums.SaleApplyScope;
import com.proshop.sale_service.util.enums.SaleType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SaleResponse {

    private Long id;
    private String code;
    private String name;
    private String description;

    private SaleType saleType;
    private BigDecimal saleValue;

    private SaleApplyScope applyScope;
    private Integer minPurchaseQuantity;
    private BigDecimal maxDiscountAmount;

    private Integer quantity; // null = unlimited
    private Integer usedCount;
    private BigDecimal minOrderValue;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private PromotionStatus status;
    private Boolean isActive;

    private Integer priority;

    private String bannerImageUrl;
    private String thumbnailImageUrl;

    private Integer totalAppliedCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
