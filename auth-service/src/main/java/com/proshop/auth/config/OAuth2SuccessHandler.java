package com.proshop.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proshop.auth.dto.response.LoginResponse;
import com.proshop.auth.entity.DomainEntity;
import com.proshop.auth.entity.SocialProviderEntity;
import com.proshop.auth.entity.UserEntity;
import com.proshop.auth.mapper.LoginMapper;
import com.proshop.auth.repository.DomainRepository;
import com.proshop.auth.repository.SocialProviderRepository;
import com.proshop.auth.repository.UserRepository;
import com.proshop.auth.service.JwtAuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
  private final UserRepository userRepository;
  private final SocialProviderRepository providerRepository;
  private final JwtAuthService jwtAuthService;
  private final DomainRepository domainRepository;
  private final LoginMapper loginMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication)
      throws IOException, ServletException {
    OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
    OAuth2User oAuth2User = oauthToken.getPrincipal();

    String email = oAuth2User.getAttribute("email");
    String name = oAuth2User.getAttribute("name");
    String picture = oAuth2User.getAttribute("picture");
    String provider = oauthToken.getAuthorizedClientRegistrationId();

    String providerCode = oAuth2User.getAttribute("sub");

    log.info("email: " + email + "name: "+ name + "picture: " + picture + "provider: " + provider + "providerCode: " + providerCode);

    UserEntity user = userRepository.findByEmail(email).orElse(null);

    if (user == null) {
      user = new UserEntity();
      user.setEmail(email);
      user.setAccount(email);
      user.setFullName(name);
      user.setAvatarUrl(picture);

      long count = userRepository.count();
      long nextId = count + 1;
      user.setCode(String.format("USER_%03d", nextId));
      user.setCreatedDate(LocalDateTime.now());
      user = userRepository.save(user);
    } else {
      boolean updated = false;
      if (name != null && !name.equals(user.getFullName())) {
        user.setFullName(name);
        updated = true;
      }
      if (picture != null && !picture.equals(user.getAvatarUrl())) {
        user.setAvatarUrl(picture);
        updated = true;
      }
      if (updated) {
        user.setModifiedDate(LocalDateTime.now());
        user.setModifiedBy(user.getCode());
        user = userRepository.save(user);
      }
    }
    SocialProviderEntity sp = providerRepository
        .findByUserEntityAndProviderName(user, provider)
        .orElse(null);

    if (sp == null) {
      sp = new SocialProviderEntity();
      sp.setProviderName(provider);
      sp.setProviderCode(providerCode);
      sp.setBase(provider + ".com");
      sp.setUserEntity(user);
      sp.setCreatedDate(LocalDateTime.now());
    } else {
      boolean updated = false;
      if (!providerCode.equals(sp.getProviderCode())) {
        sp.setProviderCode(providerCode);
        updated = true;
      }
      String newBase = provider + ".com";
      if (!newBase.equals(sp.getBase())) {
        sp.setBase(newBase);
        updated = true;
      }
      if (updated) {
        sp.setModifiedDate(LocalDateTime.now());
        sp.setModifiedBy(user.getCode());
      }
    }
    providerRepository.save(sp);

    user.setLastLogin(LocalDateTime.now());
    user.setModifiedDate(LocalDateTime.now());
    user.setModifiedBy(user.getCode());
    userRepository.save(user);

    LoginResponse loginResponse = makeLoginResponse(user, provider);


    String json = new ObjectMapper().writeValueAsString(loginResponse);
    String encoded = URLEncoder.encode(json, StandardCharsets.UTF_8);

    String redirectUrl = "http://localhost:3000/oauth2/callback?data=" + encoded;
    response.sendRedirect(redirectUrl);
  }

  public LoginResponse makeLoginResponse(UserEntity entity, String provider) {
    Map<String, Object> info = new HashMap<>();
    info.put("user_code", entity.getCode());
    info.put("phone", entity.getPhone());
    info.put("email", entity.getEmail());
    info.put("provider", provider);

      // create new auth token with provider
      Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
    if (currentAuthentication != null) {
      UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
          currentAuthentication.getPrincipal(),
          null,
          currentAuthentication.getAuthorities()
      );
      newAuth.setDetails(Map.of("provider", provider));
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
}

