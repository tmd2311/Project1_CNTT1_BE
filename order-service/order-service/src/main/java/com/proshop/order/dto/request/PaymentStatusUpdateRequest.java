package com.proshop.order.dto.request;

import lombok.Data;

@Data
public class PaymentStatusUpdateRequest {
    private String status; // PENDING, PROCESSING, PAID, FAILED, CANCELLED
}
