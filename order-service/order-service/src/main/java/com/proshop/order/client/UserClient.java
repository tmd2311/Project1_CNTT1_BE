package com.proshop.order.client;

import com.proshop.order.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", url = "http://localhost:8082/api/users")
public interface UserClient {
    @GetMapping("/{id}")
    UserResponse getUserById(@PathVariable("id") long userId);
}
