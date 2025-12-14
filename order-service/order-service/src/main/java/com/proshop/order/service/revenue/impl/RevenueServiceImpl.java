package com.proshop.order.service.revenue.impl;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.order.dto.response.DailyRevenueResponse;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.ResponseStatus;
import com.proshop.order.dto.response.RevenueReportResponse;
import com.proshop.order.entity.OrderEntity;
import com.proshop.order.entity.RevenueEntity;
import com.proshop.order.repository.OrderRepository;
import com.proshop.order.repository.RevenueRepository;
import com.proshop.order.service.revenue.RevenueService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevenueServiceImpl implements RevenueService {

    private final OrderRepository orderRepository;
    private final RevenueRepository revenueRepository;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public int calculateAndUpdateRevenue() {
        log.info("Starting revenue calculation job...");

        // Get all completed orders not yet included in revenue
        List<OrderEntity> orders = orderRepository.findCompletedOrdersNotInRevenue();

        if (orders.isEmpty()) {
            log.info("No new completed orders to process");
            return 0;
        }

        log.info("Found {} completed orders to process", orders.size());

        // Group orders by date (from createdAt)
        Map<LocalDate, List<OrderEntity>> ordersByDate = orders.stream()
            .collect(Collectors.groupingBy(order -> order.getCreatedAt().toLocalDate()));

        // Process each date
        for (Map.Entry<LocalDate, List<OrderEntity>> entry : ordersByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<OrderEntity> dateOrders = entry.getValue();

            // Calculate total revenue for this date
            BigDecimal dailyRevenue = dateOrders.stream()
                .map(OrderEntity::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            int orderCount = dateOrders.size();

            log.info("Date {}: {} orders, revenue: {}", date, orderCount, dailyRevenue);

            // Update or create revenue record
            Optional<RevenueEntity> existingRevenue = revenueRepository.findByRevenueDate(date);

            if (existingRevenue.isPresent()) {
                // Update existing record
                RevenueEntity revenue = existingRevenue.get();
                revenue.setTotalRevenue(revenue.getTotalRevenue().add(dailyRevenue));
                revenue.setOrderCount(revenue.getOrderCount() + orderCount);
                revenueRepository.save(revenue);
                log.info("Updated revenue for {}", date);
            } else {
                // Create new record
                RevenueEntity revenue = RevenueEntity.builder()
                    .revenueDate(date)
                    .totalRevenue(dailyRevenue)
                    .orderCount(orderCount)
                    .build();
                revenueRepository.save(revenue);
                log.info("Created new revenue record for {}", date);
            }

            // Mark orders as included in revenue
            dateOrders.forEach(order -> order.setIncludedInRevenue(true));
            orderRepository.saveAll(dateOrders);
        }

        log.info("Revenue calculation completed. Processed {} orders across {} days",
            orders.size(), ordersByDate.size());

        return orders.size();
    }

    @Override
    public GeneralResponse<RevenueReportResponse> getRevenueReport(
            LocalDate startDate,
            LocalDate endDate,
            HttpServletRequest request) {

        checkAdminRole(request);
        log.info("Getting revenue report from {} to {}", startDate, endDate);

        // Default to current month if not specified
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // Get total revenue and order count
        BigDecimal totalRevenue = revenueRepository.sumRevenueByDateRange(startDate, endDate);
        Long totalOrders = revenueRepository.sumOrderCountByDateRange(startDate, endDate);

        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }
        if (totalOrders == null) {
            totalOrders = 0L;
        }

        // Calculate average order value
        BigDecimal averageOrderValue = BigDecimal.ZERO;
        if (totalOrders > 0) {
            averageOrderValue = totalRevenue.divide(
                new BigDecimal(totalOrders),
                2,
                RoundingMode.HALF_UP
            );
        }

        RevenueReportResponse report = RevenueReportResponse.builder()
            .startDate(startDate)
            .endDate(endDate)
            .totalRevenue(totalRevenue)
            .totalOrders(totalOrders)
            .averageOrderValue(averageOrderValue)
            .build();

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, report, null);
    }

    @Override
    public GeneralResponse<List<DailyRevenueResponse>> getMonthlyRevenue(
            int year,
            int month,
            HttpServletRequest request) {

        checkAdminRole(request);
        log.info("Getting monthly revenue for {}/{}", year, month);

        List<RevenueEntity> revenues = revenueRepository.findRevenueByMonth(year, month);

        List<DailyRevenueResponse> dailyRevenues = revenues.stream()
            .map(revenue -> DailyRevenueResponse.builder()
                .date(revenue.getRevenueDate())
                .revenue(revenue.getTotalRevenue())
                .orderCount(revenue.getOrderCount())
                .build())
            .collect(Collectors.toList());

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, dailyRevenues, null);
    }

    /**
     * Check if user has Admin role from JWT token
     */
    private void checkAdminRole(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Missing or invalid Authorization header");
            throw new ResException(ResErrorCode.UNAUTHORIZED);
        }

        String token = authHeader.substring(7).trim();

        try {
            List<String> roles = jwtUtil.extractRoles(token);
            boolean isAdmin = roles.stream().anyMatch(role -> role.equalsIgnoreCase("Admin"));

            if (!isAdmin) {
                throw new ResException(ResErrorCode.PERMISSION_DENIED,
                    "Bạn không có quyền truy cập tài nguyên này (Admin only)");
            }

            log.info("Admin role verified");
        } catch (ResException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to extract roles from token: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.TOKEN_INVALID);
        }
    }
}
