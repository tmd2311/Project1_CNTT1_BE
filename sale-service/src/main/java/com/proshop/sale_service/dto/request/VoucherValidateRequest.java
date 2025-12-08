package com.proshop.sale_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VoucherValidateRequest {

    @NotNull(message = "Voucher code is required")
    private String code;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Order value is required")
    private BigDecimal orderValue;
}
