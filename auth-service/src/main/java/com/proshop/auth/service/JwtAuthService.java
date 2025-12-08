package com.proshop.auth.service;

import com.proshop.auth.entity.UserEntity;
import com.proshop.auth.entity.UserRoleEntity;
import com.proshop.auth.redis.TokenEntity;
import com.proshop.auth.repository.TokenRedisRepository;
import com.proshop.auth.utils.JsonUtils;
import com.proshop.auth.utils.JwtUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtAuthService {
  private final JwtUtil jwtUtil;

  private final TokenRedisRepository redisRepository;


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
}
