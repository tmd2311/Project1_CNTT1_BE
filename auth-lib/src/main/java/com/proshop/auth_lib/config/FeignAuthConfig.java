package com.proshop.auth_lib.config;

import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "feign.RequestInterceptor")
public class FeignAuthConfig {

  @Bean
  public RequestInterceptor feignAuthInterceptor() {
    return new FeignAuthInterceptor();
  }
}
