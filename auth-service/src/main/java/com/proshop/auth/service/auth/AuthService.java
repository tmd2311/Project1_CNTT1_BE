package com.proshop.auth.service.auth;

import com.proshop.auth.dto.request.ChangePasswordRequest;
import com.proshop.auth.dto.request.LoginRequest;
import com.proshop.auth.dto.request.RefreshTokenRequest;
import com.proshop.auth.dto.request.RegisterRequest;
import com.proshop.auth.dto.request.ResetPasswordRequest;
import com.proshop.auth.dto.request.SendOtpRequest;
import com.proshop.auth.dto.request.VerifyOtpRequest;
import com.proshop.auth.dto.response.LoginResponse;
import com.proshop.auth.dto.response.OtpResponse;
import com.proshop.auth.dto.response.RefreshTokenResponse;
import com.proshop.auth.dto.response.UserInfoResponse;
import com.proshop.auth.entity.UserEntity;

public interface AuthService {
  LoginResponse login(LoginRequest request);

  LoginResponse makeLoginResponse(UserEntity entity);

  LoginResponse makeLoginResponse(UserEntity entity, String provider);

  UserInfoResponse changePassword(ChangePasswordRequest req, String userCode);

  UserInfoResponse register(RegisterRequest request);

  Boolean logout(String token);

  OtpResponse sendOtp(SendOtpRequest request);

  OtpResponse verifyOtp(VerifyOtpRequest request);

  OtpResponse resetPassword(ResetPasswordRequest request);

  RefreshTokenResponse refreshToken(RefreshTokenRequest request);

}
