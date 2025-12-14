package com.proshop.order.controller;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.order.dto.response.DailyRevenueResponse;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.ResponseStatus;
import com.proshop.order.dto.response.RevenueReportResponse;
import com.proshop.order.service.revenue.RevenueService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/revenue")
@RequiredArgsConstructor
public class RevenueController {

    private final RevenueService revenueService;
    private final JwtUtil jwtUtil;

    /**
     * Get revenue report for date range (ADMIN only)
     * GET /api/revenue/report?startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/report")
    public ResponseEntity<GeneralResponse<RevenueReportResponse>> getRevenueReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest request) {

        log.info("Getting revenue report from {} to {} (admin)", startDate, endDate);
        GeneralResponse<RevenueReportResponse> response = revenueService.getRevenueReport(startDate, endDate, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get daily revenue breakdown for a specific month (ADMIN only)
     * GET /api/revenue/monthly?year=2024&month=12
     */
    @GetMapping("/monthly")
    public ResponseEntity<GeneralResponse<List<DailyRevenueResponse>>> getMonthlyRevenue(
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year,
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") int month,
            HttpServletRequest request) {

        log.info("Getting monthly revenue for {}/{} (admin)", year, month);
        GeneralResponse<List<DailyRevenueResponse>> response = revenueService.getMonthlyRevenue(year, month, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Manually trigger revenue calculation (ADMIN only)
     * Useful for testing or if scheduled job fails
     * POST /api/revenue/calculate
     */
    @PostMapping("/calculate")
    public ResponseEntity<GeneralResponse<String>> triggerRevenueCalculation(HttpServletRequest request) {
        log.info("Manual revenue calculation triggered (admin)");

        // Check admin role before allowing manual trigger
        checkAdminRole(request);

        int processedOrders = revenueService.calculateAndUpdateRevenue();

        String message = String.format("Revenue calculation completed. Processed %d orders", processedOrders);
        GeneralResponse<String> response = new GeneralResponse<>();
        response.setStatus(ResponseStatus.SUCCESS_STATUS);
        response.setData(message);

        return ResponseEntity.ok(response);
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

            log.info("Admin role verified for manual revenue calculation");
        } catch (ResException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to extract roles from token: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.TOKEN_INVALID);
        }
    }
}
