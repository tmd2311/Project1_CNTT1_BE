package com.proshop.order.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private UUID orderId;
    private long userId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private String shippingAddress;
}

