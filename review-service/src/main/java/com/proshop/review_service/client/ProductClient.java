package com.proshop.review_service.client;

import com.proshop.review_service.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Example: Call product-service to get product details
@FeignClient(name = "product-service", url = "${product-service.url:http://localhost:8082}")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductResponse getProductById(@PathVariable Long id);
}