package com.proshop.order.service.payment;

import com.proshop.order.dto.request.PaymentRequest;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.PaymentDeleteResponse;
import com.proshop.order.dto.response.PaymentResponse;
import com.proshop.order.entity.PaymentStatus;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    GeneralResponse<List<PaymentResponse>> getPaymentsByUserId(Long userId);
    GeneralResponse<PaymentResponse> getPaymentByIdAndUserId(UUID paymentId, Long userId);
    GeneralResponse<List<PaymentResponse>> getPaymentsByOrderId(UUID orderId, Long userId);
    GeneralResponse<PaymentResponse> createPayment(PaymentRequest request, Long userId);
    GeneralResponse<List<PaymentResponse>> getAllPayments(HttpServletRequest httpRequest);
    GeneralResponse<PaymentResponse> getPaymentById(HttpServletRequest httpRequest, UUID paymentId);
    GeneralResponse<List<PaymentResponse>> getPaymentsByOrderIdAdmin(HttpServletRequest httpRequest, UUID orderId);
    GeneralResponse<PaymentResponse> createPaymentAdmin(HttpServletRequest httpRequest, PaymentRequest request);
    GeneralResponse<PaymentResponse> updatePaymentStatus(HttpServletRequest httpRequest, UUID paymentId, PaymentStatus status);
    GeneralResponse<PaymentDeleteResponse> deletePayment(HttpServletRequest httpRequest, UUID paymentId);

}
