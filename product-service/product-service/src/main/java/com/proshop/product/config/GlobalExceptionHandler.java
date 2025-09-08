package com.proshop.product.config;

import com.proshop.product.dto.response.ErrorResponse;
import com.proshop.product.dto.response.ResponseStatus;
import com.proshop.product.exceptions.ResException;
import com.proshop.product.utils.enums.ResErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

  private static final String PREFIX = "Error: ";

  @ExceptionHandler(value = ResException.class)
  public Object handleAppException(HttpServletRequest request, ResException re)
      throws IOException {
    ErrorResponse<Object> errorResponse = new ErrorResponse();
    ResponseStatus responseStatus = new ResponseStatus();
    responseStatus.setCode(re.getCode());
    responseStatus.setLabel(re.getLabel());
    responseStatus.setMessage(re.getMessage());
    errorResponse.setStatus(responseStatus);
    errorResponse.setData(re.getData());
    log.error(PREFIX, re);
    return new ResponseEntity<>(errorResponse, re.getStatus());
  }

  @ExceptionHandler(value = AccessDeniedException.class)
  public Object handleAccessDeniedException(AccessDeniedException re)
      throws IOException {
    ErrorResponse<Object> errorResponse = new ErrorResponse();
    ResponseStatus responseStatus = new ResponseStatus();
    responseStatus.setCode(ResponseStatus.PERMISSION_DENIED_CODE);
    responseStatus.setLabel(ResponseStatus.PERMISSION_DENIED_LABEL);
    responseStatus.setMessage(ResponseStatus.PERMISSION_DENIED_MESSAGES);
    errorResponse.setStatus(responseStatus);
    log.error(PREFIX, re);
    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(value = Exception.class)
  public Object handleException(HttpServletRequest request, Exception re)
      throws IOException {
    ErrorResponse<Object> errorResponse = new ErrorResponse();
    ResponseStatus responseStatus = new ResponseStatus();
    responseStatus.setCode(ResErrorCode.GENERAL_ERROR.code());
    responseStatus.setLabel(ResErrorCode.GENERAL_ERROR.label());
    responseStatus.setMessage("Có lỗi xảy ra, xin vui lòng thử lại sau ít phút");
    errorResponse.setStatus(responseStatus);
    log.error(PREFIX, re);
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = MissingServletRequestParameterException.class)
  public Object handleMissingParamException(HttpServletRequest request,
      MissingServletRequestParameterException re)
      throws IOException {
    ErrorResponse<Object> errorResponse = new ErrorResponse();
    ResponseStatus responseStatus = new ResponseStatus();
    responseStatus.setCode(ResErrorCode.MISS_PARAM.code());
    responseStatus.setLabel(ResErrorCode.MISS_PARAM.label());
    responseStatus.setMessage("Miss params");
    errorResponse.setStatus(responseStatus);
    log.error(PREFIX, re);
    return new ResponseEntity<>(errorResponse, ResErrorCode.MISS_PARAM.status());
  }

  public boolean exitsCode(String code) {
    List<String> resCode = Arrays.asList(ResErrorCode.ACCOUNT_BLOCKED.code(),
        ResErrorCode.PASS_NOT_EQUAL.code(), ResErrorCode.SAME_PASSWORD.code(),
        ResErrorCode.OLD_PASSWORD_NOT_VALID.code(), ResErrorCode.INVALID_USER_PASS.code());
    return resCode.contains(code);
  }

}
