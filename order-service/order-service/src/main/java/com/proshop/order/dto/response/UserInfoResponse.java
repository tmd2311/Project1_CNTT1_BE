package com.proshop.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
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