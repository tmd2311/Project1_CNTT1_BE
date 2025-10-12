package com.proshop.auth_lib.utils;

import com.proshop.auth_lib.config.JwtConfig;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.PrivateKeyInitializationException;
import com.proshop.exceptionlib.exceptions.ResException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

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
      throw new ResException(ResErrorCode.TOKEN_EXPIRED);
    } catch (Exception e) {
      log.error("Invalid token", e);
      throw new ResException(ResErrorCode.TOKEN_INVALID);
    }
  }

  public String getAccountFromToken(String token) {
    return getClaims(token).getSubject();
  }

  public String getUserCodeFromToken(String token) {
    return getClaims(token).get("user_code", String.class);
  }

  public List<String> extractRoles(String token) {
    Object rolesObj = getClaims(token).get("roles");
    if (rolesObj instanceof List<?>) {
      return ((List<?>) rolesObj).stream()
          .map(Object::toString)
          .toList();
    }
    return List.of();
  }

  public Long getUserIDFromToken(String token) { return getClaims(token).get("userId", Long.class); }

  public boolean validateToken(String token) {
    try {
      getClaims(token);
      return true;
    } catch (PrivateKeyInitializationException e) {
      return false;
    }
  }
}