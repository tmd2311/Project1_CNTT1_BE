package com.proshop.review_service.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserInfoResponse {

    private String code;
    private String account;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private String currentAddress;
    private LocalDate birthday;
    private LocalDateTime lastLogin;
    private String status;
}
