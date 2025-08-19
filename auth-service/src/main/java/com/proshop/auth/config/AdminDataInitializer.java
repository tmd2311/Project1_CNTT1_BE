//package com.proshop.auth.config;
//
//
//import com.proshop.auth.entity.UserEntity;
//import com.proshop.auth.repository.UserRepository;
//import com.proshop.auth.utils.PasswordUtils;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class AdminDataInitializer implements CommandLineRunner {
//
//  private static final Logger logger = LoggerFactory.getLogger(AdminDataInitializer.class);
//  private final UserRepository userRepository;
//  private final PasswordUtils passwordUtils;
//
//  @Override
//  public void run(String... args) {
//    if (userRepository.findByAccount("Dung").isEmpty()) {
//      logger.info("Initializing default admin user");
//
//      UserEntity adminUser = new UserEntity();
//      adminUser.setCode("USER_002");
//      adminUser.setAccount("tmd2311");
//      adminUser.setPasswordHash(passwordUtils.hashPassword("Admin@123"));
//      adminUser.setEmail("dung@example.com");
//      adminUser.setStatus("ACTIVE");
//      adminUser.setDeleted(false);
//
//      userRepository.save(adminUser);
//
//      logger.info("Default admin user created successfully");
//    } else {
//      logger.info("Admin user already exists");
//    }
//  }
//}