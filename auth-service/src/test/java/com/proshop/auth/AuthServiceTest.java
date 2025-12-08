package com.proshop.auth;

import com.proshop.auth.dto.request.*;
import com.proshop.auth.dto.response.*;
import com.proshop.auth.entity.*;
import com.proshop.auth.mapper.LoginMapper;
import com.proshop.auth.mapper.UserMapper;
import com.proshop.auth.repository.*;
import com.proshop.auth.service.JwtAuthService;
import com.proshop.auth.service.auth.impl.AuthServiceImpl;
import com.proshop.auth.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SocialProviderRepository socialProviderRepository;

    @Mock
    private JwtAuthService jwtAuthService;


    @Mock
    private LoginMapper loginMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenRedisRepository tokenRedisRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserEntity testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        loginRequest = createLoginRequest();
        registerRequest = createRegisterRequest();
    }

    // ==================== Helper Methods ====================

    private UserEntity createTestUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setCode("USER_001");
        user.setAccount("testuser");
        user.setEmail("test@example.com");
        user.setPhone("0123456789");
        user.setPasswordHash("hashedPassword");
        user.setDeleted(false);
        return user;
    }

    private LoginRequest createLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setAccount("testuser");
        request.setPassword("password123");
        return request;
    }

    private RegisterRequest createRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setAccount("newuser");
        request.setEmail("new@example.com");
        request.setPhone("0987654321");
        request.setPassword("Password@123");
        return request;
    }

    private Authentication mockSuccessfulAuthentication() {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(testUser);
        return auth;
    }

    // ==================== Login Tests ====================

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully with valid credentials")
        void login_Success() {
            // Arrange
            Authentication auth = mockSuccessfulAuthentication();
            LoginResponse expectedResponse = new LoginResponse();
            expectedResponse.setToken("token123");

            when(userRepository.findByAccount(anyString())).thenReturn(Optional.of(testUser));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(auth);
            when(socialProviderRepository.findByUserEntity(any())).thenReturn(Collections.emptyList());
            when(userRepository.getRoleByUserId(anyLong())).thenReturn(Collections.emptyList());
            when(jwtAuthService.generateAndStoreToken(any(), any())).thenReturn("token123");
            when(loginMapper.toDTO(any())).thenReturn(expectedResponse);

            // Act
            LoginResponse result = authService.login(loginRequest);

            // Assert
            assertNotNull(result);
            assertEquals("token123", result.getToken());

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(userRepository).save(argThat(user ->
                    user.getLastLogin() != null
            ));
            verify(jwtAuthService).generateAndStoreToken(any(), any());
            verify(loginMapper).toDTO(any());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void login_UserNotFound_ThrowsException() {
            // Arrange
            when(userRepository.findByAccount(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.login(loginRequest));
            assertEquals(ResErrorCode.INVALID_USER_PASS.code(), exception.getCode());

            verify(authenticationManager, never()).authenticate(any());
        }

        @Test
        @DisplayName("Should throw exception when account is blocked")
        void login_AccountBlocked_ThrowsException() {
            // Arrange
            when(userRepository.findByAccount(anyString())).thenReturn(Optional.of(testUser));
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new LockedException("Account locked"));

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.login(loginRequest));
            assertEquals(ResErrorCode.ACCOUNT_BLOCKED.code(), exception.getCode());

            verify(jwtAuthService, never()).generateAndStoreToken(any(), any());
        }

        @Test
        @DisplayName("Should throw exception when credentials are invalid")
        void login_InvalidCredentials_ThrowsException() {
            // Arrange
            when(userRepository.findByAccount(anyString())).thenReturn(Optional.of(testUser));
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.login(loginRequest));
            assertEquals(ResErrorCode.INVALID_USER_PASS.code(), exception.getCode());
        }

        @Test
        @DisplayName("Should throw exception when account is deleted")
        void login_DeletedAccount_ThrowsException() {
            // Arrange
            testUser.setDeleted(true);
            when(userRepository.findByAccount(anyString())).thenReturn(Optional.of(testUser));

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.login(loginRequest));
            assertEquals(ResErrorCode.ACCOUNT_DELETED.code(), exception.getCode());

            verify(authenticationManager, never()).authenticate(any());
        }

        @Test
        @DisplayName("Should update last login timestamp on successful login")
        void login_UpdatesLastLoginTimestamp() {
            // Arrange
            Authentication auth = mockSuccessfulAuthentication();
            LoginResponse expectedResponse = new LoginResponse();

            when(userRepository.findByAccount(anyString())).thenReturn(Optional.of(testUser));
            when(authenticationManager.authenticate(any())).thenReturn(auth);
            when(socialProviderRepository.findByUserEntity(any())).thenReturn(Collections.emptyList());
            when(userRepository.getRoleByUserId(anyLong())).thenReturn(Collections.emptyList());
            when(jwtAuthService.generateAndStoreToken(any(), any())).thenReturn("token123");
            when(loginMapper.toDTO(any())).thenReturn(expectedResponse);

            // Act
            authService.login(loginRequest);

            // Assert
            ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository).save(userCaptor.capture());
            assertNotNull(userCaptor.getValue().getLastLogin());
        }
    }

    // ==================== Register Tests ====================

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should register new user successfully")
        void register_Success() {
            // Arrange
            UserInfoResponse expectedResponse = new UserInfoResponse();

            when(userRepository.existsByAccount(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.count()).thenReturn(0L);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);
            when(userMapper.toDTO(any())).thenReturn(expectedResponse);

            // Act
            UserInfoResponse result = authService.register(registerRequest);

            // Assert
            assertNotNull(result);

            ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository).save(userCaptor.capture());

            UserEntity savedUser = userCaptor.getValue();
            assertNotNull(savedUser.getCode());
            assertEquals("encodedPassword", savedUser.getPasswordHash());
        }

        @Test
        @DisplayName("Should throw exception when account already exists")
        void register_AccountAlreadyExists_ThrowsException() {
            // Arrange
            when(userRepository.existsByAccount(anyString())).thenReturn(true);

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.register(registerRequest));
            assertEquals(ResErrorCode.ACCOUNT_ALREADY_EXISTS.code(), exception.getCode());

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void register_EmailAlreadyExists_ThrowsException() {
            // Arrange
            when(userRepository.existsByAccount(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(true);

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.register(registerRequest));
            assertEquals(ResErrorCode.EMAIL_ALREADY_EXISTS.code(), exception.getCode());

            verify(userRepository, never()).save(any());
        }

        @ParameterizedTest
        @ValueSource(strings = {"weak", "12345", "password", "PASSWORD", "Pass123"})
        @DisplayName("Should throw exception for weak passwords")
        void register_WeakPassword_ThrowsException(String weakPassword) {
            // Arrange
            registerRequest.setPassword(weakPassword);
            when(userRepository.existsByAccount(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.register(registerRequest));
            assertEquals(ResErrorCode.WEAK_PASSWORD.code(), exception.getCode());
        }

        @Test
        @DisplayName("Should generate unique user code")
        void register_GeneratesUniqueUserCode() {
            // Arrange
            when(userRepository.existsByAccount(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.count()).thenReturn(5L);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);
            when(userMapper.toDTO(any())).thenReturn(new UserInfoResponse());

            // Act
            authService.register(registerRequest);

            // Assert
            ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository).save(userCaptor.capture());

            String code = userCaptor.getValue().getCode();
            assertNotNull(code);
            assertTrue(code.startsWith("USER_"));
        }
    }

    // ==================== Change Password Tests ====================

    @Nested
    @DisplayName("Change Password Tests")
    class ChangePasswordTests {

        @Test
        @DisplayName("Should change password successfully")
        void changePassword_Success() {
            // Arrange
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setOldPassword("OldPass@123");
            request.setNewPassword("NewPass@123");

            UserInfoResponse expectedResponse = new UserInfoResponse();

            when(userRepository.findByCode(anyString())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(eq("OldPass@123"), anyString())).thenReturn(true);
            when(passwordEncoder.matches(eq("NewPass@123"), anyString())).thenReturn(false);
            when(passwordEncoder.encode(eq("NewPass@123"))).thenReturn("newHashedPassword");
            when(tokenRedisRepository.deleteAllTokens(anyString())).thenReturn(true);
            when(userMapper.toDTO(any())).thenReturn(expectedResponse);

            // Act
            UserInfoResponse result = authService.changePassword(request, "USER_001");

            // Assert
            assertNotNull(result);

            ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository).save(userCaptor.capture());
            assertEquals("newHashedPassword", userCaptor.getValue().getPasswordHash());

            verify(tokenRedisRepository).deleteAllTokens("USER_001");
        }

        @Test
        @DisplayName("Should throw exception when old password is invalid")
        void changePassword_InvalidOldPassword_ThrowsException() {
            // Arrange
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setOldPassword("wrongPassword");
            request.setNewPassword("NewPass@123");

            when(userRepository.findByCode(anyString())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(eq("wrongPassword"), anyString())).thenReturn(false);

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.changePassword(request, "USER_001"));
            assertEquals(ResErrorCode.INVALID_PASSWORD.code(), exception.getCode());

            verify(userRepository, never()).save(any());
            verify(tokenRedisRepository, never()).deleteAllTokens(anyString());
        }

        @Test
        @DisplayName("Should throw exception when new password same as old")
        void changePassword_SameAsOldPassword_ThrowsException() {
            // Arrange
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setOldPassword("OldPass@123");
            request.setNewPassword("OldPass@123");

            when(userRepository.findByCode(anyString())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(eq("OldPass@123"), anyString())).thenReturn(true);

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.changePassword(request, "USER_001"));
            assertEquals(ResErrorCode.OLD_PASSWORD_NOT_VALID.code(), exception.getCode());
        }

        @ParameterizedTest
        @ValueSource(strings = {"weak", "12345", "newpass"})
        @DisplayName("Should throw exception for weak new passwords")
        void changePassword_WeakNewPassword_ThrowsException(String weakPassword) {
            // Arrange
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setOldPassword("OldPass@123");
            request.setNewPassword(weakPassword);

            when(userRepository.findByCode(anyString())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(eq("OldPass@123"), anyString())).thenReturn(true);
            when(passwordEncoder.matches(eq(weakPassword), anyString())).thenReturn(false);

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.changePassword(request, "USER_001"));
            assertEquals(ResErrorCode.WEAK_PASSWORD.code(), exception.getCode());
        }
    }

    // ==================== Logout Tests ====================

    @Nested
    @DisplayName("Logout Tests")
    class LogoutTests {

        @Test
        @DisplayName("Should logout successfully")
        void logout_Success() {
            // Arrange
            String authHeader = "Bearer token123";

            when(jwtUtil.getUserCodeFromToken("token123")).thenReturn("USER_001");
            when(tokenRedisRepository.deleteToken("USER_001", "token123")).thenReturn(true);

            // Act
            Boolean result = authService.logout(authHeader);

            // Assert
            assertTrue(result);
            verify(jwtUtil).getUserCodeFromToken("token123");
            verify(tokenRedisRepository).deleteToken("USER_001", "token123");
        }

        @Test
        @DisplayName("Should throw exception for invalid token format")
        void logout_InvalidTokenFormat_ThrowsException() {
            // Arrange
            String authHeader = "InvalidToken";

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.logout(authHeader));
            assertEquals(ResErrorCode.TOKEN_INVALID.code(), exception.getCode());

            verify(tokenRedisRepository, never()).deleteToken(anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw exception for null token")
        void logout_NullToken_ThrowsException() {
            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.logout(null));
            assertEquals(ResErrorCode.TOKEN_INVALID.code(), exception.getCode());
        }
    }

    // ==================== OTP Tests ====================

    @Nested
    @DisplayName("OTP Tests")
    class OtpTests {

        @Test
        @DisplayName("Should send OTP successfully")
        void sendOtp_Success() {
            // Arrange
            SendOtpRequest request = new SendOtpRequest("test@example.com");

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            lenient().when(valueOperations.get("otp:test@example.com")).thenReturn(null);
            doNothing().when(valueOperations).set(anyString(), anyString());

            // Mock email - use lenient to avoid unnecessary stubbing error
            lenient().when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            lenient().doNothing().when(mailSender).send(any(MimeMessage.class));

            // Act
            OtpResponse result = authService.sendOtp(request);

            // Assert
            assertNotNull(result);
            assertEquals("test@example.com", result.getEmail());

            verify(valueOperations).set(anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw exception when user not found for OTP")
        void sendOtp_UserNotFound_ThrowsException() {
            // Arrange
            SendOtpRequest request = new SendOtpRequest("notfound@example.com");

            when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.sendOtp(request));
            assertEquals(ResErrorCode.ENTITY_NOT_EXISTS.code(), exception.getCode());

            verify(valueOperations, never()).set(anyString(), anyString());
        }

        @Test
        @DisplayName("Should verify OTP successfully")
        void verifyOtp_Success() {
            // Arrange
            VerifyOtpRequest request = new VerifyOtpRequest();
            request.setEmail("test@example.com");
            request.setOtp("123456");

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("otp:test@example.com")).thenReturn("123456");
            doNothing().when(valueOperations).set(anyString(), anyString());
            when(redisTemplate.delete("otp:test@example.com")).thenReturn(true);

            // Act
            OtpResponse result = authService.verifyOtp(request);

            // Assert
            assertNotNull(result);
            assertEquals("test@example.com", result.getEmail());

            verify(valueOperations).set("otp:VERIFIED:test@example.com", "true");
            verify(redisTemplate).delete("otp:test@example.com");
        }

        @Test
        @DisplayName("Should throw exception when OTP is invalid")
        void verifyOtp_InvalidOtp_ThrowsException() {
            // Arrange
            VerifyOtpRequest request = new VerifyOtpRequest();
            request.setEmail("test@example.com");
            request.setOtp("wrong");

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("otp:test@example.com")).thenReturn("123456");

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.verifyOtp(request));
            assertEquals(ResErrorCode.OTP_VERIFICATION_FAIL.code(), exception.getCode());

            verify(redisTemplate, never()).delete(anyString());
        }

        @Test
        @DisplayName("Should throw exception when OTP is expired")
        void verifyOtp_ExpiredOtp_ThrowsException() {
            // Arrange
            VerifyOtpRequest request = new VerifyOtpRequest();
            request.setEmail("test@example.com");
            request.setOtp("123456");

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("otp:test@example.com")).thenReturn(null);

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.verifyOtp(request));
            assertEquals(ResErrorCode.OTP_EXPIRED.code(), exception.getCode());
        }
    }

    // ==================== Reset Password Tests ====================

    @Nested
    @DisplayName("Reset Password Tests")
    class ResetPasswordTests {

        @Test
        @DisplayName("Should reset password successfully")
        void resetPassword_Success() {
            // Arrange
            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setEmail("test@example.com");
            request.setNewPassword("NewPass@123");
            request.setConfirmPassword("NewPass@123");

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("otp:VERIFIED:test@example.com")).thenReturn("true");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.encode("NewPass@123")).thenReturn("newHashedPassword");

            // Act
            OtpResponse result = authService.resetPassword(request);

            // Assert
            assertNotNull(result);
            assertEquals("test@example.com", result.getEmail());

            ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository).save(userCaptor.capture());
            assertEquals("newHashedPassword", userCaptor.getValue().getPasswordHash());
        }

        @Test
        @DisplayName("Should throw exception when OTP not verified")
        void resetPassword_NotVerified_ThrowsException() {
            // Arrange
            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setEmail("test@example.com");
            request.setNewPassword("NewPass@123");
            request.setConfirmPassword("NewPass@123");

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("otp:VERIFIED:test@example.com")).thenReturn(null);

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.resetPassword(request));
            assertEquals(ResErrorCode.OTP_VERIFICATION_FAIL.code(), exception.getCode());

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when passwords don't match")
        void resetPassword_PasswordMismatch_ThrowsException() {
            // Arrange
            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setEmail("test@example.com");
            request.setNewPassword("NewPass@123");
            request.setConfirmPassword("DifferentPass@123");

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("otp:VERIFIED:test@example.com")).thenReturn("true");

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.resetPassword(request));
            assertEquals(ResErrorCode.INVALID_PASSWORD.code(), exception.getCode());

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void resetPassword_UserNotFound_ThrowsException() {
            // Arrange
            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setEmail("notfound@example.com");
            request.setNewPassword("NewPass@123");
            request.setConfirmPassword("NewPass@123");

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("otp:VERIFIED:notfound@example.com")).thenReturn("true");
            when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.resetPassword(request));
            assertEquals(ResErrorCode.ENTITY_NOT_EXISTS.code(), exception.getCode());
        }

        @ParameterizedTest
        @ValueSource(strings = {"weak", "12345", "password"})
        @DisplayName("Should throw exception for weak passwords in reset")
        void resetPassword_WeakPassword_ThrowsException(String weakPassword) {
            // Arrange
            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setEmail("test@example.com");
            request.setNewPassword(weakPassword);
            request.setConfirmPassword(weakPassword);

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("otp:VERIFIED:test@example.com")).thenReturn("true");

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> authService.resetPassword(request));
            assertEquals(ResErrorCode.ENTITY_NOT_EXISTS.code(), exception.getCode());
        }
    }
}