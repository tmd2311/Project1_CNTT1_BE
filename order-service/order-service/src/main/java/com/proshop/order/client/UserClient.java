package com.proshop.order.client;

import com.proshop.order.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8081/api/v1/users")
public interface UserClient {
    @GetMapping("/{id}")
    UserResponse getUserById(@PathVariable("id") long user_Id);
}
