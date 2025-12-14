package com.proshop.auth.config;

import com.proshop.auth.filter.AServiceJwtAuthenticationFilter;
import com.proshop.exceptionlib.utils.SecurityExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
      "/api/auth/**",
      "/actuator/health",
      "/api/v1"
  };

  private final AServiceJwtAuthenticationFilter tokenAuthenticationFilter;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            // Public URLs (static resources, auth endpoints)
            .requestMatchers(PUBLIC_URLS).permitAll()

            // ============================================
            // ADMIN ENDPOINTS (must be before general rules)
            // ============================================
            .requestMatchers(HttpMethod.GET, "/api/v1/user/getAllUser").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/v1/user/{id}/activate").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/v1/user/{id}/deactivate").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/user/**").hasRole("ADMIN")

            // ============================================
            // AUTHENTICATED USER ENDPOINTS
            // ============================================
            // User profile endpoint - authenticated users only
            .requestMatchers(HttpMethod.PUT, "/api/v1/user/profile").authenticated()

            // Allow authenticated users to view user info (including their own)
            .requestMatchers(HttpMethod.GET, "/api/v1/**").authenticated()

            .anyRequest().authenticated())
        .oauth2Login(oauth2 -> oauth2
            .successHandler(oAuth2SuccessHandler))
        .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) -> {
              SecurityExceptionHandler.handleAuthenticationException(request, response, authException);
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              SecurityExceptionHandler.handleAccessDeniedException(request, response, accessDeniedException);
            })
        );

    logger.debug("Security filter chain configuration completed");
    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {return  new BCryptPasswordEncoder();}
}