package com.proshop.order.client;

import com.proshop.order.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "product-service", url = "http://localhost:8081")
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    ProductResponse getProduct(@PathVariable("id") UUID productId);
}


