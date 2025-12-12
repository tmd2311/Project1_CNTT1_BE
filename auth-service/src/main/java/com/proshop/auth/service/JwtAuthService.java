package com.proshop.auth.service;

import com.proshop.auth.entity.UserEntity;
import com.proshop.auth.entity.UserRoleEntity;
import com.proshop.auth.redis.RefreshTokenEntity;
import com.proshop.auth.redis.TokenEntity;
import com.proshop.auth.repository.TokenRedisRepository;
import com.proshop.auth.utils.JsonUtils;
import com.proshop.auth.utils.JwtUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtAuthService {
  private final JwtUtil jwtUtil;

  private final TokenRedisRepository redisRepository;

  @Value("${jwt.refresh-expiration}")
  private Long refreshExpirationMillis;


  public String generateAndStoreToken(UserEntity userEntity, Map<String, Object> claims ){
    String token = jwtUtil.generateToken(userEntity.getAccount(),claims);
    TokenEntity tokenEntity = new TokenEntity();
    List<String> roles = new ArrayList<>();
    for (UserRoleEntity userRole : userEntity.getUserRoles()) {
      roles.add(userRole.getRoleEntity().getCode());
    }
    tokenEntity.setRoles(roles);
    tokenEntity.setToken(token);
    tokenEntity.setStatus(userEntity.getStatus());
    tokenEntity.setDeleted(userEntity.getDeleted());
    redisRepository.add(userEntity.getCode(), JsonUtils.objectToJson(tokenEntity));
    return token;
  }

  /**
   * Generate and store both access token and refresh token
   * @param userEntity User entity
   * @param claims JWT claims
   * @return TokenPair containing access token and refresh token
   */
  public TokenPair generateAndStoreTokenPair(UserEntity userEntity, Map<String, Object> claims) {
    // Extract roles
    List<String> roles = new ArrayList<>();
    for (UserRoleEntity userRole : userEntity.getUserRoles()) {
      roles.add(userRole.getRoleEntity().getCode());
    }

    // Generate access token
    String accessToken = jwtUtil.generateToken(userEntity.getAccount(), claims);
    TokenEntity tokenEntity = new TokenEntity();
    tokenEntity.setRoles(roles);
    tokenEntity.setToken(accessToken);
    tokenEntity.setStatus(userEntity.getStatus());
    tokenEntity.setDeleted(userEntity.getDeleted());
    redisRepository.add(userEntity.getCode(), JsonUtils.objectToJson(tokenEntity));

    // Generate refresh token
    String refreshToken = jwtUtil.generateRefreshToken(userEntity.getAccount(), claims);
    Date refreshExpiryDate = new Date(System.currentTimeMillis() + refreshExpirationMillis);
    RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity(
        refreshToken,
        userEntity.getCode(),
        roles,
        refreshExpiryDate
    );
    redisRepository.addRefreshToken(userEntity.getCode(), JsonUtils.objectToJson(refreshTokenEntity));

    return new TokenPair(accessToken, refreshToken);
  }

  /**
   * Inner class to hold access token and refresh token pair
   */
  @Data
  @AllArgsConstructor
  public static class TokenPair {
    private String accessToken;
    private String refreshToken;
  }
}
