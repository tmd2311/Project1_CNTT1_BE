package com.proshop.auth.service.auth.impl;

import com.proshop.auth.dto.request.ChangePasswordRequest;
import com.proshop.auth.dto.request.LoginRequest;
import com.proshop.auth.dto.request.RegisterRequest;
import com.proshop.auth.dto.request.ResetPasswordRequest;
import com.proshop.auth.dto.request.SendOtpRequest;
import com.proshop.auth.dto.request.VerifyOtpRequest;
import com.proshop.auth.dto.response.LoginResponse;
import com.proshop.auth.dto.response.OtpResponse;
import com.proshop.auth.dto.response.UserInfoResponse;
import com.proshop.auth.entity.DomainEntity;
import com.proshop.auth.entity.SocialProviderEntity;
import com.proshop.auth.entity.UserEntity;
import com.proshop.auth.exceptions.ResException;
import com.proshop.auth.mapper.LoginMapper;
import com.proshop.auth.mapper.UserMapper;
import com.proshop.auth.repository.DomainRepository;
import com.proshop.auth.repository.SocialProviderRepository;
import com.proshop.auth.repository.TokenRedisRepository;
import com.proshop.auth.repository.UserRepository;
import com.proshop.auth.service.JwtAuthService;
import com.proshop.auth.service.auth.AuthService;
import com.proshop.auth.utils.JwtUtil;
import com.proshop.auth.utils.enums.ResErrorCode;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final TokenRedisRepository tokenRedisRepository;
  private final JwtUtil jwtUtil;
  private final JavaMailSender mailSender;
  private final BlockingQueue<SimpleMailMessage> queue = new LinkedBlockingQueue<>();
  private final RedisTemplate<String,String> redisTemplate;
  private static final String OTP_PREFIX = "otp:";

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
    UserEntity userEntity = userRepository.findByCode(userCode).orElseThrow(() -> new ResException(
        ResErrorCode.valueOf("")));
    String oldPassword = req.getOldPassword() != null ? req.getOldPassword().trim() : "";
    String newPassword = req.getNewPassword() != null ? req.getNewPassword().trim() : "";
    if (!passwordEncoder.matches(oldPassword, userEntity.getPassword())) {
      throw new ResException(ResErrorCode.INVALID_PASSWORD);
    }
    if (passwordEncoder.matches(newPassword, userEntity.getPassword())) {
      throw new ResException(ResErrorCode.OLD_PASSWORD_NOT_VALID);
    }

    validateNewPassword(newPassword);

    userEntity.setPasswordHash(passwordEncoder.encode(newPassword));
    userEntity.setModifiedDate(LocalDateTime.now());
    userEntity.setModifiedBy(userCode);
    userRepository.save(userEntity);
    boolean deleted = tokenRedisRepository.deleteAllTokens(userEntity.getCode());
    if (!deleted) {
      log.error("Unable to revoke Redis token for userCode = {}", userEntity.getCode());
      throw new ResException(ResErrorCode.TOKEN_DELETE_FAILED);
    }

    return userMapper.toDTO(userEntity);
  }

  @Override
  public UserInfoResponse register(RegisterRequest request) {
    if (userRepository.existsByAccount(request.getAccount())) {
      throw new ResException(ResErrorCode.ACCOUNT_ALREADY_EXISTS);
    }
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new ResException(ResErrorCode.EMAIL_ALREADY_EXISTS);
    }

    validateNewPassword(request.getPassword());

    UserEntity user = new UserEntity();
    user.setAccount(request.getAccount().trim());
    user.setEmail(request.getEmail().trim());
    user.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
    user.setPasswordHash(passwordEncoder.encode(request.getPassword().trim()));
    user.setCreatedDate(LocalDateTime.now());
    user.setCreatedBy(request.getAccount());

    String userCode = generateUserCode();
    user.setCode(userCode);

    UserEntity savedUser = userRepository.save(user);
    return userMapper.toDTO(savedUser);
  }

  @Override
  @Transactional
  public Boolean logout(String authHeader) {
    String token = authHeader;
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    } else {
      throw new ResException(ResErrorCode.TOKEN_INVALID); // Không đúng format
    }
    try {
      String userCode = jwtUtil.getUserCodeFromToken(token);
      return tokenRedisRepository.deleteToken(userCode, token);
    } catch (Exception e) {
      log.error("Logout failed - Invalid token", e);
      throw new ResException(ResErrorCode.TOKEN_INVALID);
    }
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

  private void validateNewPassword(String password) {
    if (password == null || password.trim().isEmpty()) {
      throw new ResException(ResErrorCode.INVALID_USER_PASS);
    }
    if (password.length() < 6) {
      throw  new ResException(ResErrorCode.WEAK_PASSWORD);
    }
    String trimmedPassword = password.trim();
    if (!trimmedPassword.matches(".*[A-Z].*")) {
      throw new ResException(ResErrorCode.WEAK_PASSWORD);
    }
    if (!trimmedPassword.matches(".*[a-z].*")) {
      throw new ResException(ResErrorCode.WEAK_PASSWORD);
    }
    if (!trimmedPassword.matches(".*\\d.*")) {
      throw new ResException(ResErrorCode.WEAK_PASSWORD);
    }
    if (!trimmedPassword.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
      throw new ResException(ResErrorCode.WEAK_PASSWORD);
    }
  }

  public String generateUserCode() {
    long count = userRepository.count();
    long nextId = count + 1;
    return String.format("USER_%03d", nextId);
  }

  @Async("mailTaskExecutor")
  public void sendAsync(String to, String otp) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

      helper.setTo(to);
      helper.setFrom(new InternetAddress("proshop@gmail.com", "ProShop"));
      helper.setSubject("Mã OTP Đặt Lại Mật Khẩu - ProShop");
      helper.setText("""
                Xin chào,
                
                Bạn (hoặc ai đó) đã yêu cầu đặt lại mật khẩu cho tài khoản tại ProShop.
                
                Mã OTP của bạn là: %s
                
                Mã này sẽ hết hạn sau 5 phút.
                
                Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.
                
                Trân trọng,
                Nhóm 3 ProShop !
            """.formatted(otp), false);

      mailSender.send(mimeMessage);
    } catch (Exception e) {
      System.err.println("Không thể gửi email: " + e.getMessage());
    }
  }

  @Override
  public OtpResponse sendOtp(SendOtpRequest request) {
    String email = request.getEmail();
    Optional<UserEntity> userEntity = userRepository.findByEmail(email);
    if (!userEntity.isPresent()) {
      throw new ResException(ResErrorCode.ENTITY_NOT_EXISTS);
    }

    String otp = String.format("%06d", new Random().nextInt(999999));
    String key = OTP_PREFIX + email;
    redisTemplate.opsForValue().set(key, otp);
    redisTemplate.expire(key, 5, TimeUnit.MINUTES);
    sendAsync(email, otp);
    return new OtpResponse(email, LocalDateTime.now().plusMinutes(5), "OTP đã được gửi");
  }

  @Override
  public OtpResponse verifyOtp(VerifyOtpRequest request) {
    String email = request.getEmail();
    String inputOtp = request.getOtp();

    String key = OTP_PREFIX + email;
    String redisOtp = redisTemplate.opsForValue().get(key);
    if (redisOtp == null) {
      throw new ResException(ResErrorCode.OTP_EXPIRED);
    }
    if (!redisOtp.equals(inputOtp)) {
      throw new ResException(ResErrorCode.OTP_VERIFICATION_FAIL);
    }
    String verifiedKey = OTP_PREFIX + "VERIFIED:" + email;
    redisTemplate.opsForValue().set(verifiedKey,"true");
    redisTemplate.expire(verifiedKey, 5, TimeUnit.MINUTES);
    redisTemplate.delete(key);
    return new OtpResponse(email, LocalDateTime.now(), "OTP hợp lệ");
  }


  @Override
  public OtpResponse resetPassword(ResetPasswordRequest request) {
    String email = request.getEmail();
    String newPassword = request.getNewPassword();
    String confirmPassword = request.getConfirmPassword();

    String verifiedKey = OTP_PREFIX + "VERIFIED:" + email;
    Object verified = redisTemplate.opsForValue().get(verifiedKey);

    if (verified == null) {
      throw new ResException(ResErrorCode.OTP_NOT_VERIFICATION);
    }

    if (newPassword == null || confirmPassword == null || !newPassword.equals(confirmPassword)) {
      throw new ResException(ResErrorCode.INVALID_PASSWORD);
    }

    Optional<UserEntity> userEntity = userRepository.findByEmail(email);
    if (!userEntity.isPresent()) {
      throw new ResException(ResErrorCode.ENTITY_NOT_EXISTS);
    }
    UserEntity  user = userEntity.get();
    user.setPasswordHash(passwordEncoder.encode(newPassword));
    userRepository.save(user);
    return new OtpResponse(email, LocalDateTime.now(), "Mật khẩu đã được đặt lại thành công");
  }
}
