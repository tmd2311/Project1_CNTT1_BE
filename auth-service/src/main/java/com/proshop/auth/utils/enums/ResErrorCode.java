package com.proshop.auth.utils.enums;

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

  // ===== GENERAL ERROR =====
  GENERAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GEN01", "Thất bại",
      "An error occurred, please try again in a few minutes"),
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "GEN02", "Truyền sai tham số", "Passing wrong parameters"),
  ENTITY_NOT_EXISTS(HttpStatus.NOT_FOUND, "GEN03", "Thực thể không tồn tại",
      "Entity does not exist"),
  ENTITY_EXISTED(HttpStatus.BAD_REQUEST, "GEN04", "Thực thể đã tồn tại", "Entity already exists"),
  MISS_PARAM(HttpStatus.BAD_REQUEST, "GEN05", "Truyền thiếu tham số", "Missing parameters"),
  ENTITY_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "GEN06", "Thực thể dừng hoạt động", "Entity not active"),
  DATE_NOT_VALID(HttpStatus.BAD_REQUEST, "GEN07", "Thời gian không hợp lệ", "Invalid input time"),

  // ===== TOKEN =====
  SAME_PASSWORD(HttpStatus.BAD_REQUEST, "TOK01",
      "Không được đặt mật khẩu giống mật khẩu cũ", "Cannot reuse old password"),
  TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOK02", "Mã truy cập hết hạn", "Expired access code"),
  TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "TOK03", "Không tìm thấy mã truy cập", "Access code not found"),
  TOKEN_INVALID(HttpStatus.BAD_REQUEST, "TOK04", "Mã truy cập không hợp lệ", "Invalid access code"),
  TOKEN_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TOK05",
      "Thu hồi token thất bại", "Failed to revoke token"),

  // ===== OTP =====
  CANT_SEND_OTP(HttpStatus.SERVICE_UNAVAILABLE, "OTP01", "Lỗi khi gửi OTP", "Error sending OTP"),
  OTP_REFERENCE_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "OTP02", "Không tìm thấy mã tham chiếu OTP",
      "OTP reference not found"),
  ERROR_SEND_OTP(HttpStatus.BAD_REQUEST, "OTP03", "Gửi OTP không thành công",
      "Send OTP failed"),
  OTP_REFERENCE_ID_IS_REQUIRED(HttpStatus.BAD_REQUEST, "OTP04", "Thiếu mã tham chiếu OTP",
      "OTP reference is required"),
  MAXIMUM_OTP_GENERATION_REACHED(HttpStatus.BAD_REQUEST, "OTP05", "Đã đạt giới hạn gửi OTP",
      "Max OTP generation reached"),
  CANT_INSTANT_REGENERATE_OTP_AFTER_CREATE(HttpStatus.BAD_REQUEST, "OTP06",
      "Vui lòng không gửi lại quá nhanh", "Too many OTP requests"),
  OTP_USED(HttpStatus.BAD_REQUEST, "OTP07", "Mã OTP đã được sử dụng", "OTP already used"),
  OTP_EXPIRED(HttpStatus.BAD_REQUEST, "OTP08", "Mã OTP đã hết hạn", "OTP expired"),
  INVALID_OTP_REGISTER_REQUEST(HttpStatus.BAD_REQUEST, "OTP09", "Yêu cầu số điện thoại khi đăng ký",
      "Phone number required when registering"),
  INVALID_OTP_TYPE(HttpStatus.BAD_REQUEST, "OTP10", "Kiểu OTP không hợp lệ", "Invalid OTP type"),
  INVALID_MOBILE_NUMBER_FORMAT(HttpStatus.BAD_REQUEST, "OTP11", "Định dạng số điện thoại không hợp lệ",
      "Invalid phone number format"),
  OTP_VERIFICATION_FAIL(HttpStatus.BAD_REQUEST, "OTP12", "Xác minh OTP thất bại",
      "OTP verification failed"),
  MOBILE_REGISTERED(HttpStatus.BAD_REQUEST, "OTP13", "Số điện thoại đã được đăng ký",
      "Phone number already registered"),
  EMAIL_REGISTERED(HttpStatus.BAD_REQUEST, "OTP14", "Email đã được đăng ký",
      "Email already registered"),
  MOBILE_NOT_REGISTERED(HttpStatus.BAD_REQUEST, "OTP15", "Số điện thoại chưa được đăng ký",
      "Phone number not registered"),
  EMAIL_NOT_REGISTERED(HttpStatus.BAD_REQUEST, "OTP16", "Email chưa được đăng ký",
      "Email not registered"),
  MAXIMUM_OTP_VERIFICATION_REACHED(HttpStatus.BAD_REQUEST, "OTP17", "Đã đạt giới hạn xác minh OTP",
      "Max OTP verification attempts reached"),

  // ===== ACCOUNT =====
  ACCOUNT_BLOCKED(HttpStatus.FORBIDDEN, "ACC01", "Tài khoản đã bị khóa", "Account blocked"),
  ACCOUNT_DELETED(HttpStatus.BAD_REQUEST, "ACC02", "Tài khoản đã bị xóa", "Account deleted"),
  MOBILE_BLOCKED(HttpStatus.FORBIDDEN, "ACC03",
      "Số điện thoại bị chặn do yêu cầu quá nhiều OTP", "Mobile blocked"),

  INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "ACC04", "Mật khẩu không hợp lệ", "Invalid password"),
  PASS_NOT_EQUAL(HttpStatus.BAD_REQUEST, "ACC05", "Mật khẩu mới trùng mật khẩu cũ",
      "New password must differ from old password"),
  OLD_PASSWORD_NOT_VALID(HttpStatus.BAD_REQUEST, "ACC06", "Mật khẩu cũ không đúng",
      "Old password is incorrect"),

  // ===== USER =====
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USR01",
      "Không tìm thấy người dùng", "User not found"),
  USER_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "USR02",
      "Người dùng không hoạt động", "User not active"),
  USER_DELETED(HttpStatus.BAD_REQUEST, "USR03",
      "Người dùng đã bị xóa", "User deleted"),

  // ===== DATA FORMAT =====
  INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "FMT01", "Định dạng email không hợp lệ",
      "Invalid email format"),
  INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "FMT02", "Định dạng ảnh không hợp lệ",
      "Invalid image type"),
  INVALID_DATA_FORMAT(HttpStatus.BAD_REQUEST, "FMT03", "Định dạng dữ liệu không hợp lệ",
      "Invalid data format"),

  // ===== SECURITY =====
  WEAK_PASSWORD(HttpStatus.BAD_REQUEST, "SEC01",
      "Password không đáp ứng yêu cầu bảo mật", "Weak Password"),
  PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "SEC02",
      "Mật khẩu mới phải khác mật khẩu cũ", "Password Same As Old"),
  ACCOUNT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SEC03",
      "Tài khoản đã tồn tại", "Account"),
  EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SEC04",
      "Email đã tồn tại", "Email");

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
