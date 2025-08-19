package com.proshop.auth.utils.enums;

import org.springframework.http.HttpStatus;

public enum ResErrorCode {

  SUCCESS(HttpStatus.OK, "200", "Thành công", "Success"),
  PERMISSION_DENIED(HttpStatus.OK, "201", "Quyền bị từ chối", "Permission Denied"),
  PERMISSION_DENIED_REMOVE_ACCOUNT(HttpStatus.UNAUTHORIZED, "202",
      "Liên hệ với người quản lý để thực hiện chức năng này", "Permission Denied"),

  GENERAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "Thất bại",
      "An error occurred, please try again in a few minutes"),
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "500", "Truyền sai tham số", "Passing wrong parameters"),
  ENTITY_NOT_EXISTS(HttpStatus.BAD_REQUEST, "404", "Thực thể không tồn tại",
      "Entity does not exist"),
  ENTITY_EXISTED(HttpStatus.BAD_REQUEST, "400", "Thực thể đã tồn tại", "Entity already exists"),
  MISS_PARAM(HttpStatus.BAD_REQUEST, "415", "Truyền lên thiếu tham số",
      "Passing up without parameters"),

  ENTITY_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "416", "Thực thể dừng hoạt động", "Entity not active"),

  SAME_PASSWORD(HttpStatus.NOT_FOUND, "500", "Không được đặt mật khẩu giống mật khẩu cũ",
      "Cannot set a password that is the same as old password"),
  TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "405", "Mã truy cập hết hạn", "Expired access code"),
  TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "406", "Không tìm thấy mã truy cập",
      "Expired access code"),
  TOKEN_INVALID(HttpStatus.BAD_REQUEST, "407", "Mã truy cập không hợp lệ", "Invalid access code"),
  CANNOT_SEND_MESSAGE(HttpStatus.BAD_REQUEST, "408", "Không thể gửi thông báo",
      "Can't send notifications"),
  UNAUTHORIZED(HttpStatus.BAD_REQUEST, "409", "Thông tin xác thực bị thiếu hoặc không chính xác",
      "Credentials are missing or incorrect"),
  INVALID_USER_PASS(HttpStatus.UNAUTHORIZED, "401",
      "Tài khoản hoặc mật khẩu không đúng, quý khách vui lòng kiểm tra và đăng nhập lại",
      "The account or password is incorrect"),

  USER_INACTIVE(HttpStatus.UNAUTHORIZED, "401",
      "Tài khoản đã tạm khóa, quý khách vui lòng kiểm tra và đăng nhập lại",
      "The account is temporarily locked"),
  DATE_NOT_VALID(HttpStatus.BAD_REQUEST, "500", "Thời gian truyền vào không hợp lệ",
      "Invalid input time"),

  CANT_SEND_OTP(HttpStatus.SERVICE_UNAVAILABLE, "601", "Lỗi khi gửi tin nhắn mã OTP",
      "Error sending OTP code message"), //"Error when send otp notification message"
  OTP_REFERENCE_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "602", "Không tìm thấy mã tham chiếu OTP",
      "OTP reference code not found"), //OTP reference id is not found
  ERROR_SEND_OTP(HttpStatus.BAD_REQUEST, "603", "Gửi tin nhắn thông báo OTP không thành công",
      "Send OTP notification message failed"), //"send otp notification message is not successful"
  OTP_REFERENCE_ID_IS_REQUIRED(HttpStatus.BAD_REQUEST, "604", "Yêu cầu mã tham chiếu OTP",
      "Request OTP reference code"), //Otp_reference_id must be specified
  MAXIMUM_OTP_GENERATION_REACHED(HttpStatus.BAD_REQUEST, "605", "Đã đạt tối đa số lần gửi OTP",
      "Maximum number of times has sent OTP"), //Maximum OTP generation reached
  CANT_INSTANT_REGENERATE_OTP_AFTER_CREATE(HttpStatus.BAD_REQUEST, "606",
      "Vui lòng không gửi lại quá nhanh", "Please do not send back too quickly"),
  OTP_USED(HttpStatus.BAD_REQUEST, "607", "Mã OTP đã được sử dụng",
      "OTP code already used"), //OTP code is already used
  OTP_EXPIRED(HttpStatus.BAD_REQUEST, "608", "Mã OTP đã hết hạn",
      "The OTP has expired"), //OTP expired
  INVALID_OTP_REGISTER_REQUEST(HttpStatus.BAD_REQUEST, "609", "Yêu cầu số điện thoại khi đăng ký",
      "Request phone number when registering"), //Invalid OTP request
  INVALID_OTP_TYPE(HttpStatus.BAD_REQUEST, "610", "Kiểu OTP không hợp lệ",
      "Invalid OTP type"), //Invalid OTP type
  INVALID_MOBILE_NUMBER_FORMAT(HttpStatus.BAD_REQUEST, "611",
      "Định dạng số điện thoại không hợp lệ",
      "Invalid phone number format"), //Invalid mobile number format
  OTP_VERIFICATION_FAIL(HttpStatus.BAD_REQUEST, "612", "Xác minh mã OTP không thành công",
      "OTP verification failed"), //OTP Verification fail
  MOBILE_REGISTERED(HttpStatus.BAD_REQUEST, "613", "Số điện thoại đã được đăng ký",
      "Registered phone number"), //"phone_number_registered", "Phone number is already registered before"
  EMAIL_REGISTERED(HttpStatus.BAD_REQUEST, "613", "Email đã được đăng ký",
      "The Email was registered"), //"phone_number_registered", "Phone number is already registered before"
  MOBILE_NOT_REGISTERED(HttpStatus.BAD_REQUEST, "614", "Số điện thoại chưa được đăng ký",
      "Unregistered phone number"), //"phone_number_not_registered", "Phone number is not registered"
  EMAIL_NOT_REGISTERED(HttpStatus.BAD_REQUEST, "614", "Email chưa được đăng ký",
      "Email is not registered"), //"Email is already registered before"
  MAXIMUM_OTP_VERIFICATION_REACHED(HttpStatus.BAD_REQUEST, "615", "Đã đạt tối đa số lần gửi OTP",
      "The maximum number of OTP submissions has been reached"), //Maximum OTP generation reached
  ACCOUNT_BLOCKED(HttpStatus.FORBIDDEN, "616", "Tài khoản đã bị khóa",
      "This account is blocked"), //"blocked_mobile", "This mobile is blocked because request too many OTP"

  ACCOUNT_DELETED(HttpStatus.BAD_REQUEST, "714", "Tài khoản đã bị xóa",
      "This account is removed"), //"blocked_mobile", "This mobile is blocked because request too many OTP"

  MOBILE_BLOCKED(HttpStatus.FORBIDDEN, "616",
      "Số điện thoại này đã bị chặn do yêu cầu quá nhiều OTP",
      "This mobile is blocked because request too many OTP"), //"blocked_mobile", "This mobile is blocked because request too many OTP"

  INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "617", "Mật khẩu không hợp lệ", "Invalid password"),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "618", "Yêu cầu không hợp lệ", "Invalid request"),
  PASS_NOT_EQUAL(HttpStatus.BAD_REQUEST, "619", "Mật khẩu mới không được trùng với mật khẩu cũ",
      "The new password does not match the old password"),
  OLD_PASSWORD_NOT_VALID(HttpStatus.BAD_REQUEST, "620", "Mật khẩu cũ không đúng",
      "Old password is incorrect"),
  TOKEN_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "621", "Thu hồi token thất bại", "Failed to revoke token"),


  INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "711", "Định dạng email không hợp lệ",
      "Invalid email type"), //Invalid email format
  INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "712", "Định dạng ảnh không hợp lệ",
      "Invalid image type"),
  INVALID_DATA_FORMAT(HttpStatus.BAD_REQUEST, "713", "Định dạng dữ liệu không hợp lệ",
      "Invalid data format");
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

  public String code() {
    return code;
  }

  public HttpStatus status() {
    return status;
  }

  public String message() {
    return message;
  }

  public String label() {
    return label;
  }
}
