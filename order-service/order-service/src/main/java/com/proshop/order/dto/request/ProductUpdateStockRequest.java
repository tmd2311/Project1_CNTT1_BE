package com.proshop.order.dto.request;

import lombok.Data;

@Data
public class ProductUpdateStockRequest {
    private Integer stock; // Positive to add, negative to deduct
}
