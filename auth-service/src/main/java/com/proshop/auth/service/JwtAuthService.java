package com.proshop.auth.service;

import com.proshop.auth.entity.PermissionEntity;
import com.proshop.auth.entity.UserEntity;
import com.proshop.auth.entity.UserRoleEntity;
import com.proshop.auth.redis.SystemPolicy;
import com.proshop.auth.redis.TokenEntity;
import com.proshop.auth.repository.ApiRepository;
import com.proshop.auth.repository.PermissionRepository;
import com.proshop.auth.repository.TokenRedisRepository;
import com.proshop.auth.repository.UserRoleRepository;
import com.proshop.auth.utils.JsonUtils;
import com.proshop.auth.utils.JwtUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtAuthService {
  private final JwtUtil jwtUtil;

  private final PermissionRepository permissionRepository;
  private final UserRoleRepository userRoleRepository;
  private final ApiRepository apiRepository;
  private final TokenRedisRepository redisRepository;


  public String generateAndStoreToken(UserEntity userEntity, Map<String, Object> claims ){
    String token = jwtUtil.generateToken(userEntity.getAccount(),claims);
    TokenEntity tokenEntity = new TokenEntity();
    tokenEntity.setToken(token);
    List<SystemPolicy> systemPolicies = buildSystemPolicy(userEntity);
    tokenEntity.setSystemPolicies(systemPolicies);
    tokenEntity.setStatus(userEntity.getStatus());
    tokenEntity.setDeleted(userEntity.getDeleted());
    redisRepository.add(userEntity.getCode(), JsonUtils.objectToJson(tokenEntity));
    return token;
  }

  private List<SystemPolicy> buildSystemPolicy (UserEntity userEntity){
    List<SystemPolicy> systemPolicies = new ArrayList<>();
    List<UserRoleEntity> userRoleEntities = userRoleRepository.findByUserId(userEntity.getId());
    for (UserRoleEntity userRoleEntity : userRoleEntities) {
      long roleId = userRoleEntity.getRoleEntity().getId();
      SystemPolicy systemPolicy = new SystemPolicy();
      systemPolicy.setRole(userRoleEntity.getRoleEntity().getName());
      systemPolicy.setDomain(userRoleEntity.getRoleEntity().getName());
      List<PermissionEntity> permissionEntities = permissionRepository.findPermissionsByRoleId(roleId);
      for (PermissionEntity permissionEntity : permissionEntities) {
        long permissionId = permissionEntity.getId();
        systemPolicy.setPermission(permissionEntity.getAction());
        Set<String> apiPaths = apiRepository.findApisByPermissionId(permissionId);
        systemPolicy.setApis(apiPaths);
        systemPolicies.add(systemPolicy);
      }
    }
    return systemPolicies;
  }
}
