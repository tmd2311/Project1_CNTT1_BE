package com.proshop.auth.service.auth;

import com.proshop.auth.dto.request.ChangePasswordRequest;
import com.proshop.auth.dto.request.LoginRequest;
import com.proshop.auth.dto.request.LogoutRequest;
import com.proshop.auth.dto.response.LoginResponse;
import com.proshop.auth.dto.response.UserInfoResponse;
import com.proshop.auth.entity.UserEntity;

public interface AuthService {
  LoginResponse login(LoginRequest request);

  LoginResponse makeLoginResponse(UserEntity entity);

  LoginResponse makeLoginResponse(UserEntity entity, String provider);

  UserInfoResponse changePassword(ChangePasswordRequest req, String userCode);

  Boolean logout(LogoutRequest req);
}
