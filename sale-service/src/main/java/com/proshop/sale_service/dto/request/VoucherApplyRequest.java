package com.proshop.sale_service.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VoucherApplyRequest {

    @NotNull(message = "Voucher code is required")
    private String code;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotNull(message = "Order value is required")
    private BigDecimal orderValue;
}
