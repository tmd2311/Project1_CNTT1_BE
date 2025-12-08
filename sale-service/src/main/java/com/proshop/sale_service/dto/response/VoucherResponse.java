package com.proshop.sale_service.dto.response;

import com.proshop.sale_service.util.enums.DiscountType;
import com.proshop.sale_service.util.enums.PromotionStatus;
import com.proshop.sale_service.util.enums.VoucherType;
import com.proshop.sale_service.util.enums.VoucherUserScope;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VoucherResponse {

    private Long id;
    private String code;
    private String name;
    private String description;

    private VoucherType voucherType;
    private DiscountType discountType;
    private BigDecimal discountValue;

    private BigDecimal minOrderValue;
    private BigDecimal maxDiscountAmount;

    private Integer totalQuantity;
    private Integer usedCount;
    private Integer remainingQuantity;
    private Integer usageLimitPerUser;

    private VoucherUserScope userScope;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private PromotionStatus status;
    private Boolean isActive;

    private String bannerImageUrl;
    private String thumbnailImageUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Helper method
    public Integer getRemainingQuantity() {
        return totalQuantity - usedCount;
    }
}
