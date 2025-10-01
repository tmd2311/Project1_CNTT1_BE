package com.proshop.auth_lib.utils;

import com.proshop.auth_lib.config.JwtConfig;
import com.proshop.auth_lib.exceptions.ResException;
import com.proshop.auth_lib.utils.enums.ResErrorCode;
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
}
