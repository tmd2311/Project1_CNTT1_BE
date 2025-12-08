package com.proshop.sale_service.dto.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AddProductsToSaleRequest {

    /**
     * Danh sách Product IDs (nếu apply cho toàn bộ product)
     */
    private List<UUID> productIds;

    /**
     * Danh sách SKU IDs (nếu chỉ apply cho SKU cụ thể)
     */
    private List<UUID> skuIds;

    /**
     * Danh sách Category IDs (nếu apply theo category)
     */
    private List<UUID> categoryIds;

    /**
     * Danh sách Brand IDs (nếu apply theo brand)
     */
    private List<UUID> brandIds;
}
