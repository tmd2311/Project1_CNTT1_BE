package com.proshop.auth.repository;

import static com.proshop.auth.utils.constant.ServiceConstants.REDIS_TOKEN_PREFIX;

import com.proshop.auth.exceptions.ResException;
import com.proshop.auth.redis.TokenEntity;
import com.proshop.auth.utils.JsonUtils;
import com.proshop.auth.utils.enums.ResErrorCode;
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
}
