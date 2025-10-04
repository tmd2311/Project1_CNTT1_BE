package com.proshop.auth.config;


import com.proshop.auth.entity.UserEntity;
import com.proshop.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(AdminDataInitializer.class);
  @Lazy
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    if (userRepository.findByAccount("admin").isEmpty()) {
      logger.info("Initializing default admin user");

      UserEntity adminUser = new UserEntity();
      adminUser.setCode("ADMIN_001");
      adminUser.setAccount("admin");
      adminUser.setPasswordHash(passwordEncoder.encode("P@ssw0rd"));
      adminUser.setEmail("admin@example.com");
      adminUser.setStatus("ACTIVE");
      adminUser.setDeleted(false);

      userRepository.save(adminUser);

      logger.info("Default admin user created successfully");
    } else {
      logger.info("Admin user already exists");
    }
  }
}