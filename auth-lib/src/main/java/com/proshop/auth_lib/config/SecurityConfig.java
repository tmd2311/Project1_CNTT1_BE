package com.proshop.auth_lib.config;

import com.proshop.auth_lib.filter.JwtAuthenticationFilter;
import com.proshop.auth_lib.utils.JwtUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

  private static final String[] PUBLIC_URLS = {
      "/",
      "/error",
      "/favicon.ico",
      "/*/*.png",
      "/*/*.gif",
      "/*/*.svg",
      "/*/*.jpg",
      "/*/*.html",
      "/*/*.css",
      "/*/*.js",
      "/api/auth/**"   // auth-service endpoints
  };

  private static final List<String> ALLOWED_METHODS = List.of(
      "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

  private static final List<String> ALLOWED_HEADERS = List.of(
      "Authorization", "Content-Type");

  private static final List<String> EXPOSE_HEADERS = List.of(
      "Authorization");

  /**
   * Đăng ký JwtAuthenticationFilter như một bean.
   */
  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil) {
    return new JwtAuthenticationFilter(jwtUtil);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      CorsConfigurationSource corsConfigurationSource,
      JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

    logger.debug("Configuring security filter chain");

    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(PUBLIC_URLS).permitAll()
            .anyRequest().authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    logger.debug("Security filter chain configuration completed");
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    logger.debug("Configuring cors configuration source");

    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of("*")); // hỗ trợ wildcard
    configuration.setAllowedMethods(ALLOWED_METHODS);
    configuration.setAllowedHeaders(ALLOWED_HEADERS);
    configuration.setExposedHeaders(EXPOSE_HEADERS);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    logger.debug("Configuring cors configuration source completed");
    return source;
  }
}
