package com.proshop.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

  @Bean
  public CorsWebFilter corsWebFilter() {
    CorsConfiguration config = new CorsConfiguration();

    // Allow credentials
    config.setAllowCredentials(true);

    // Allowed origins - specify your frontend origins
    config.setAllowedOriginPatterns(Arrays.asList(
        "http://localhost:*",
        "http://103.90.225.90:*",
        "https://*"
    ));

    // Allowed methods
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

    // Allowed headers
    config.setAllowedHeaders(List.of("*"));

    // Exposed headers
    config.setExposedHeaders(Arrays.asList(
        "Authorization",
        "Content-Type",
        "X-Total-Count",
        "Access-Control-Allow-Origin",
        "Access-Control-Allow-Credentials"
    ));

    // Max age for preflight requests
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return new CorsWebFilter(source);
  }
}
