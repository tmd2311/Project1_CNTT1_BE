package com.proshop.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenResponse {

  private String accessToken;  // New access token
  private String refreshToken;  // New refresh token (optional - can rotate refresh token)
  private Long expiresIn;  // Access token expiration in seconds
  private String tokenType;  // "Bearer"
}
