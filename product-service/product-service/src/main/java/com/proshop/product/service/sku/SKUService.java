// SKUService.java
package com.proshop.product.service.sku;

import com.proshop.product.dto.request.SKURequest;
import com.proshop.product.dto.request.SKUstockRequest;
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
}
