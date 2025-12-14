package com.proshop.order.job;

import com.proshop.order.service.revenue.RevenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RevenueCalculationJob {

    private final RevenueService revenueService;


    @Scheduled(cron = "0 */5 * * * *") // Mỗi 5 phút
    public void calculateRevenue() {
        log.info("===== REVENUE CALCULATION JOB STARTED =====");

        try {
            int processedOrders = revenueService.calculateAndUpdateRevenue();
            log.info("Revenue calculation completed. Processed {} orders", processedOrders);
        } catch (Exception e) {
            log.error("Error during revenue calculation: {}", e.getMessage(), e);
        }

        log.info("===== REVENUE CALCULATION JOB FINISHED =====");
    }
}
