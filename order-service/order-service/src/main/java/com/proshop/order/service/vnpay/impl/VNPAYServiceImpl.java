package com.proshop.order.service.vnpay.impl;

import com.proshop.order.config.VNPAYConfig;
import com.proshop.order.dto.request.VNPayPaymentRequest;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.ResponseStatus;
import com.proshop.order.dto.response.VNPayPaymentResponse;
import com.proshop.order.entity.OrderEntity;
import com.proshop.order.entity.PaymentEntity;
import com.proshop.order.entity.PaymentMethod;
import com.proshop.order.entity.PaymentStatus;
import com.proshop.order.repository.OrderRepository;
import com.proshop.order.repository.PaymentRepository;
import com.proshop.order.service.vnpay.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VNPAYServiceImpl implements VNPAYService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    /**
     * Create VNPay payment URL
     */
    @Transactional
    public GeneralResponse<VNPayPaymentResponse> createPayment(
            VNPayPaymentRequest request,
            HttpServletRequest httpRequest) {

        try {
            // Validate order exists
            OrderEntity order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Create payment record
            PaymentEntity payment = PaymentEntity.builder()
                    .order(order)
                    .method(PaymentMethod.VNPAY)
                    .amount(order.getTotalAmount())
                    .status(PaymentStatus.PENDING)
                    .build();

            PaymentEntity savedPayment = paymentRepository.save(payment);

            // Create VNPay payment URL
            String paymentUrl = createVNPayUrl(
                    httpRequest,
                    order.getTotalAmount().longValue(),
                    "Payment for Order: " + order.getOrderId(),
                    savedPayment.getPaymentId().toString()
            );

            VNPayPaymentResponse response = VNPayPaymentResponse.builder()
                    .paymentUrl(paymentUrl)
                    .orderId(order.getOrderId())
                    .paymentId(savedPayment.getPaymentId())
                    .amount(order.getTotalAmount())
                    .build();

            log.info("Created VNPay payment URL for order: {}", order.getOrderId());

            return new GeneralResponse<>(
                    ResponseStatus.SUCCESS_STATUS,
                    response,
                    null
            );

        } catch (Exception e) {
            log.error("Error creating VNPay payment: {}", e.getMessage(), e);
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi tạo thanh toán VNPay", "Error"),
                    null,
                    null
            );
        }
    }

    /**
     * Create VNPay payment URL
     */
    private String createVNPayUrl(HttpServletRequest request, long amount, String orderInfo, String txnRef) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_IpAddr = VNPAYConfig.getIpAddress(request);
        String vnp_TmnCode = VNPAYConfig.vnp_TmnCode;
        String orderType = "other";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPay requires amount * 100
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPAYConfig.vnp_Returnurl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // Set creation and expiration time
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Build query string
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    log.error("Error encoding URL: {}", e.getMessage());
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPAYConfig.hmacSHA512(VNPAYConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return VNPAYConfig.vnp_PayUrl + "?" + queryUrl;
    }

    /**
     * Handle VNPay callback
     */
    @Transactional
    public GeneralResponse<Map<String, Object>> handleCallback(Map<String, String> params) {
        try {
            String vnp_SecureHash = params.get("vnp_SecureHash");
            params.remove("vnp_SecureHashType");
            params.remove("vnp_SecureHash");

            // Verify signature
            String signValue = VNPAYConfig.hashAllFields(params);

            if (!signValue.equals(vnp_SecureHash)) {
                log.error("Invalid signature for VNPay callback");
                return new GeneralResponse<>(
                        new ResponseStatus("400", "Chữ ký không hợp lệ", "Invalid Signature"),
                        null,
                        null
                );
            }

            String txnRef = params.get("vnp_TxnRef");
            String responseCode = params.get("vnp_ResponseCode");
            String transactionNo = params.get("vnp_TransactionNo");
            String bankCode = params.get("vnp_BankCode");

            // Find payment by transaction reference (paymentId)
            UUID paymentId = UUID.fromString(txnRef);
            PaymentEntity payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            Map<String, Object> result = new HashMap<>();
            result.put("orderId", payment.getOrder().getOrderId());
            result.put("paymentId", payment.getPaymentId());
            result.put("transactionNo", transactionNo);
            result.put("bankCode", bankCode);

            // Check response code
            if ("00".equals(responseCode)) {
                // Payment successful
                payment.setStatus(PaymentStatus.PAID);
                payment.setPaidAt(LocalDateTime.now());
                paymentRepository.save(payment);

                result.put("status", "SUCCESS");
                result.put("message", "Thanh toán thành công");

                log.info("VNPay payment successful for order: {}", payment.getOrder().getOrderId());

                return new GeneralResponse<>(
                        ResponseStatus.SUCCESS_STATUS,
                        result,
                        null
                );
            } else {
                // Payment failed
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);

                result.put("status", "FAILED");
                result.put("message", "Thanh toán thất bại");

                log.warn("VNPay payment failed for order: {} with code: {}",
                        payment.getOrder().getOrderId(), responseCode);

                return new GeneralResponse<>(
                        new ResponseStatus("400", "Thanh toán thất bại", "Payment Failed"),
                        result,
                        null
                );
            }

        } catch (Exception e) {
            log.error("Error handling VNPay callback: {}", e.getMessage(), e);
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi xử lý callback", "Error"),
                    null,
                    null
            );
        }
    }
}