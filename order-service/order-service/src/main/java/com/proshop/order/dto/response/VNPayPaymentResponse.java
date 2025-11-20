package com.proshop.order.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VNPayPaymentResponse {
    private String paymentUrl;
    private UUID orderId;
    private UUID paymentId;
    private BigDecimal amount;
}