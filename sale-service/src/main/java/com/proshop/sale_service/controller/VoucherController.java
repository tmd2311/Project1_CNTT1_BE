package com.proshop.sale_service.controller;

import com.proshop.exceptionlib.dto.response.ResponseStatus;
import com.proshop.sale_service.dto.request.VoucherApplyRequest;
import com.proshop.sale_service.dto.request.VoucherCreateRequest;
import com.proshop.sale_service.dto.request.VoucherValidateRequest;
import com.proshop.sale_service.dto.response.*;
import com.proshop.sale_service.service.voucher.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller quản lý Voucher (Mã giảm giá đơn hàng)
 */
@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
@Slf4j
public class VoucherController {

    private final VoucherService voucherService;

    // ========== QUẢN LÝ VOUCHER ==========

    /**
     * Tạo voucher mới
     * POST /api/v1/vouchers
     */
    @PostMapping
    public ResponseEntity<GeneralResponse<VoucherResponse>> createVoucher(
        @Valid @RequestBody VoucherCreateRequest request
    ) {
        log.info("Creating voucher: {}", request.getCode());
        VoucherResponse response = voucherService.createVoucher(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    /**
     * Lấy danh sách tất cả vouchers
     * GET /api/v1/vouchers
     */
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<GeneralResponse<List<VoucherResponse>>> getAllVouchers() {
        log.info("Getting all vouchers");
        List<VoucherResponse> response = voucherService.getAllVouchers();
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    /**
     * Lấy voucher theo ID
     * GET /api/v1/vouchers/{id}
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<GeneralResponse<VoucherResponse>> getVoucherById(@PathVariable Long id) {
        log.info("Getting voucher by id: {}", id);
        VoucherResponse response = voucherService.getVoucherById(id);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    /**
     * Lấy voucher theo code
     * GET /api/v1/vouchers/code/{code}
     */
    @GetMapping("/code/{code}")
    @Transactional(readOnly = true)
    public ResponseEntity<GeneralResponse<VoucherResponse>> getVoucherByCode(
        @PathVariable String code
    ) {
        log.info("Getting voucher by code: {}", code);
        VoucherResponse response = voucherService.getVoucherByCode(code);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    /**
     * Lấy danh sách vouchers đang active
     * GET /api/v1/vouchers/active
     */
    @GetMapping("/active")
    @Transactional(readOnly = true)
    public ResponseEntity<GeneralResponse<List<VoucherResponse>>> getActiveVouchers() {
        log.info("Getting active vouchers");
        List<VoucherResponse> response = voucherService.getActiveVouchers();
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    // ========== SỬ DỤNG VOUCHER ==========

    /**
     * Validate voucher cho order
     * POST /api/v1/vouchers/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<GeneralResponse<VoucherValidationResponse>> validateVoucher(
        @Valid @RequestBody VoucherValidateRequest request
    ) {
        log.info("Validating voucher {} for user {}", request.getCode(), request.getUserId());
        VoucherValidationResponse response = voucherService.validateVoucher(request);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    /**
     * Áp dụng voucher vào order
     * POST /api/v1/vouchers/apply
     */
    @PostMapping("/apply")
    public ResponseEntity<GeneralResponse<VoucherApplyResponse>> applyVoucher(
        @Valid @RequestBody VoucherApplyRequest request
    ) {
        log.info("Applying voucher {} to order {}", request.getCode(), request.getOrderId());
        VoucherApplyResponse response = voucherService.applyVoucher(request);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    /**
     * Hủy voucher đã apply (khi order bị hủy)
     * POST /api/v1/vouchers/cancel/{orderId}
     */
    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<GeneralResponse<Void>> cancelVoucherUsage(@PathVariable Long orderId) {
        log.info("Cancelling voucher usage for order: {}", orderId);
        voucherService.cancelVoucherUsage(orderId);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, null, null));
    }

    // ========== VOUCHER CỦA USER ==========

    /**
     * Lấy danh sách vouchers của user
     * GET /api/v1/vouchers/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<GeneralResponse<List<VoucherResponse>>> getUserVouchers(
        @PathVariable Long userId
    ) {
        log.info("Getting vouchers for user: {}", userId);
        List<VoucherResponse> response = voucherService.getUserVouchers(userId);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    /**
     * Lấy danh sách vouchers khả dụng của user
     * GET /api/v1/vouchers/user/{userId}/available
     */
    @GetMapping("/user/{userId}/available")
    public ResponseEntity<GeneralResponse<List<VoucherResponse>>> getAvailableVouchersForUser(
        @PathVariable Long userId
    ) {
        log.info("Getting available vouchers for user: {}", userId);
        List<VoucherResponse> response = voucherService.getAvailableVouchersForUser(userId);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    // ========== GÁN VOUCHER CHO USER ==========

    /**
     * Gán voucher cho users
     * POST /api/v1/vouchers/{id}/users
     */
    @PostMapping("/{id}/users")
    public ResponseEntity<GeneralResponse<Void>> assignVoucherToUsers(
        @PathVariable Long id,
        @RequestBody List<Long> userIds
    ) {
        log.info("Assigning voucher {} to {} users", id, userIds.size());
        voucherService.assignVoucherToUsers(id, userIds);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, null, null));
    }

    /**
     * Xóa user khỏi voucher
     * DELETE /api/v1/vouchers/{id}/users/{userId}
     */
    @DeleteMapping("/{id}/users/{userId}")
    public ResponseEntity<GeneralResponse<Void>> removeUserFromVoucher(
        @PathVariable Long id,
        @PathVariable Long userId
    ) {
        log.info("Removing user {} from voucher {}", userId, id);
        voucherService.removeUserFromVoucher(id, userId);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, null, null));
    }
}
