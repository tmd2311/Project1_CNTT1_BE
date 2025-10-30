package com.proshop.order.client;

import com.proshop.order.dto.request.ProductUpdateStockRequest;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.ProductResponse;
import com.proshop.order.dto.response.SKUResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "product-service", url = "http://product-service:8082")
public interface ProductClient {

    @GetMapping("/api/product/{id}")
    GeneralResponse<ProductResponse> getProductById(@PathVariable("id") UUID productId);
    // Đổi return type thành GeneralResponse<ProductResponse>
    @GetMapping("/api/sku/{id}")
    GeneralResponse<SKUResponse> getSkuById(@PathVariable("id") UUID skuId);

    @PutMapping("/api/sku/stock/{id}")
    GeneralResponse<SKUResponse> updateProductStock(
            @PathVariable("id") UUID skuId,
            @RequestBody ProductUpdateStockRequest request);
}
