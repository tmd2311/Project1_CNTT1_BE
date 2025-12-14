package com.proshop.review_service.client;

import com.proshop.review_service.config.FeignClientConfig;
import com.proshop.review_service.dto.response.ProductResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Example: Call product-service to get product details
@FeignClient(name = "product-service", url = "${product-service.url:http://localhost:8082}", configuration = FeignClientConfig.class)
public interface ProductClient {

    @GetMapping("/api/product/{id}")
    ProductResponse getProductById(@PathVariable UUID id);
}