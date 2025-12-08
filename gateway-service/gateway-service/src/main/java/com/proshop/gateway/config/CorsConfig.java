package com.proshop.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration for Spring Cloud Gateway
 *
 * File duy nhất cần config CORS cho toàn bộ hệ thống
 * Không cần thêm @CrossOrigin ở controller
 *
 * @author ProShop Team
 */
@Configuration
public class CorsConfig {

  @Bean
  public CorsWebFilter corsWebFilter() {
    CorsConfiguration config = new CorsConfiguration();

    // ====================================
    // ALLOW CREDENTIALS
    // ====================================
    config.setAllowCredentials(true);

    // ====================================
    // ALLOWED ORIGINS
    // ====================================
    config.setAllowedOriginPatterns(Arrays.asList(
        "http://localhost:*",
        "http://127.0.0.1:*",
        "http://103.90.225.90:*",
        "https://103.90.225.90:*",
        "https://*.vercel.app",
        "https://*.netlify.app",
        "https://*.railway.app"
    ));

    // ====================================
    // ALLOWED METHODS
    // ====================================
    config.setAllowedMethods(Arrays.asList(
        "GET",
        "POST",
        "PUT",
        "PATCH",
        "DELETE",
        "OPTIONS",
        "HEAD"
    ));

    // ====================================
    // ALLOWED HEADERS
    // ====================================
    config.setAllowedHeaders(List.of("*"));

    // ====================================
    // EXPOSED HEADERS - QUAN TRỌNG!
    // ====================================
    config.setExposedHeaders(Arrays.asList(
        "Authorization",
        "Content-Type",
        "Content-Length",
        "Content-Disposition",
        "X-Total-Count",
        "X-Total-Pages",
        "Access-Control-Allow-Origin",
        "Access-Control-Allow-Credentials",
        "Access-Control-Allow-Headers",
        "Access-Control-Allow-Methods",
        "Set-Cookie"
    ));

    // ====================================
    // MAX AGE
    // ====================================
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return new CorsWebFilter(source);
  }
}
