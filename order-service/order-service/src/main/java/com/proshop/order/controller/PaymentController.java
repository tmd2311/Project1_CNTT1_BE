package com.proshop.order.controller;

import com.proshop.order.dto.request.PaymentRequest;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.PaymentDeleteResponse;
import com.proshop.order.dto.response.PaymentResponse;
import com.proshop.order.entity.PaymentStatus;
import com.proshop.order.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public GeneralResponse<List<PaymentResponse>> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public GeneralResponse<PaymentResponse> getPaymentById(@PathVariable UUID id) {
        return paymentService.getPaymentById(id);
    }

    @PostMapping
    public GeneralResponse<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @PutMapping("/{id}/status")
    public GeneralResponse<PaymentResponse> updatePaymentStatus(
            @PathVariable UUID id,
            @RequestParam PaymentStatus status) {
        return paymentService.updatePaymentStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public GeneralResponse<PaymentDeleteResponse> deletePayment(@PathVariable UUID id) {
        return paymentService.deletePayment(id);
    }
}
