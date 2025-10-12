package com.proshop.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private UUID id;
    private UUID productId;
    private String productName;  // Optional: from Product Service
    private Double productPrice; // Optional: from Product Service
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}