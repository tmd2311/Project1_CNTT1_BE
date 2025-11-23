package com.proshop.auth_lib.config;

import com.proshop.auth_lib.filter.JwtAuthenticationFilter;
import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.utils.SecurityExceptionHandler;
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
            // ============================================
            // PUBLIC ENDPOINTS (no token required)
            // ============================================
            .requestMatchers(PUBLIC_URLS).permitAll()

            .requestMatchers("/actuator/**").permitAll()

            // Product, Brand, Category, SKU - GET requests are public
            .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/brand/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/category/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/sku/**").permitAll()

            // SKU sale-price operations - permitAll for internal service calls (sale-service scheduler)
            .requestMatchers(HttpMethod.PUT, "/api/sku/*/sale-price").permitAll()
            .requestMatchers(HttpMethod.PUT, "/api/sku/*/revert-price").permitAll()
            .requestMatchers(HttpMethod.PUT, "/api/sku/stock/*").permitAll() // for order-service

            // Statistics - public for users to view
            .requestMatchers(HttpMethod.GET, "/api/products/statistics/**").permitAll()

            // Reviews - GET and search are public
            .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/answers/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/reviews/search").permitAll()

            // Order - best sellers is public
            .requestMatchers(HttpMethod.GET, "/api/order/best-sellers").permitAll()

            // Sales - GET requests are public
            .requestMatchers(HttpMethod.GET, "/api/v1/sales/**").permitAll()

            // Vouchers - GET requests for public vouchers
            .requestMatchers(HttpMethod.GET, "/api/v1/vouchers/active").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/vouchers/code/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/vouchers/{id}").permitAll()

            // ============================================
            // ADMIN ENDPOINTS (admin role required)
            // ============================================
            // Product, Brand, Category, SKU - Create, Update, Delete require ADMIN
            .requestMatchers(HttpMethod.POST, "/api/product/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/product/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/product/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/delete").hasRole("ADMIN")

            .requestMatchers(HttpMethod.POST, "/api/brand/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/brand/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/brand/**").hasRole("ADMIN")

            .requestMatchers(HttpMethod.POST, "/api/category/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/category/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/category/**").hasRole("ADMIN")

            .requestMatchers(HttpMethod.POST, "/api/sku/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/sku/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/sku/**").hasRole("ADMIN")

            // Cart Admin endpoints
            .requestMatchers("/api/cart/admin/**").hasRole("ADMIN")

            // Order Admin endpoints
            .requestMatchers("/api/order/admin/**").hasRole("ADMIN")

            // Review Admin endpoints
            .requestMatchers(HttpMethod.PUT, "/api/reviews/{id}/status").hasRole("ADMIN")

            // Sales Admin endpoints - Create, Update, Delete require ADMIN
            .requestMatchers(HttpMethod.POST, "/api/v1/sales/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/v1/sales/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/sales/**").hasRole("ADMIN")

            // Vouchers Admin endpoints - Create, Update, Delete require ADMIN
            .requestMatchers(HttpMethod.POST, "/api/v1/vouchers").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/v1/vouchers/{id}/users").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/vouchers/{id}/users/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/v1/vouchers").hasRole("ADMIN")

            // ============================================
            // AUTHENTICATED ENDPOINTS (token required)
            // ============================================
            // Cart - user endpoints require authentication
            .requestMatchers("/api/cart/**").authenticated()

            // Order - user endpoints require authentication
            .requestMatchers("/api/order/**").authenticated()

            // Reviews - Create, Update, Delete require authentication
            .requestMatchers(HttpMethod.POST, "/api/reviews").authenticated()
            .requestMatchers(HttpMethod.PUT, "/api/reviews/**").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").authenticated()

            // Vouchers - User endpoints require authentication
            .requestMatchers(HttpMethod.POST, "/api/v1/vouchers/validate").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/v1/vouchers/apply").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/v1/vouchers/cancel/**").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/v1/vouchers/user/**").authenticated()

            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
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
}
