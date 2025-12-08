package com.proshop.review_service.client;

import com.proshop.review_service.config.FeignClientConfig;
import com.proshop.review_service.dto.response.AuthResponse;
import com.proshop.review_service.dto.response.GeneralResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        url = "${user.service.url:http://localhost:8081}",
        configuration = FeignClientConfig.class
)
public interface AuthClient {

    @GetMapping("/api/v1/{id}")
    GeneralResponse<AuthResponse> getUserById(@PathVariable("id") Long id);
}

