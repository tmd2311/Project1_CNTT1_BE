package com.proshop.order.dto.request;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequest {

    private UUID productId; // id sản phẩm
    private UUID skuId;
    private int quantity;   // số lượng đặt
}
