package com.proshop.order.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class PaymentResponse {
    private UUID paymentId;
    private UUID orderId;
    private String method;
    private String status;
    private BigDecimal amount;
    private LocalDateTime paidAt;
}
