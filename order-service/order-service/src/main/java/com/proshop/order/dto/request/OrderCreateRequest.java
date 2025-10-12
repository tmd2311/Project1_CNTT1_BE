package com.proshop.order.dto.request;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequest {

    private long userId;   // id người dùng

    private List<OrderItemRequest> items; // danh sách sản phẩm

    private String paymentMethod; // phương thức thanh toán (COD, CREDIT_CARD, ...)
}
