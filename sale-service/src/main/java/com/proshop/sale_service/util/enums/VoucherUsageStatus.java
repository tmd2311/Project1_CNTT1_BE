package com.proshop.sale_service.util.enums;

/**
 * Trạng thái sử dụng voucher
 */
public enum VoucherUsageStatus {
    USED,      // Đã sử dụng
    CANCELLED, // Đã hủy (order bị hủy)
    REFUNDED   // Đã hoàn (order bị refund)
}
