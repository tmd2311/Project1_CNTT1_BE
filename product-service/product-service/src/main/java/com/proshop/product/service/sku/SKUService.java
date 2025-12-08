// SKUService.java
package com.proshop.product.service.sku;

import com.proshop.product.dto.request.SKURequest;
import com.proshop.product.dto.request.SKUstockRequest;
import com.proshop.product.dto.request.UpdateSKUSalePriceRequest;
import com.proshop.product.dto.response.SKUResponse;

import java.util.List;
import java.util.UUID;

public interface SKUService {
    SKUResponse createSKU(SKURequest request);
    SKUResponse updateSKU(UUID id, SKURequest request);
    void deleteSKU(UUID id);
    SKUResponse getById(UUID id);
    List<SKUResponse> getAll();
    SKUResponse updateStockSKU(UUID id, SKUstockRequest request);

    // Sale-related methods
    void updateSalePrice(UUID id, UpdateSKUSalePriceRequest request);
    void revertPrice(UUID id);

    // Get SKUs by product/category/brand
    List<SKUResponse> getSKUsByProductId(UUID productId);
    List<SKUResponse> getSKUsByCategoryId(UUID categoryId);
    List<SKUResponse> getSKUsByBrandId(UUID brandId);
}
