package com.proshop.sale_service.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherApplyResponse {

    private Long voucherUsageId;
    private Long voucherId;
    private Long userId;
    private UUID orderId;

    private BigDecimal orderValue;
    private BigDecimal discountAmount;
    private BigDecimal finalOrderValue;

    private LocalDateTime appliedAt;
}
