package com.proshop.sale_service.dto.request;

import com.proshop.sale_service.util.enums.SaleApplyScope;
import com.proshop.sale_service.util.enums.SaleType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SaleCreateRequest {

    @NotNull(message = "Code is required")
    private String code;

    @NotNull(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Sale type is required")
    private SaleType saleType;

    @NotNull(message = "Sale value is required")
    @Positive(message = "Sale value must be positive")
    private BigDecimal saleValue;

    @NotNull(message = "Apply scope is required")
    private SaleApplyScope applyScope;

    private Integer minPurchaseQuantity = 1;

    private BigDecimal maxDiscountAmount;

    private Integer quantity; // null = unlimited

    private BigDecimal minOrderValue;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    private Integer priority = 0;

    private String bannerImageUrl;

    private String thumbnailImageUrl;
}
