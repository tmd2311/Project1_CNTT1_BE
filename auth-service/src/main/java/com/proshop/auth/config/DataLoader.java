package com.proshop.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proshop.auth.entity.RoleEntity;
import com.proshop.auth.entity.UserEntity;
import com.proshop.auth.entity.UserRoleEntity;
import com.proshop.auth.repository.RoleRepository;
import com.proshop.auth.repository.UserRepository;
import com.proshop.auth.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final UserRoleRepository userRoleRepository;
  private final PasswordEncoder passwordEncoder;
  private final ObjectMapper objectMapper;

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    logger.info("Seeding default users and roles...");

    // Tạo role nếu chưa có
    RoleEntity adminRole = roleRepository.findByCode("ADMIN")
        .orElseGet(() -> {
          RoleEntity role = new RoleEntity();
          role.setCode("ADMIN");
          role.setName("Administrator");
          role.setStatus("ACTIVE");
          return roleRepository.save(role);
        });

    RoleEntity customerRole = roleRepository.findByCode("CUSTOMER")
        .orElseGet(() -> {
          RoleEntity role = new RoleEntity();
          role.setCode("CUSTOMER");
          role.setName("Customer");
          role.setStatus("ACTIVE");
          return roleRepository.save(role);
        });

    // Nếu DB chưa có user thì seed
    if (userRepository.count() == 0) {
      for (int i = 1; i <= 10; i++) {
        UserEntity user = new UserEntity();
        user.setCode("U" + i);
        user.setAccount("user" + i);
        user.setUsername("user" + i);
        user.setEmail("user" + i + "@example.com");
        user.setFullName("User " + i);
        user.setPasswordHash(passwordEncoder.encode("password" + i));
        user.setStatus("ACTIVE");

        UserEntity savedUser = userRepository.save(user);

        // Gán role cho user
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUserEntity(savedUser);

        // set context_data = {} (jsonb)
        userRole.setContextData(objectMapper.createObjectNode());

        if (i <= 2) { // 2 user đầu tiên là ADMIN
          userRole.setRoleEntity(adminRole);
        } else if (i <= 4) { // user 3,4 là CUSTOMER
          userRole.setRoleEntity(customerRole);
        } else { // còn lại random
          userRole.setRoleEntity(i % 2 == 0 ? adminRole : customerRole);
        }

        userRoleRepository.save(userRole);
      }
      logger.info("Seeded 10 users successfully");
    } else {
      logger.info("Users already exist, skipping seeding");
    }
  }
}
