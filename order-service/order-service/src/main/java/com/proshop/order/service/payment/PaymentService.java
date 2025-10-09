package com.proshop.order.service.payment;

import com.proshop.order.dto.request.PaymentRequest;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.PaymentDeleteResponse;
import com.proshop.order.dto.response.PaymentResponse;
import com.proshop.order.entity.PaymentStatus;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    GeneralResponse<List<PaymentResponse>> getAllPayments();

    GeneralResponse<PaymentResponse> getPaymentById(UUID id);

    GeneralResponse<PaymentResponse> createPayment(PaymentRequest request);

    GeneralResponse<PaymentResponse> updatePaymentStatus(UUID id, PaymentStatus status);

    GeneralResponse<PaymentDeleteResponse> deletePayment(UUID id);
}
