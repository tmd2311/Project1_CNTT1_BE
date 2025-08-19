package com.proshop.auth.dto.response;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseStatus {

  public static final String SUCCESS_LABEL = "Success";
  public static final String PERMISSION_DENIED_LABEL = "Permission Denied";
  public static String SUCCESS_CODE = "200";
  public static String PERMISSION_DENIED_CODE = "403";
  public static String PERMISSION_DENIED_MESSAGES = "Không có quyền thực hiện chức năng này";
  public static String SUCCESS_MESSAGE = "Thành công";
  public static final ResponseStatus SUCCESS_STATUS = new ResponseStatus(SUCCESS_CODE,
      SUCCESS_MESSAGE, SUCCESS_LABEL);
  public static String GENERAL_ERROR_MESSAGE = "Error happened";
  public static String GENERAL_ERROR_CODE = "40000";
  private String code;
  private String message;

  private String label;

  public ResponseStatus() {
  }

  public ResponseStatus(String code, String message, String label) {
    this.code = code;
    this.message = message;
    this.label = label;
  }

}

