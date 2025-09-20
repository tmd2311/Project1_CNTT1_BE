package com.proshop.auth.service.user;

import com.proshop.auth.dto.response.GeneralResponse;
import com.proshop.auth.dto.response.PageResponse;
import com.proshop.auth.dto.response.UserInfoResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {
  GeneralResponse<PageResponse<UserInfoResponse>> getAllUsers(Pageable pageable);

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

  /**
   * Tìm kiếm user
   */


}
