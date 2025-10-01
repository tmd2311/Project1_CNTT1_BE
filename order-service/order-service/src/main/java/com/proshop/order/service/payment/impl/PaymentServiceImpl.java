package com.proshop.order.service.payment.impl;

import com.proshop.order.dto.request.PaymentRequest;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.PaymentDeleteResponse;
import com.proshop.order.dto.response.PaymentResponse;
import com.proshop.order.dto.response.ResponseStatus;
import com.proshop.order.entity.OrderEntity;
import com.proshop.order.entity.PaymentEntity;
import com.proshop.order.repository.OrderRepository;
import com.proshop.order.repository.PaymentRepository;
import com.proshop.order.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    public GeneralResponse<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> data = paymentRepository.findAll().stream()
                .map(this::toPaymentResponse)
                .collect(Collectors.toList());

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    public GeneralResponse<PaymentResponse> getPaymentById(UUID id) {
        return paymentRepository.findById(id)
                .map(payment -> new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, toPaymentResponse(payment), null))
                .orElseGet(() -> new GeneralResponse<>(new ResponseStatus("404", "Không tìm thấy thanh toán", "Payment Not Found"), null, null));
    }

    @Override
    @Transactional
    public GeneralResponse<PaymentResponse> createPayment(PaymentRequest request) {
        OrderEntity order = orderRepository.findById(request.getOrderId()).orElse(null);
        if (order == null) {
            return new GeneralResponse<>(new ResponseStatus("404", "Không tìm thấy đơn hàng", "Order Not Found"), null, null);
        }

        PaymentEntity payment = PaymentEntity.builder()
                .order(order)
                .method(request.getMethod())
                .amount(request.getAmount())
                .status(com.proshop.order.entity.PaymentStatus.PENDING)
                .paidAt(null)
                .build();

        paymentRepository.save(payment);

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, toPaymentResponse(payment), null);
    }

    @Override
    @Transactional
    public GeneralResponse<PaymentResponse> updatePaymentStatus(UUID id, com.proshop.order.entity.PaymentStatus status) {
        return paymentRepository.findById(id)
                .map(payment -> {
                    payment.setStatus(status);
                    if (status == com.proshop.order.entity.PaymentStatus.PAID) {
                        payment.setPaidAt(LocalDateTime.now());
                    }
                    paymentRepository.save(payment);
                    return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, toPaymentResponse(payment), null);
                })
                .orElseGet(() -> new GeneralResponse<>(new ResponseStatus("404", "Không tìm thấy thanh toán", "Payment Not Found"), null, null));
    }

    @Override
    @Transactional
    public GeneralResponse<PaymentDeleteResponse> deletePayment(UUID id) {
        return paymentRepository.findById(id)
                .map(payment -> {
                    paymentRepository.delete(payment);
                    return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS,
                            new PaymentDeleteResponse(payment.getPaymentId(), payment.getOrder().getOrderId()), null);
                })
                .orElseGet(() -> new GeneralResponse<>(new ResponseStatus("404", "Không tìm thấy thanh toán", "Payment Not Found"), null, null));
    }

    private PaymentResponse toPaymentResponse(PaymentEntity payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getPaymentId());
        response.setOrderId(payment.getOrder().getOrderId());
        response.setMethod(payment.getMethod().name());
        response.setStatus(payment.getStatus().name());
        response.setAmount(payment.getAmount());
        response.setPaidAt(payment.getPaidAt());
        return response;
    }
}
