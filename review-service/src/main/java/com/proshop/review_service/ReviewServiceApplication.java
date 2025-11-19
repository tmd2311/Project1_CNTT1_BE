package com.proshop.review_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.proshop.review_service.client")
@SpringBootApplication(scanBasePackages = {
        "com.proshop.review_service",
        "com.proshop.auth_lib"
}) // hoặc package chứa AuthClient
public class ReviewServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReviewServiceApplication.class, args);
	}

}
