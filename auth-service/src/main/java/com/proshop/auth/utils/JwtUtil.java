package com.proshop.auth.utils;

import static com.proshop.auth.utils.enums.UserStatus.INACTIVE;

import com.proshop.auth.config.JwtConfig;
import com.proshop.auth.entity.UserEntity;
import com.proshop.auth.repository.UserRepository;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtUtil {

  private final JwtConfig jwtConfig;
  private final UserRepository userRepository;

  private static void buildResponseAuthorize(HttpServletResponse response,
      ResErrorCode unauthorized)
      throws java.io.IOException {
    response.addHeader("Content-Type", "application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    PrintWriter printWriter = response.getWriter();
    String message = "{\"status\":{\"code\":\"%s\",\"message\":\"%s\",\"label\":\"%s\"},\"data\":null}";
    printWriter.println(String.format(message, unauthorized.code(),
        unauthorized.message(), unauthorized.label()));
    printWriter.close();
  }

  public String generateToken(String username, Map<String, Object> claims) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + jwtConfig.getExpirationTime());

    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .addClaims(claims)
        .signWith(jwtConfig.getPrivateKey(), SignatureAlgorithm.RS256)
        .compact();
  }

  public boolean validateToken(String token, HttpServletResponse response) throws IOException {
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(jwtConfig.getPublicKey())
          .build()
          .parseClaimsJws(token)
          .getBody();

      String userCode = claims.get("user_code").toString();

      // check account lock
      UserEntity entity = userRepository.findByCode(userCode)
          .orElseThrow(() -> new ResException(ResErrorCode.UNAUTHORIZED));
      if (entity.getStatus().equals(INACTIVE.name())) {
        buildResponseAuthorize(response, ResErrorCode.ACCOUNT_BLOCKED);
        return false;
      }
      // check account deleted
      else if (Boolean.TRUE.equals(entity.getDeleted())) {
        buildResponseAuthorize(response, ResErrorCode.ACCOUNT_DELETED);
        return false;
      }
    } catch (ExpiredJwtException e) {
      buildResponseAuthorize(response, ResErrorCode.TOKEN_EXPIRED);
      return false;
    } catch (Exception e) {
      buildResponseAuthorize(response, ResErrorCode.UNAUTHORIZED);
      return false;
    }
    return true;
  }

  public String getAccountFromToken(String token) {
    try{
      String account = Jwts.parserBuilder()
          .setSigningKey(jwtConfig.getPublicKey())
          .build()
          .parseClaimsJws(token)
          .getBody()
          .getSubject();
      log.debug("account = {}", account);
      return account;
    } catch (Exception e) {
      log.error("failed to get account from token", e);
      throw new ResException(ResErrorCode.UNAUTHORIZED);
    }
  }

  public Claims getClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(jwtConfig.getPublicKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public String getUserCodeFromToken(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(jwtConfig.getPublicKey())
          .build()
          .parseClaimsJws(token)
          .getBody();

      String userCode = claims.get("user_code", String.class);
      log.debug("Extracted user_code from token: {}", userCode);
      return userCode;
    } catch (Exception e) {
      log.error("Failed to extract user_code from token", e);
      throw new ResException(ResErrorCode.UNAUTHORIZED);
    }
  }

  // ==================== REFRESH TOKEN METHODS ====================

  /**
   * Generate refresh token with 7-day expiration
   * @param username Username (account)
   * @param claims Additional claims
   * @return Refresh token string
   */
  public String generateRefreshToken(String username, Map<String, Object> claims) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + jwtConfig.getRefreshExpirationTime());

    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .addClaims(claims)
        .claim("token_type", "refresh")  // Mark as refresh token
        .signWith(jwtConfig.getPrivateKey(), SignatureAlgorithm.RS256)
        .compact();
  }

  /**
   * Validate refresh token
   * @param refreshToken Refresh token string
   * @return true if valid, throws exception otherwise
   */
  public boolean validateRefreshToken(String refreshToken) {
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(jwtConfig.getPublicKey())
          .build()
          .parseClaimsJws(refreshToken)
          .getBody();

      // Check if it's actually a refresh token
      String tokenType = claims.get("token_type", String.class);
      if (!"refresh".equals(tokenType)) {
        log.error("Token is not a refresh token");
        throw new ResException(ResErrorCode.TOKEN_INVALID);
      }

      String userCode = claims.get("user_code", String.class);

      // Check account status
      UserEntity entity = userRepository.findByCode(userCode)
          .orElseThrow(() -> new ResException(ResErrorCode.UNAUTHORIZED));

      if (entity.getStatus().equals(INACTIVE.name())) {
        throw new ResException(ResErrorCode.ACCOUNT_BLOCKED);
      } else if (Boolean.TRUE.equals(entity.getDeleted())) {
        throw new ResException(ResErrorCode.ACCOUNT_DELETED);
      }

      return true;
    } catch (ExpiredJwtException e) {
      log.error("Refresh token expired", e);
      throw new ResException(ResErrorCode.TOKEN_EXPIRED);
    } catch (ResException e) {
      throw e;
    } catch (Exception e) {
      log.error("Invalid refresh token", e);
      throw new ResException(ResErrorCode.TOKEN_INVALID);
    }
  }

  /**
   * Get claims from refresh token
   * @param refreshToken Refresh token string
   * @return Claims
   */
  public Claims getClaimsFromRefreshToken(String refreshToken) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(jwtConfig.getPublicKey())
          .build()
          .parseClaimsJws(refreshToken)
          .getBody();
    } catch (ExpiredJwtException e) {
      log.error("Refresh token expired", e);
      throw new ResException(ResErrorCode.TOKEN_EXPIRED);
    } catch (Exception e) {
      log.error("Invalid refresh token", e);
      throw new ResException(ResErrorCode.TOKEN_INVALID);
    }
  }

  /**
   * Get user code from refresh token
   * @param refreshToken Refresh token string
   * @return User code
   */
  public String getUserCodeFromRefreshToken(String refreshToken) {
    try {
      Claims claims = getClaimsFromRefreshToken(refreshToken);
      String userCode = claims.get("user_code", String.class);
      log.debug("Extracted user_code from refresh token: {}", userCode);
      return userCode;
    } catch (Exception e) {
      log.error("Failed to extract user_code from refresh token", e);
      throw new ResException(ResErrorCode.UNAUTHORIZED);
    }
  }
}