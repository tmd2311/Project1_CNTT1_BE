package com.proshop.auth.service.auth.impl;

import com.proshop.auth.dto.request.ChangePasswordRequest;
import com.proshop.auth.dto.request.LoginRequest;
import com.proshop.auth.dto.request.LogoutRequest;
import com.proshop.auth.dto.response.LoginResponse;
import com.proshop.auth.dto.response.UserInfoResponse;
import com.proshop.auth.entity.DomainEntity;
import com.proshop.auth.entity.SocialProviderEntity;
import com.proshop.auth.entity.UserEntity;
import com.proshop.auth.exceptions.ResException;
import com.proshop.auth.mapper.LoginMapper;
import com.proshop.auth.repository.DomainRepository;
import com.proshop.auth.repository.SocialProviderRepository;
import com.proshop.auth.repository.UserRepository;
import com.proshop.auth.service.JwtAuthService;
import com.proshop.auth.service.auth.AuthService;
import com.proshop.auth.utils.enums.ResErrorCode;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;

  private final AuthenticationManager authenticationManager;

  private final SocialProviderRepository socialProviderRepository;

  private final JwtAuthService jwtAuthService;

  private final DomainRepository domainRepository;

  private final LoginMapper loginMapper;

  @Override
  @Transactional
  public LoginResponse login(LoginRequest request) {
    validateLoginRequest(request);
    try {
      Authentication authenticate = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              request.getAccount(), request.getPassword()));
      UserEntity user = (UserEntity) authenticate.getPrincipal();
      return makeLoginResponse(user);
    } catch (LockedException ex) {
      throw new ResException(ResErrorCode.ACCOUNT_BLOCKED);
    } catch (DisabledException ex) {
      throw new ResException(ResErrorCode.ACCOUNT_DELETED);
    } catch (BadCredentialsException ex) {
      log.error("Login failed", ex);
      throw new ResException(ResErrorCode.INVALID_USER_PASS);
    }
  }

  @Override
  public LoginResponse makeLoginResponse(UserEntity entity) {
    return makeLoginResponse(entity, null);
  }

  @Override
  public LoginResponse makeLoginResponse(UserEntity entity, String provider) {
    Map<String, Object> info = new HashMap<>();
    info.put("user_code", entity.getCode());
    info.put("phone", entity.getPhone());
    info.put("email", entity.getEmail());

    String loginProvider = provider;
    if (loginProvider == null) {
      loginProvider = getSocialLoginProvider(entity);
    }

    if (loginProvider != null) {
      info.put("isSocialLogin", loginProvider);

      // create new auth token with provider
      Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
      UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
          currentAuthentication.getPrincipal(),
          currentAuthentication.getCredentials(),
          currentAuthentication.getAuthorities()
      );
      newAuth.setDetails(Map.of("provider", loginProvider));
      SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
    List<DomainEntity> domainEntities = domainRepository.findDomainForUser(entity.getId());
    List<String> domainNames = new ArrayList<>();
    if (domainEntities != null && !domainEntities.isEmpty()) {
      domainNames = domainEntities.stream().map(DomainEntity::getName).toList();
    }
    List<String> roleNames = userRepository.getRoleNamesByUserId(entity.getId());
    info.put("domain", domainNames);
    String token = jwtAuthService.generateAndStoreToken(entity, info);
    LoginResponse loginResponse = loginMapper.toDTO(entity);
    loginResponse.setToken(token);
    loginResponse.setRoleNames(roleNames);
    if (entity.getLastLogin() == null) {
      loginResponse.setFirstLogin(true);
    }

    LocalDateTime now = LocalDateTime.now();
    entity.setLastLogin(now);
    entity.setModifiedDate(now);
    entity.setModifiedBy(entity.getCode());
    userRepository.save(entity);
    return loginResponse;
  }

  @Override
  public UserInfoResponse changePassword(ChangePasswordRequest req, String userCode) {
    return null;
  }

  @Override
  public Boolean logout(LogoutRequest req) {
    return null;
  }

  private void validateLoginRequest(LoginRequest request) {
    if (request.getPassword().isEmpty()) {
      throw new ResException(ResErrorCode.INVALID_USER_PASS);
    }
    UserEntity userEntity = userRepository.findByAccount(request.getAccount())
        .orElseThrow(() -> new ResException(ResErrorCode.INVALID_USER_PASS));
    if (Boolean.TRUE.equals(userEntity.getDeleted())) {
      throw new ResException(ResErrorCode.ACCOUNT_DELETED);
    }
  }
  private String getSocialLoginProvider(UserEntity user) {
    return socialProviderRepository.findByUserEntity(user).stream()
        .filter(provider -> !Boolean.TRUE.equals(provider.getDeleted()))
        .findFirst()
        .map(SocialProviderEntity::getProviderName)
        .orElse(null);
  }
}
