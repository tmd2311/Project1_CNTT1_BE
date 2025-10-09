package com.proshop.order.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import com.proshop.order.entity.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private UUID orderId;
    private BigDecimal amount;       // phải có trường này
    private PaymentMethod method;
}


