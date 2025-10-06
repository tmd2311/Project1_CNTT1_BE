package com.proshop.auth.config;

import com.proshop.exceptionlib.exceptions.PrivateKeyInitializationException;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class JwtConfig {
  @Value("${jwt.private-key-path}")
  private Resource privateKeyResource;

  @Value("${jwt.public-key-path}")
  private Resource publicKeyResource;

  @Getter
  @Value("${jwt.expiration}")
  private long expirationTime;

  @Getter
  private PrivateKey privateKey;

  @Getter
  private PublicKey publicKey;

  @PostConstruct
  public void init() {
    try {
      this.privateKey = readPrivateKey(privateKeyResource);
      this.publicKey = readPublicKey(publicKeyResource);
    } catch (Exception e) {
      throw new PrivateKeyInitializationException("Failed to load private key", e);
    }
  }
  private PrivateKey readPrivateKey(Resource resource) throws Exception {
    String key = readKeyFromPem(resource);
    byte[] decoded = Base64.getDecoder().decode(key);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
    return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
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
