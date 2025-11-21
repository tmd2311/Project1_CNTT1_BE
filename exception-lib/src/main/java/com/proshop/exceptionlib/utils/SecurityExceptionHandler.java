package com.proshop.exceptionlib.utils;

import com.proshop.exceptionlib.enums.ResErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for handling Spring Security exceptions
 * Builds error responses in ResException format for authentication and authorization failures
 */
@Slf4j
public class SecurityExceptionHandler {

  private SecurityExceptionHandler() {
    // Private constructor to prevent instantiation
  }

  /**
   * Build error response in ResException format
   *
   * @param request HttpServletRequest for logging context
   * @param response HttpServletResponse to write to
   * @param errorCode ResErrorCode containing error details
   * @param exception Original exception for logging
   * @throws IOException if writing response fails
   */
  public static void buildErrorResponse(HttpServletRequest request, HttpServletResponse response,
      ResErrorCode errorCode, Exception exception) throws IOException {

    // Log error details for debugging
    log.error("Security exception occurred:");
    log.error("  Error Code: {} - {}", errorCode.code(), errorCode.message());
    log.error("  Request: {} {}", request.getMethod(), request.getRequestURI());
    log.error("  Remote IP: {}", request.getRemoteAddr());
    log.error("  User-Agent: {}", request.getHeader("User-Agent"));

    if (exception != null) {
      log.error("  Exception Type: {}", exception.getClass().getSimpleName());
      log.error("  Exception Message: {}", exception.getMessage());
      log.error("  Stack Trace:", exception);
    }

    // Build response
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(errorCode.status().value());

    String jsonResponse = String.format(
        "{\"status\":{\"code\":\"%s\",\"message\":\"%s\",\"label\":\"%s\"},\"data\":null}",
        errorCode.code(),
        errorCode.message(),
        errorCode.label()
    );

    response.getWriter().write(jsonResponse);
    response.getWriter().flush();
  }

  /**
   * Handle authentication entry point - for missing or invalid authentication
   *
   * @param request HttpServletRequest for logging context
   * @param response HttpServletResponse to write to
   * @param exception Original authentication exception
   * @throws IOException if writing response fails
   */
  public static void handleAuthenticationException(HttpServletRequest request,
      HttpServletResponse response, Exception exception) throws IOException {
    log.warn("Authentication failed for request: {} {}", request.getMethod(), request.getRequestURI());
    buildErrorResponse(request, response, ResErrorCode.UNAUTHORIZED, exception);
  }

  /**
   * Handle access denied - for insufficient permissions
   *
   * @param request HttpServletRequest for logging context
   * @param response HttpServletResponse to write to
   * @param exception Original access denied exception
   * @throws IOException if writing response fails
   */
  public static void handleAccessDeniedException(HttpServletRequest request,
      HttpServletResponse response, Exception exception) throws IOException {
    log.warn("Access denied for request: {} {}", request.getMethod(), request.getRequestURI());
    buildErrorResponse(request, response, ResErrorCode.PERMISSION_DENIED, exception);
  }
}
