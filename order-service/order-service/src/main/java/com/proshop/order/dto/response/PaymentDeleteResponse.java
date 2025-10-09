package com.proshop.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class PaymentDeleteResponse {
    private UUID paymentId;
    private UUID orderId;
}
