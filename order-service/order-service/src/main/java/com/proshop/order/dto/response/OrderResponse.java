package com.proshop.order.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {
    private UUID orderId;
    private long userId;
    private BigDecimal totalAmount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal discountAmount;
    private String status;
    private LocalDateTime createdAt;
    private String shippingAddress;

    public OrderResponse(UUID orderId,
            long userId,
            BigDecimal totalAmount,
            String status,
            LocalDateTime createdAt,
            String shippingAddress) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.discountAmount = BigDecimal.ZERO;
        this.status = status;
        this.createdAt = createdAt;
        this.shippingAddress = shippingAddress;
    }

    public OrderResponse(UUID orderId,
            long userId,
            BigDecimal totalAmount,
            BigDecimal discountAmount,
            String status,
            LocalDateTime createdAt,
            String shippingAddress) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.shippingAddress = shippingAddress;
    }
}
