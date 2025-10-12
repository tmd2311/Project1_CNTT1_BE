package com.proshop.auth.controller;

import com.proshop.auth.dto.request.ChangePasswordRequest;
import com.proshop.auth.dto.request.LoginRequest;
import com.proshop.auth.dto.request.RegisterRequest;
import com.proshop.auth.dto.request.ResetPasswordRequest;
import com.proshop.auth.dto.request.SendOtpRequest;
import com.proshop.auth.dto.request.VerifyOtpRequest;
import com.proshop.auth.dto.response.GeneralResponse;
import com.proshop.auth.dto.response.LoginResponse;
import com.proshop.auth.dto.response.OtpResponse;
import com.proshop.auth.dto.response.ResponseFactory;
import com.proshop.auth.dto.response.UserInfoResponse;
import com.proshop.auth.service.auth.AuthService;
import com.proshop.auth.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class AuthController {

  private final AuthService authService;
  private final JwtUtil jwtUtil;


  @PostMapping("/auth/login")
  public ResponseEntity<GeneralResponse<LoginResponse>> login(
      @RequestBody @Valid LoginRequest loginRequest) {
    LoginResponse loginResponse = authService.login(loginRequest);
    return ResponseFactory.success(loginResponse);
  }

  @PostMapping("/change-password")
  public ResponseEntity<GeneralResponse<UserInfoResponse>> changePassword(
      @RequestBody @Valid ChangePasswordRequest request,
      @RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7); // Remove "Bearer "
    String userCode = jwtUtil.getUserCodeFromToken(token);
    UserInfoResponse response = authService.changePassword(request, userCode);
    return ResponseFactory.success(response);
  }


  @PostMapping("/auth/register")
  public ResponseEntity<GeneralResponse<UserInfoResponse>> register(
      @Valid @RequestBody RegisterRequest request) {
    UserInfoResponse response = authService.register(request);
    return ResponseFactory.success(response);
  }

  @PostMapping("/logout")
  public ResponseEntity<GeneralResponse<Boolean>> logout(@RequestHeader("Authorization") String authHeader) {
    Boolean result = authService.logout(authHeader);
    return ResponseFactory.success(result);
  }

  @PostMapping("/auth/forgot-password")
  public ResponseEntity<GeneralResponse<OtpResponse>> sendOtp(@RequestBody SendOtpRequest request) {
    return ResponseFactory.success(authService.sendOtp(request));
  }

  @PostMapping("/auth/verify-otp")
  public ResponseEntity<GeneralResponse<OtpResponse>> verifyOtp(@RequestBody VerifyOtpRequest request) {
    return ResponseFactory.success(authService.verifyOtp(request));
  }

  @PostMapping("/auth/reset-password")
  public ResponseEntity<GeneralResponse<OtpResponse>> resetPassword(@RequestBody ResetPasswordRequest request) {
    return ResponseFactory.success(authService.resetPassword(request));
  }


}
