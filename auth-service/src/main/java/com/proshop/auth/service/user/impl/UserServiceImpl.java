package com.proshop.auth.service.user.impl;

import com.proshop.auth.dto.request.UpdateUserRequest;
import com.proshop.auth.dto.response.GeneralResponse;
import com.proshop.auth.dto.response.PageResponse;
import com.proshop.auth.dto.response.PageResponseUtil;
import com.proshop.auth.dto.response.ResponseStatus;
import com.proshop.auth.dto.response.UserCountResponse;
import com.proshop.auth.dto.response.UserInfoResponse;
import com.proshop.auth.entity.UserEntity;
import com.proshop.auth.mapper.UserMapper;
import com.proshop.auth.repository.UserRepository;
import com.proshop.auth.service.user.UserService;
import com.proshop.auth.utils.FileUtil;
import com.proshop.auth.utils.enums.UserStatus;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final FileUtil fileUtil;

  @Override
  @Transactional(readOnly = true)
  public GeneralResponse<UserInfoResponse> getUserById(long id) {
    log.info("Getting user by ID: {}", id);
    UserEntity userEntity = userRepository.findById(id)
        .orElseThrow(() -> new ResException(ResErrorCode.USER_NOT_FOUND));

    UserInfoResponse userInfoResponse = userMapper.toDTO(userEntity);

    return new GeneralResponse<>(
        ResponseStatus.SUCCESS_STATUS,
        userInfoResponse,
        null);
  }

  @Override
  public GeneralResponse<UserInfoResponse> updateUser(Long id, UpdateUserRequest request,
      MultipartFile avatar) {
    log.info("Updating user with ID: {}", id);
    UserEntity userEntity = userRepository.findById(id)
        .orElseThrow(() -> new ResException(ResErrorCode.USER_NOT_FOUND));

    // Cập nhật các trường nếu có giá trị
    if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
      userEntity.setFullName(request.getFullName());
    }
    if (request.getBirthday() != null) {
      userEntity.setBirthday(request.getBirthday());
    }

    // Xử lý upload avatar
    if (avatar != null && !avatar.isEmpty()) {
      // Xóa file cũ nếu có
      if (userEntity.getAvatarUrl() != null && !userEntity.getAvatarUrl().isBlank()) {
        fileUtil.deleteFileByUrl(userEntity.getAvatarUrl());
      }
      // Upload file mới và lấy URL
      String newAvatarUrl = fileUtil.uploadSingleImage(avatar);
      userEntity.setAvatarUrl(newAvatarUrl);
    }

    if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
      userEntity.setPhone(request.getPhone());
    }
    if (request.getCurrentAddress() != null) {
      userEntity.setCurrentAddress(request.getCurrentAddress());
    }

    userEntity = userRepository.save(userEntity);
    log.info("Updated user successfully with ID: {}", id);

    UserInfoResponse userInfoResponse = userMapper.toDTO(userEntity);
    return new GeneralResponse<>(
        ResponseStatus.SUCCESS_STATUS,
        userInfoResponse,
        null);
  }

  @Override
  @Transactional(readOnly = true)
  public GeneralResponse<PageResponse<UserInfoResponse>> getAllUsers(Pageable pageable) {
    Page<UserEntity> usersPage = userRepository.findAllByDeletedFalse(pageable);
    List<UserInfoResponse> listUserInfoResponses = new ArrayList<>();
    UserInfoResponse userInfoResponse;
    for (UserEntity user : usersPage) {
      userInfoResponse = userMapper.toDTO(user);
      listUserInfoResponses.add(userInfoResponse);
    }
    PageResponse<UserInfoResponse> pageResponse = PageResponseUtil.buildPageResponse(
        listUserInfoResponses,
        usersPage.getTotalElements(),
        pageable.getPageNumber(),
        pageable.getPageSize());

    return new GeneralResponse<>(
        ResponseStatus.SUCCESS_STATUS,
        pageResponse,
        null);
  }

  @Override
  public void deactivateUser(Long id) {
    log.info("Deactivating user with ID: {}", id);
    UserEntity userEntity = userRepository.findById(id)
        .orElseThrow(() -> new ResException(ResErrorCode.USER_NOT_FOUND));
    userEntity.setStatus(UserStatus.INACTIVE.name());
    userRepository.save(userEntity);
    log.info("Deactivated user successfully with ID: {}", id);
  }

  @Override
  public void activateUser(Long id) {
    log.info("Activating user with ID: {}", id);
    UserEntity userEntity = userRepository.findById(id)
        .orElseThrow(() -> new ResException(ResErrorCode.USER_NOT_FOUND));
    userEntity.setStatus(UserStatus.ACTIVE.toString());
    userRepository.save(userEntity);
    log.info("Activated user successfully with ID: {}", id);
  }

  @Override
  public void deleteUser(Long id) {
    log.info("Deleting user with ID: {}", id);
    if (!userRepository.existsById(id)) {
      throw new ResException(ResErrorCode.USER_NOT_FOUND);
    }
    userRepository.deleteById(id);
    log.info("Deleted user successfully with ID: {}", id);
  }

  @Override
  public void softDeleteUser(Long id) {
    log.info("Soft deleting user with ID: {}", id);
    UserEntity userEntity = userRepository.findById(id)
        .orElseThrow(() -> new ResException(ResErrorCode.USER_NOT_FOUND));
    userEntity.setDeleted(true);
    userEntity.setStatus(UserStatus.INACTIVE.name());
    userRepository.save(userEntity);
    log.info("Soft deleted user successfully with ID: {}", id);
  }

  // ============================================
  // STATISTICS METHODS IMPLEMENTATION
  // ============================================

  @Override
  public UserCountResponse getUserCount() {
    log.info("Getting user count statistics");

    // Count total non-deleted users
    Long total = userRepository.count();

    // Count active users (status = ACTIVE and not deleted)
    Long active = userRepository.countActiveUsers();

    // Calculate inactive users
    Long inactive = total - active;

    log.info("User count: total={}, active={}, inactive={}", total, active, inactive);

    return UserCountResponse.builder()
        .totalUsers(total)
        .activeUsers(active)
        .inactiveUsers(inactive)
        .build();
  }

}
