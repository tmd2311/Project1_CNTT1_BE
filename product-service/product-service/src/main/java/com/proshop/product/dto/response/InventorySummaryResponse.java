package com.proshop.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventorySummaryResponse {
    private Long totalProducts;
    private Long lowStock;
    private Long outOfStock;
    private Integer lowStockThreshold;
}
