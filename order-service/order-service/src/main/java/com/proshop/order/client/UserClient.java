package com.proshop.order.client;

import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.ProductResponse;
import com.proshop.order.dto.response.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UserClient {

    @GetMapping("/api/v1/{id}")
    GeneralResponse<UserInfoResponse> getUserById(@PathVariable Long id);

}