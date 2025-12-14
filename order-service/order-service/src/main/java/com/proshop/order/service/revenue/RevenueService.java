package com.proshop.order.service.revenue;

import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.RevenueReportResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.List;

public interface RevenueService {

    /**
     * Calculate and update revenue from completed orders (for scheduled job)
     * @return Number of orders processed
     */
    int calculateAndUpdateRevenue();

    /**
     * Get revenue report for date range (ADMIN only)
     */
    GeneralResponse<RevenueReportResponse> getRevenueReport(
        LocalDate startDate,
        LocalDate endDate,
        HttpServletRequest request);

    /**
     * Get daily revenue for current month (ADMIN only)
     */
    GeneralResponse<List<com.proshop.order.dto.response.DailyRevenueResponse>> getMonthlyRevenue(
        int year,
        int month,
        HttpServletRequest request);
}
