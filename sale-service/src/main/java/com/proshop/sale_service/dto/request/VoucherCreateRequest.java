package com.proshop.sale_service.dto.request;

import com.proshop.sale_service.util.enums.DiscountType;
import com.proshop.sale_service.util.enums.VoucherType;
import com.proshop.sale_service.util.enums.VoucherUserScope;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VoucherCreateRequest {

    @NotNull(message = "Code is required")
    private String code;

    @NotNull(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Voucher type is required")
    private VoucherType voucherType;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be positive")
    private BigDecimal discountValue;

    private BigDecimal minOrderValue;

    private BigDecimal maxDiscountAmount;

    @NotNull(message = "Total quantity is required")
    @Positive(message = "Total quantity must be positive")
    private Integer totalQuantity;

    private Integer usageLimitPerUser = 1;

    @NotNull(message = "User scope is required")
    private VoucherUserScope userScope;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    private String bannerImageUrl;

    private String thumbnailImageUrl;
}
