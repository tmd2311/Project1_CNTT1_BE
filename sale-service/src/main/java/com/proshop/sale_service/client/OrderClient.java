package com.proshop.sale_service.client;

import com.proshop.sale_service.dto.response.GeneralResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Feign Client để gọi Order Service
 */
@FeignClient(name = "order-service", url = "${order.service.url:http://localhost:8083}")
public interface OrderClient {

    /**
     * Cập nhật total amount của order sau khi apply voucher
     */
    @PutMapping("/api/order/{orderId}/apply-voucher")
    GeneralResponse<OrderResponse> applyVoucherToOrder(
        @PathVariable("orderId") UUID orderId,
        @RequestBody ApplyVoucherRequest request
    );

    /**
     * DTO cho Order Response
     */
    class OrderResponse {
        private UUID orderId;
        private Long userId;
        private BigDecimal totalAmount;
        private String status;
        private String shippingAddress;
        
        // Getters and setters
        public UUID getOrderId() { return orderId; }
        public void setOrderId(UUID orderId) { this.orderId = orderId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    }

    /**
     * DTO cho Apply Voucher Request
     */
    class ApplyVoucherRequest {
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private String voucherCode;
        
        public ApplyVoucherRequest() {}
        
        public ApplyVoucherRequest(BigDecimal discountAmount, BigDecimal finalAmount, String voucherCode) {
            this.discountAmount = discountAmount;
            this.finalAmount = finalAmount;
            this.voucherCode = voucherCode;
        }
        
        // Getters and setters
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
        public BigDecimal getFinalAmount() { return finalAmount; }
        public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
        public String getVoucherCode() { return voucherCode; }
        public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }
    }
}

