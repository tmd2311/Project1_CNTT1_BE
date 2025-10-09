package com.proshop.auth_lib.utils.enums;

import org.springframework.http.HttpStatus;

public enum ResErrorCode {

  // ===== SUCCESS =====
  SUCCESS(HttpStatus.OK, "SUC01", "Thành công", "Success"),

  // ===== PERMISSION / AUTH =====
  PERMISSION_DENIED(HttpStatus.FORBIDDEN, "PMS01", "Quyền bị từ chối", "Permission Denied"),
  PERMISSION_DENIED_REMOVE_ACCOUNT(HttpStatus.UNAUTHORIZED, "PMS02",
      "Liên hệ với người quản lý để thực hiện chức năng này", "Permission Denied"),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "PMS03",
      "Thông tin xác thực bị thiếu hoặc không chính xác", "Credentials are missing or incorrect"),
  INVALID_USER_PASS(HttpStatus.UNAUTHORIZED, "PMS04",
      "Mật khẩu không đúng", "The password is incorrect"),
  USER_INACTIVE(HttpStatus.UNAUTHORIZED, "PMS05",
      "Tài khoản đã tạm khóa, vui lòng đăng nhập lại", "The account is temporarily locked"),

  // ===== TOKEN =====
  TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOK02", "Mã truy cập hết hạn", "Expired access code"),
  TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "TOK03", "Không tìm thấy mã truy cập", "Access code not found"),
  TOKEN_INVALID(HttpStatus.BAD_REQUEST, "TOK04", "Mã truy cập không hợp lệ", "Invalid access code"),
  TOKEN_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TOK05",
      "Thu hồi token thất bại", "Failed to revoke token"),

  // ===== GENERAL ERROR =====
  GENERAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GEN01", "Thất bại",
      "An error occurred, please try again in a few minutes");
  // ===== FIELDS =====
  private final HttpStatus status;
  private final String code;
  private final String message;
  private final String label;

  ResErrorCode(HttpStatus status, String code, String message, String label) {
    this.status = status;
    this.code = code;
    this.message = message;
    this.label = label;
  }

  public HttpStatus status() {
    return status;
  }

  public String code() {
    return code;
  }

  public String message() {
    return message;
  }

  public String label() {
    return label;
  }
}
