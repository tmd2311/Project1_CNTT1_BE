package com.proshop.sale_service.util.enums;

/**
 * Trạng thái của Sale/Voucher
 */
public enum PromotionStatus {
    SCHEDULED,  // Đã lên lịch, chưa bắt đầu
    ACTIVE,     // Đang hoạt động
    EXPIRED,    // Hết hạn
    PAUSED      // Tạm dừng
}
