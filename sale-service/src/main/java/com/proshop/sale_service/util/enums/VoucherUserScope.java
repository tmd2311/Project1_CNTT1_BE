package com.proshop.sale_service.util.enums;

/**
 * Phạm vi user được sử dụng voucher
 */
public enum VoucherUserScope {
    ALL_USERS,      // Tất cả user
    SPECIFIC_USERS, // User cụ thể (phải được assign)
    NEW_USERS,      // User mới (đăng ký trong X ngày)
    VIP_USERS       // User VIP (có điểm tích lũy hoặc rank cao)
}
