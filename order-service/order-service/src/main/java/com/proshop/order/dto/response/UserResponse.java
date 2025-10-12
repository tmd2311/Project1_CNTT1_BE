package com.proshop.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private long id;
    private String code;
    private String account;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private String currentAddress;
    private LocalDateTime lastLogin;
    private String status;
}
