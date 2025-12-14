package com.proshop.auth.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class LoginResponse {

  private String token;
  private String refreshToken;
  private Long expiresIn;
  private String code;
  private String account;
  private String phone;
  private String fullName;
  private String email;
  private String avatarUrl;
  private boolean isFirstLogin = false;
  private List<String> roleNames;
}

