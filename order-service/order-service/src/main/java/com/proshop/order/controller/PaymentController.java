package com.proshop.order.controller;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.order.dto.request.PaymentRequest;
import com.proshop.order.dto.request.PaymentStatusUpdateRequest;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.PaymentDeleteResponse;
import com.proshop.order.dto.response.PaymentResponse;
import com.proshop.order.entity.PaymentStatus;
import com.proshop.order.service.payment.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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

    @GetMapping("/my-payments")
    public ResponseEntity<GeneralResponse<List<PaymentResponse>>> getMyPayments(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<GeneralResponse<PaymentResponse>> getPaymentById(
        @PathVariable("paymentId") UUID paymentId,
        HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        return ResponseEntity.ok(paymentService.getPaymentByIdAndUserId(paymentId, userId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<GeneralResponse<List<PaymentResponse>>> getPaymentsByOrderId(
        @PathVariable("orderId") UUID orderId,
        HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        return ResponseEntity.ok(paymentService.getPaymentsByOrderId(orderId, userId));
    }

    @PostMapping
    public ResponseEntity<GeneralResponse<PaymentResponse>> createPayment(
        @Valid @RequestBody PaymentRequest paymentRequest,
        HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        return ResponseEntity.ok(paymentService.createPayment(paymentRequest, userId));
    }

    // ============================================
    // ADMIN ENDPOINTS
    // ============================================

    @GetMapping("/admin/all")
    public ResponseEntity<GeneralResponse<List<PaymentResponse>>> getAllPayments(HttpServletRequest request) {
        return ResponseEntity.ok(paymentService.getAllPayments(request));
    }

    @GetMapping("/admin/{paymentId}")
    public ResponseEntity<GeneralResponse<PaymentResponse>> getPaymentByIdAdmin(
        @PathVariable("paymentId") UUID paymentId,
        HttpServletRequest request) {
        return ResponseEntity.ok(paymentService.getPaymentById(request, paymentId));
    }

    @GetMapping("/admin/order/{orderId}")
    public ResponseEntity<GeneralResponse<List<PaymentResponse>>> getPaymentsByOrderIdAdmin(
        @PathVariable("orderId") UUID orderId,
        HttpServletRequest request) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrderIdAdmin(request, orderId));
    }

    @PostMapping("/admin")
    public ResponseEntity<GeneralResponse<PaymentResponse>> createPaymentAdmin(
        @Valid @RequestBody PaymentRequest paymentRequest,
        HttpServletRequest request) {
        return ResponseEntity.ok(paymentService.createPaymentAdmin(request, paymentRequest));
    }

    @PutMapping("/admin/{paymentId}/status")
    public ResponseEntity<GeneralResponse<PaymentResponse>> updatePaymentStatus(
        @PathVariable("paymentId") UUID paymentId,
        @Valid @RequestBody PaymentStatusUpdateRequest statusRequest,
        HttpServletRequest request) {
        PaymentStatus status = PaymentStatus.valueOf(statusRequest.getStatus());
        return ResponseEntity.ok(paymentService.updatePaymentStatus(request, paymentId, status));
    }

    @DeleteMapping("/admin/{paymentId}")
    public ResponseEntity<GeneralResponse<PaymentDeleteResponse>> deletePayment(
        @PathVariable("paymentId") UUID paymentId,
        HttpServletRequest request) {
        return ResponseEntity.ok(paymentService.deletePayment(request, paymentId));
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Missing or invalid Authorization header");
            throw new ResException(ResErrorCode.UNAUTHORIZED);
        }

        String token = authHeader.substring(7).trim();
        log.debug("Token extracted, length: {}", token.length());

        try {
            Long userId = jwtUtil.getUserIDFromToken(token);
            log.info("✅ Successfully extracted userId from token: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("❌ Failed to extract userId from token: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.TOKEN_INVALID);
        }
    }
}