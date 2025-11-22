package com.proshop.sale_service.service.voucher;

import com.proshop.sale_service.dto.request.VoucherApplyRequest;
import com.proshop.sale_service.dto.request.VoucherCreateRequest;
import com.proshop.sale_service.dto.request.VoucherValidateRequest;
import com.proshop.sale_service.dto.response.VoucherApplyResponse;
import com.proshop.sale_service.dto.response.VoucherResponse;
import com.proshop.sale_service.dto.response.VoucherValidationResponse;

import java.util.List;

public interface VoucherService {

    /**
     * Tạo voucher mới
     */
    VoucherResponse createVoucher(VoucherCreateRequest request);

    /**
     * Lấy voucher theo ID
     */
    VoucherResponse getVoucherById(Long id);

    /**
     * Lấy voucher theo code
     */
    VoucherResponse getVoucherByCode(String code);

    /**
     * Lấy danh sách tất cả vouchers
     */
    List<VoucherResponse> getAllVouchers();

    /**
     * Lấy danh sách vouchers đang active
     */
    List<VoucherResponse> getActiveVouchers();

    /**
     * Validate voucher cho order
     */
    VoucherValidationResponse validateVoucher(VoucherValidateRequest request);

    /**
     * Áp dụng voucher vào order
     */
    VoucherApplyResponse applyVoucher(VoucherApplyRequest request);

    /**
     * Hủy voucher đã apply (khi order bị hủy)
     */
    void cancelVoucherUsage(Long orderId);

    /**
     * Lấy danh sách vouchers của user
     */
    List<VoucherResponse> getUserVouchers(Long userId);

    /**
     * Lấy danh sách vouchers khả dụng của user
     */
    List<VoucherResponse> getAvailableVouchersForUser(Long userId);

    /**
     * Gán voucher cho user
     */
    void assignVoucherToUsers(Long voucherId, List<Long> userIds);

    /**
     * Xóa user khỏi voucher
     */
    void removeUserFromVoucher(Long voucherId, Long userId);
}
