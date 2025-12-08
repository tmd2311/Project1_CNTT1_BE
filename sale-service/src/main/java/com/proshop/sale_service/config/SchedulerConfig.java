package com.proshop.sale_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration để enable Spring Scheduling
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
    // Spring sẽ tự động scan và chạy các @Scheduled methods
}
