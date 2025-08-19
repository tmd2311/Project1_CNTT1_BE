package com.proshop.auth.controller;

import com.proshop.auth.dto.request.LoginRequest;
import com.proshop.auth.dto.response.GeneralResponse;
import com.proshop.auth.dto.response.LoginResponse;
import com.proshop.auth.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.proshop.auth.dto.response.ResponseFactory;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class AuthController {

  private final AuthService authService;


  @PostMapping("/auth/login")
  public ResponseEntity<GeneralResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {
    LoginResponse loginResponse = authService.login(loginRequest);
    return ResponseFactory.success(loginResponse);
  }
}
