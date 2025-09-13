package com.proshop.auth_lib.config;

import com.proshop.auth_lib.exceptions.PrivateKeyInitializationException;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class JwtConfig {

  @Value("${jwt.public-key-path}")
  private Resource publicKeyResource;

  @Getter
  @Value("${jwt.expiration}")
  private long expirationTime;

  @Getter
  private PublicKey publicKey;

  @PostConstruct
  public void init() {
    try {
      this.publicKey = readPublicKey(publicKeyResource);
    } catch (Exception e) {
      throw new PrivateKeyInitializationException("Failed to load private key", e);
    }
  }

  private PublicKey readPublicKey(Resource resource) throws Exception {
    String key = readKeyFromPem(resource);
    byte[] decoded = Base64.getDecoder().decode(key);
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
    return KeyFactory.getInstance("RSA").generatePublic(keySpec);
  }

  private String readKeyFromPem(Resource resource) throws Exception {
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
      StringBuilder keyBuilder = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.contains("BEGIN") || line.contains("END")) {
          continue;
        }
        keyBuilder.append(line.trim());
      }
      return keyBuilder.toString();
    } catch (IOException e) {
      throw new PrivateKeyInitializationException("Failed to read key from PEM file", e);
    }
  }
}
