package com.proshop.order.dto.request;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequest {

    private long userId;   // id người dùng

    private List<OrderItemRequest> items; // danh sách sản phẩm

    private String paymentMethod; // phương thức thanh toán (COD, CREDIT_CARD, ...)

    private String shippingAddress;
}
