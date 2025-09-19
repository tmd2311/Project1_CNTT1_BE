package com.proshop.auth.controller;

import com.proshop.auth.dto.response.GeneralResponse;
import com.proshop.auth.dto.response.PageResponse;
import com.proshop.auth.dto.response.UserInfoResponse;
import com.proshop.auth.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {
  private final UserService userService;

  @GetMapping("/getAllUser")
  public ResponseEntity<GeneralResponse<PageResponse<UserInfoResponse>>> getAllUsers(Pageable pageable) {
    GeneralResponse<PageResponse<UserInfoResponse>> users = userService.getAllUsers(pageable);
    return ResponseEntity.ok(users);
  }
}
