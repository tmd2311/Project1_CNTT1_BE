package com.proshop.order.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.proshop.order.entity.OrderStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private UUID userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderItemRequest> items;
}


