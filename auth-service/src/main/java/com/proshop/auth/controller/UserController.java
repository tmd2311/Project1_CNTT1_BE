package com.proshop.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proshop.auth.dto.request.UpdateUserRequest;
import com.proshop.auth.dto.response.GeneralResponse;
import com.proshop.auth.dto.response.PageResponse;
import com.proshop.auth.dto.response.ResponseStatus;
import com.proshop.auth.dto.response.UserInfoResponse;
import com.proshop.auth.entity.UserEntity;
import com.proshop.auth.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final UserService userService;
  private final ObjectMapper objectMapper;

  @GetMapping("/{id}")
  public ResponseEntity<GeneralResponse<UserInfoResponse>> getUser1(@PathVariable("id") Long id) {
    GeneralResponse<UserInfoResponse> response = userService.getUserById(id);
    return ResponseEntity.status(
        response.getStatus().getCode().equals(ResponseStatus.SUCCESS_CODE) ? 200 : 500)
        .body(response);
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<GeneralResponse<UserInfoResponse>> getUser(@PathVariable("id") Long id) {
    log.info("Fetching user with id: {}", id);
    GeneralResponse<UserInfoResponse> response = userService.getUserById(id);
    return ResponseEntity.status(
            response.getStatus().getCode().equals(ResponseStatus.SUCCESS_CODE) ? 200 : 500)
        .body(response);
  }

  @GetMapping("/user/getAllUser")
  public ResponseEntity<GeneralResponse<PageResponse<UserInfoResponse>>> getAllUsers(
      Pageable pageable) {
    GeneralResponse<PageResponse<UserInfoResponse>> users = userService.getAllUsers(pageable);
    return ResponseEntity.ok(users);
  }

  @PutMapping(value = "/user/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<GeneralResponse<UserInfoResponse>> updateUser(
      @AuthenticationPrincipal UserEntity currentUser,
      @RequestPart("user") String userJson,
      @RequestPart(value = "avatar", required = false) MultipartFile avatar)
      throws JsonProcessingException {
    Long userId = currentUser.getId();
    log.info("Updating user with id: {}", userId);
    UpdateUserRequest request = objectMapper.readValue(userJson, UpdateUserRequest.class);
    GeneralResponse<UserInfoResponse> response = userService.updateUser(userId, request, avatar);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/user/{id}/deactivate")
  public ResponseEntity<GeneralResponse<Void>> deactivateUser(@PathVariable("id") Long id) {
    userService.deactivateUser(id);
    GeneralResponse<Void> response = getVoidGeneralResponse("User deactivated successfully");
    return ResponseEntity.ok(response);
  }

  @PutMapping("/user/{id}/activate")
  public ResponseEntity<GeneralResponse<Void>> activateUser(@PathVariable("id") Long id) {
    userService.activateUser(id);
    GeneralResponse<Void> response = getVoidGeneralResponse("User activated successfully");
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/user/{id}")
  public ResponseEntity<GeneralResponse<Void>> deleteUser(@PathVariable("id") Long id) {
    userService.deleteUser(id);
    GeneralResponse<Void> response = getVoidGeneralResponse("User deleted successfully");
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/user/{id}/soft")
  public ResponseEntity<GeneralResponse<Void>> softDeleteUser(@PathVariable("id") Long id) {
    userService.softDeleteUser(id);
    GeneralResponse<Void> response = getVoidGeneralResponse("User deleted successfully");
    return ResponseEntity.ok(response);
  }

  private static GeneralResponse<Void> getVoidGeneralResponse(String message) {
    GeneralResponse<Void> response = new GeneralResponse<>();
    ResponseStatus status = new ResponseStatus();
    status.setCode(ResponseStatus.SUCCESS_CODE);
    status.setLabel(ResponseStatus.SUCCESS_LABEL);
    status.setMessage(message);
    response.setStatus(status);
    response.setData(null);
    return response;
  }
}
