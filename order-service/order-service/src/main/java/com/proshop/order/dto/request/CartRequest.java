package com.proshop.order.dto.request;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequest {
    private UUID productId;
    private int quantity;
}

