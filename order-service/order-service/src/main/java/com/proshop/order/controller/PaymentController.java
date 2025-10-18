package com.proshop.order.controller;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.order.dto.request.PaymentRequest;
import com.proshop.order.dto.request.PaymentStatusUpdateRequest;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.PaymentDeleteResponse;
import com.proshop.order.dto.response.PaymentResponse;
import com.proshop.order.entity.PaymentStatus;
import com.proshop.order.service.payment.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final JwtUtil jwtUtil;

    // ============================================
    // USER ENDPOINTS
    // ============================================

    /**
     * Get all payments for current user
     * GET /api/v1/payments/my-payments
     */
    @GetMapping("/my-payments")
    public ResponseEntity<GeneralResponse<List<PaymentResponse>>> getMyPayments(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
    }

    /**
     * Get payment by ID for current user
     * GET /api/v1/payments/{paymentId}
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<GeneralResponse<PaymentResponse>> getPaymentById(
            @PathVariable UUID paymentId,
            HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        return ResponseEntity.ok(paymentService.getPaymentByIdAndUserId(paymentId, userId));
    }

    /**
     * Get payments by order ID for current user
     * GET /api/v1/payments/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<GeneralResponse<List<PaymentResponse>>> getPaymentsByOrderId(
            @PathVariable UUID orderId,
            HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        return ResponseEntity.ok(paymentService.getPaymentsByOrderId(orderId, userId));
    }

    /**
     * Create payment for current user
     * POST /api/v1/payments
     */
    @PostMapping
    public ResponseEntity<GeneralResponse<PaymentResponse>> createPayment(
            @RequestBody PaymentRequest paymentRequest,
            HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        return ResponseEntity.ok(paymentService.createPayment(paymentRequest, userId));
    }

    // ============================================
    // ADMIN ENDPOINTS
    // ============================================

    /**
     * Get all payments (Admin only)
     * GET /api/v1/payments/admin/all
     */
    @GetMapping("/admin/all")
    public ResponseEntity<GeneralResponse<List<PaymentResponse>>> getAllPayments(HttpServletRequest request) {
        return ResponseEntity.ok(paymentService.getAllPayments(request));
    }

    /**
     * Get payment by ID (Admin only)
     * GET /api/v1/payments/admin/{paymentId}
     */
    @GetMapping("/admin/{paymentId}")
    public ResponseEntity<GeneralResponse<PaymentResponse>> getPaymentByIdAdmin(
            @PathVariable UUID paymentId,
            HttpServletRequest request) {
        return ResponseEntity.ok(paymentService.getPaymentById(request, paymentId));
    }

    /**
     * Get payments by order ID (Admin only)
     * GET /api/v1/payments/admin/order/{orderId}
     */
    @GetMapping("/admin/order/{orderId}")
    public ResponseEntity<GeneralResponse<List<PaymentResponse>>> getPaymentsByOrderIdAdmin(
            @PathVariable UUID orderId,
            HttpServletRequest request) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrderIdAdmin(request, orderId));
    }

    /**
     * Create payment (Admin only)
     * POST /api/v1/payments/admin
     */
    @PostMapping("/admin")
    public ResponseEntity<GeneralResponse<PaymentResponse>> createPaymentAdmin(
            @RequestBody PaymentRequest paymentRequest,
            HttpServletRequest request) {
        return ResponseEntity.ok(paymentService.createPaymentAdmin(request, paymentRequest));
    }

    /**
     * Update payment status (Admin only)
     * PUT /api/v1/payments/admin/{paymentId}/status
     */
    @PutMapping("/admin/status/{paymentId}")
    public ResponseEntity<GeneralResponse<PaymentResponse>> updatePaymentStatus(
            @PathVariable UUID paymentId,
            @RequestBody PaymentStatusUpdateRequest statusRequest,
            HttpServletRequest request) {
        PaymentStatus status = PaymentStatus.valueOf(statusRequest.getStatus());
        return ResponseEntity.ok(paymentService.updatePaymentStatus(request, paymentId, status));
    }

    /**
     * Delete payment (Admin only)
     * DELETE /api/v1/payments/admin/{paymentId}
     */
    @DeleteMapping("/admin/{paymentId}")
    public ResponseEntity<GeneralResponse<PaymentDeleteResponse>> deletePayment(
            @PathVariable UUID paymentId,
            HttpServletRequest request) {
        return ResponseEntity.ok(paymentService.deletePayment(request, paymentId));
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    /**
     * Extract userId from JWT token
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Missing or invalid Authorization header");
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7).trim();
        log.debug("Token extracted, length: {}", token.length());

        try {
            Long userId = jwtUtil.getUserIDFromToken(token);
            log.info("✅ Successfully extracted userId from token: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("❌ Failed to extract userId from token: {}", e.getMessage(), e);
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }
}