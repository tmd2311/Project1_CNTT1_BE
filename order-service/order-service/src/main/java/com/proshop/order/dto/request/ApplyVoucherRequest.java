package com.proshop.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyVoucherRequest {
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String voucherCode;
}

