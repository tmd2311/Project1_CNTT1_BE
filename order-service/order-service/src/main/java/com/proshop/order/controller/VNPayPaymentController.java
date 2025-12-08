package com.proshop.order.controller;

import com.proshop.order.dto.request.VNPayPaymentRequest;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.VNPayPaymentResponse;
import com.proshop.order.service.vnpay.impl.VNPAYServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment/vnpay")
@RequiredArgsConstructor
@Slf4j
public class VNPayPaymentController {

    private final VNPAYServiceImpl vnpayService;

    /**
     * Create VNPay payment
     * POST /api/payment/vnpay/create
     */
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse<VNPayPaymentResponse>> createPayment(
            @RequestBody VNPayPaymentRequest request,
            HttpServletRequest httpRequest) {

        log.info("Creating VNPay payment for order: {}", request.getOrderId());

        GeneralResponse<VNPayPaymentResponse> response = vnpayService.createPayment(request, httpRequest);

        if (response.getStatus().getCode().equals("200")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Handle VNPay callback (IPN - Instant Payment Notification)
     * GET /api/payment/vnpay/callback
     */
    @GetMapping("/callback")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> handleCallback(
            @RequestParam Map<String, String> params) {

        log.info("Received VNPay callback with params: {}", params);

        GeneralResponse<Map<String, Object>> response = vnpayService.handleCallback(params);

        if (response.getStatus().getCode().equals("200")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Check payment status
     * GET /api/payment/vnpay/status/{orderId}
     */
    @GetMapping("/status/{orderId}")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> checkPaymentStatus(
            @PathVariable String orderId) {

        log.info("Checking payment status for order: {}", orderId);

        // This endpoint can be used to query payment status
        // You can implement additional logic here if needed

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("message", "Use callback endpoint for payment status updates");

        return ResponseEntity.ok(new GeneralResponse<>(
                new com.proshop.order.dto.response.ResponseStatus("200", "Success", "Success"),
                result,
                null
        ));
    }
}