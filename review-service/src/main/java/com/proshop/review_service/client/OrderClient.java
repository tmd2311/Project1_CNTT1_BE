package com.proshop.review_service.client;

import com.proshop.review_service.config.FeignClientConfig;
import com.proshop.review_service.dto.response.GeneralResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "order-service",
        url = "${order.service.url:http://localhost:8083}",
        configuration = FeignClientConfig.class
)
public interface OrderClient {

    @GetMapping("/api/order/check-purchase")
    GeneralResponse<Boolean> checkUserPurchase(@RequestParam("productId") UUID productId);
}
