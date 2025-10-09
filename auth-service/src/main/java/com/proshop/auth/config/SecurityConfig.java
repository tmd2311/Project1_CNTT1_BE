package com.proshop.auth.config;

import com.proshop.auth.filter.JwtAuthenticationFilter;
import com.proshop.auth.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
      "/api/auth/**",
      "/actuator/health"
  };

  private static final String ALL_ORIGINS = "*";

  private static final String ALL_PATTERNS = "/**";

  private static final List<String> ALLOWED_METHODS = List.of(
      "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

  private static final List<String> ALLOWED_HEADERS = List.of(
      "authorization", "content-type");

  private final UserRepository userRepository;

  private final JwtAuthenticationFilter tokenAuthenticationFilter;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;

  @Bean
  public UserDetailsService userDetailsService() {
    return account -> userRepository
        .findByAccountWithRoles(account)
        .orElseThrow(
            () -> new UsernameNotFoundException(String.format("User: %s, not found", account)));
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      JwtAuthenticationFilter jwtAuthenticationFilter)
      throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(PUBLIC_URLS).permitAll()
            .requestMatchers("/api/v1/user/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .oauth2Login(oauth2 -> oauth2
            .successHandler(oAuth2SuccessHandler))
        .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    logger.debug("Security filter chain configuration completed");
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    logger.debug("Configuring cors configuration source");

    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:3000"));
    configuration.setAllowedMethods(ALLOWED_METHODS);
    configuration.setAllowedHeaders(ALLOWED_HEADERS);
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration(ALL_PATTERNS, configuration);

    logger.debug("Configuring cors configuration source completed");
    return source;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {return  new BCryptPasswordEncoder();}
}
