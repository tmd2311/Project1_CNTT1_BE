package com.proshop.order.service.vnpay;

import com.proshop.order.dto.request.VNPayPaymentRequest;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.VNPayPaymentResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface VNPAYService {
    public GeneralResponse<VNPayPaymentResponse> createPayment(VNPayPaymentRequest request, HttpServletRequest httpRequest);
    public GeneralResponse<Map<String, Object>> handleCallback(Map<String, String> params);

}
