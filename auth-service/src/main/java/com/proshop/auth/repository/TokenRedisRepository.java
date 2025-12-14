package com.proshop.auth.repository;

import static com.proshop.auth.utils.constant.ServiceConstants.REDIS_REFRESH_TOKEN_PREFIX;
import static com.proshop.auth.utils.constant.ServiceConstants.REDIS_TOKEN_PREFIX;

import com.proshop.auth.redis.RefreshTokenEntity;
import com.proshop.auth.redis.TokenEntity;
import com.proshop.auth.utils.JsonUtils;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TokenRedisRepository {

  private static final int MAX_DEVICE = 10;
  private final RedisTemplate<String, String> redisTemplate;

  @Value("${spring.data.redis.expired-time}")
  private Integer delta;

  @Value("${jwt.refresh-expiration}")
  private Long refreshExpirationMillis;

  public void add (String id, String token) {
    String key = REDIS_TOKEN_PREFIX + id;
    Long size = redisTemplate.opsForList().size(key);
    redisTemplate.opsForList().leftPush(key, token);
    redisTemplate.expire(key, delta, TimeUnit.MINUTES);
    if (Objects.nonNull(size) && size >= MAX_DEVICE) {
      redisTemplate.opsForList().rightPop(key);
    }
  }

  public boolean deleteToken (String id, String token) {
    String key = REDIS_TOKEN_PREFIX + id;
    List<String> tokenJsonList = redisTemplate.opsForList().range(key, 0, -1);
    if (tokenJsonList == null || tokenJsonList.isEmpty()) {
      throw new ResException(ResErrorCode.TOKEN_NOT_FOUND);
    }
    for (String tokenJson : tokenJsonList) {
      Optional<TokenEntity> optionalTokenEntity = JsonUtils.jsonToObject(tokenJson, TokenEntity.class);
      if (optionalTokenEntity.isPresent()) {
        TokenEntity tokenEntity = optionalTokenEntity.get();
        if (token.equals(tokenEntity.getToken())) {
          Long removed = redisTemplate.opsForList().remove(key, 1, tokenJson);
          return removed != null && removed > 0;
        }
      }
    }
  return false;
  }

  public Boolean exitsToken (String userId, String token) {
    String key = REDIS_TOKEN_PREFIX + userId;
    List<String> tokenJsonList = redisTemplate.opsForList().range(key, 0, -1);
    if (tokenJsonList == null || tokenJsonList.isEmpty()) {
      return false;
    }
    for (String tokenJson : tokenJsonList) {
      Optional<TokenEntity> optionalTokenEntity = JsonUtils.jsonToObject(tokenJson, TokenEntity.class);
      if (optionalTokenEntity.isPresent()) {
        TokenEntity tokenEntity = optionalTokenEntity.get();
        if (token.equals(tokenEntity.getToken())) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean deleteAllTokens (String userId) {
    String key = REDIS_TOKEN_PREFIX + userId;
    Boolean result = redisTemplate.delete(key);
    return Boolean.TRUE.equals(result);
  }

  public void updateToken (String userId, String token) {
    if (Boolean.TRUE.equals(deleteToken(userId, token))) {
      add(String.valueOf(userId), token);
    }
  }

  // ==================== REFRESH TOKEN METHODS ====================

  /**
   * Add refresh token to Redis with 7-day expiration
   * @param userCode User code
   * @param refreshToken Refresh token JSON
   */
  public void addRefreshToken(String userCode, String refreshToken) {
    String key = REDIS_REFRESH_TOKEN_PREFIX + userCode;
    Long size = redisTemplate.opsForList().size(key);
    redisTemplate.opsForList().leftPush(key, refreshToken);
    // Convert milliseconds to minutes for Redis expiration
    long refreshExpirationMinutes = refreshExpirationMillis / 60000;
    redisTemplate.expire(key, refreshExpirationMinutes, TimeUnit.MINUTES);

    // Limit to MAX_DEVICE refresh tokens per user
    if (Objects.nonNull(size) && size >= MAX_DEVICE) {
      redisTemplate.opsForList().rightPop(key);
    }
  }

  /**
   * Get refresh token entity from Redis
   * @param userCode User code
   * @param refreshToken Refresh token string to find
   * @return Optional of RefreshTokenEntity
   */
  public Optional<RefreshTokenEntity> getRefreshToken(String userCode, String refreshToken) {
    String key = REDIS_REFRESH_TOKEN_PREFIX + userCode;
    List<String> tokenJsonList = redisTemplate.opsForList().range(key, 0, -1);

    if (tokenJsonList == null || tokenJsonList.isEmpty()) {
      return Optional.empty();
    }

    for (String tokenJson : tokenJsonList) {
      Optional<RefreshTokenEntity> optionalEntity = JsonUtils.jsonToObject(tokenJson, RefreshTokenEntity.class);
      if (optionalEntity.isPresent()) {
        RefreshTokenEntity entity = optionalEntity.get();
        if (refreshToken.equals(entity.getRefreshToken())) {
          return Optional.of(entity);
        }
      }
    }
    return Optional.empty();
  }

  /**
   * Check if refresh token exists in Redis
   * @param userCode User code
   * @param refreshToken Refresh token string
   * @return true if exists, false otherwise
   */
  public Boolean existsRefreshToken(String userCode, String refreshToken) {
    return getRefreshToken(userCode, refreshToken).isPresent();
  }

  /**
   * Delete specific refresh token from Redis
   * @param userCode User code
   * @param refreshToken Refresh token string to delete
   * @return true if deleted successfully, false otherwise
   */
  public boolean deleteRefreshToken(String userCode, String refreshToken) {
    String key = REDIS_REFRESH_TOKEN_PREFIX + userCode;
    List<String> tokenJsonList = redisTemplate.opsForList().range(key, 0, -1);

    if (tokenJsonList == null || tokenJsonList.isEmpty()) {
      throw new ResException(ResErrorCode.TOKEN_NOT_FOUND);
    }

    for (String tokenJson : tokenJsonList) {
      Optional<RefreshTokenEntity> optionalEntity = JsonUtils.jsonToObject(tokenJson, RefreshTokenEntity.class);
      if (optionalEntity.isPresent()) {
        RefreshTokenEntity entity = optionalEntity.get();
        if (refreshToken.equals(entity.getRefreshToken())) {
          Long removed = redisTemplate.opsForList().remove(key, 1, tokenJson);
          return removed != null && removed > 0;
        }
      }
    }
    return false;
  }

  /**
   * Delete all refresh tokens for a user
   * @param userCode User code
   * @return true if deleted successfully, false otherwise
   */
  public boolean deleteAllRefreshTokens(String userCode) {
    String key = REDIS_REFRESH_TOKEN_PREFIX + userCode;
    Boolean result = redisTemplate.delete(key);
    return Boolean.TRUE.equals(result);
  }
}
