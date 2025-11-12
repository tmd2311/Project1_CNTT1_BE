package com.proshop.order.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.proshop.order.entity.OrderStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private long userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderItemRequest> items;
    private String shippingAddress;
}


