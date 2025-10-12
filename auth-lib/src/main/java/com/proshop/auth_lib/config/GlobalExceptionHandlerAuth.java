package com.proshop.auth_lib.config;



import com.proshop.auth_lib.dto.response.ErrorResponse;
import com.proshop.auth_lib.dto.response.ResponseStatus;
import com.proshop.exceptionlib.exceptions.ResException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import com.proshop.exceptionlib.enums.ResErrorCode;
import java.nio.file.AccessDeniedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice("com.proshop.auth_lib")
@Log4j2
public class GlobalExceptionHandlerAuth {

  private static final String PREFIX = "Error: ";

  @ExceptionHandler(value = ResException.class)
  public Object handleAppException(HttpServletRequest request, ResException re)
      throws IOException {
    ErrorResponse errorResponse = new ErrorResponse();
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
    ErrorResponse errorResponse = new ErrorResponse();
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
    ErrorResponse errorResponse = new ErrorResponse();
    ResponseStatus responseStatus = new ResponseStatus();
    responseStatus.setCode(ResErrorCode.GENERAL_ERROR.code());
    responseStatus.setLabel(ResErrorCode.GENERAL_ERROR.label());
    responseStatus.setMessage("Có lỗi xảy ra, xin vui lòng thử lại sau ít phút");
    errorResponse.setStatus(responseStatus);
    log.error(PREFIX, re);
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
