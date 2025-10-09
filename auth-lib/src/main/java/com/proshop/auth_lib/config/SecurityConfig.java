package com.proshop.auth_lib.config;

import com.proshop.auth_lib.exceptions.ResException;
import com.proshop.auth_lib.filter.JwtAuthenticationFilter;
import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.auth_lib.utils.enums.ResErrorCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil) {
    return new JwtAuthenticationFilter(jwtUtil);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

    logger.debug("Configuring security filter chain");

    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(PUBLIC_URLS).permitAll()
            .requestMatchers(HttpMethod.GET, "/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PATCH, "/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) -> {
              throw new ResException(ResErrorCode.UNAUTHORIZED);
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              throw new ResException(ResErrorCode.PERMISSION_DENIED);
            })
        );

    logger.debug("Security filter chain configuration completed");
    return http.build();
  }
}
