package com.proshop.sale_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherValidationResponse {

    private Boolean isValid;
    private String message;

    private VoucherResponse voucher;

    private BigDecimal discountAmount;
    private BigDecimal finalOrderValue;

    public static VoucherValidationResponse valid(VoucherResponse voucher, BigDecimal discountAmount, BigDecimal finalOrderValue) {
        return VoucherValidationResponse.builder()
            .isValid(true)
            .message("Voucher hợp lệ")
            .voucher(voucher)
            .discountAmount(discountAmount)
            .finalOrderValue(finalOrderValue)
            .build();
    }

    public static VoucherValidationResponse invalid(String message) {
        return VoucherValidationResponse.builder()
            .isValid(false)
            .message(message)
            .build();
    }
}
