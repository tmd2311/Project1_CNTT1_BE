package com.proshop.auth.service.user;

import com.proshop.auth.dto.request.UpdateUserRequest;
import com.proshop.auth.dto.response.GeneralResponse;
import com.proshop.auth.dto.response.PageResponse;
import com.proshop.auth.dto.response.UserCountResponse;
import com.proshop.auth.dto.response.UserInfoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  GeneralResponse<PageResponse<UserInfoResponse>> getAllUsers(Pageable pageable);

  GeneralResponse<UserInfoResponse> getUserById(long id);

  /**
   * Cập nhật thông tin người dùng
   */
  GeneralResponse<UserInfoResponse> updateUser(Long id, UpdateUserRequest request,
      MultipartFile avatar);

  /**
   * Xóa mềm user (deactivate)
   */
  void deactivateUser(Long id);

  /**
   * Kích hoạt lại user
   */
  void activateUser(Long id);

  /**
   * Xóa cứng user
   */
  void deleteUser(Long id);

  /**
   * Xóa mềm user
   */
  void softDeleteUser(Long id);

  // ============================================
  // STATISTICS METHODS
  // ============================================

  /**
   * Get user count statistics (total, active, inactive)
   */
  UserCountResponse getUserCount();
}
