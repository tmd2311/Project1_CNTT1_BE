package com.proshop.auth_lib.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class FeignAuthInterceptor implements RequestInterceptor {
  @Override
  public void apply(RequestTemplate template) {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    if (attributes != null) {
      HttpServletRequest request = attributes.getRequest();
      String authHeader = request.getHeader("Authorization");
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        template.header("Authorization", authHeader);
        log.debug("✅ Forwarded Authorization header to Feign client");
      } else {
        log.warn("⚠️ No Authorization header found in current request");
      }
    } else {
      log.warn("⚠️ No RequestAttributes available - cannot forward token");
    }
  }
}
