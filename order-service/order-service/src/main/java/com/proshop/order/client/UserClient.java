package com.proshop.order.client;

import com.proshop.order.config.FeignClientConfig;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        url = "${user.service.url:http://localhost:8081}",
        configuration = FeignClientConfig.class
)
public interface UserClient {

    @GetMapping("/api/v1/users/{id}")
    GeneralResponse<UserInfoResponse> getUserById(@PathVariable("id") Long id);
}