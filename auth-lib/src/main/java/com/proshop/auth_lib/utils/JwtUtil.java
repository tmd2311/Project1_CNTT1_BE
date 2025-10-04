package com.proshop.auth_lib.utils;


import com.proshop.auth_lib.config.JwtConfig;
import com.proshop.exceptionlib.exceptions.PrivateKeyInitializationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtUtil {

  private final JwtConfig jwtConfig;

  public Claims getClaims(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(jwtConfig.getPublicKey())
          .build()
          .parseClaimsJws(token)
          .getBody();
    } catch (ExpiredJwtException e) {
      log.error("Token expired", e);
      throw new PrivateKeyInitializationException("Token expired", e);
    } catch (Exception e) {
      log.error("Invalid token", e);
      throw new PrivateKeyInitializationException("Invalid token", e);
    }
  }

  public String getAccountFromToken(String token) {
    return getClaims(token).getSubject();
  }

  public String getUserCodeFromToken(String token) {
    return getClaims(token).get("user_code", String.class);
  }

  public boolean validateToken(String token) {
    try {
      getClaims(token);
      return true;
    } catch (PrivateKeyInitializationException e) {
      return false;
    }
  }
}