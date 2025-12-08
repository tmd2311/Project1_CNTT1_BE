package com.proshop.sale_service.service.voucher.impl;

import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.sale_service.dto.request.VoucherApplyRequest;
import com.proshop.sale_service.dto.request.VoucherCreateRequest;
import com.proshop.sale_service.dto.request.VoucherValidateRequest;
import com.proshop.sale_service.dto.response.VoucherApplyResponse;
import com.proshop.sale_service.dto.response.VoucherResponse;
import com.proshop.sale_service.dto.response.VoucherValidationResponse;
import com.proshop.sale_service.entity.VoucherEntity;
import com.proshop.sale_service.entity.VoucherUsageEntity;
import com.proshop.sale_service.entity.VoucherUserEntity;
import com.proshop.sale_service.repository.VoucherRepository;
import com.proshop.sale_service.repository.VoucherUsageRepository;
import com.proshop.sale_service.repository.VoucherUserRepository;
import com.proshop.sale_service.service.voucher.VoucherService;
import com.proshop.sale_service.util.enums.DiscountType;
import com.proshop.sale_service.util.enums.PromotionStatus;
import com.proshop.sale_service.util.enums.VoucherUsageStatus;
import com.proshop.sale_service.util.enums.VoucherUserScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherUsageRepository voucherUsageRepository;
    private final VoucherUserRepository voucherUserRepository;

    @Override
    @Transactional
    public VoucherResponse createVoucher(VoucherCreateRequest request) {
        log.info("Creating voucher with code: {}", request.getCode());

        // Check code đã tồn tại chưa
        if (voucherRepository.existsByCodeAndIsDeleteFalse(request.getCode())) {
            throw new ResException(ResErrorCode.VOUCHER_CODE_ALREADY_EXISTS);
        }

        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ResException(ResErrorCode.VOUCHER_INVALID_DATE);
        }

        VoucherEntity voucher = mapToEntity(request);
        VoucherEntity savedVoucher = voucherRepository.save(voucher);

        log.info("Voucher created successfully: {}", savedVoucher.getId());
        return mapToResponse(savedVoucher);
    }

    @Override
    public VoucherResponse getVoucherById(Long id) {
        VoucherEntity voucher = voucherRepository.findById(id)
            .orElseThrow(() -> new ResException(ResErrorCode.VOUCHER_NOT_FOUND));
        return mapToResponse(voucher);
    }

    @Override
    public VoucherResponse getVoucherByCode(String code) {
        VoucherEntity voucher = voucherRepository.findByCodeAndIsDeleteFalse(code)
            .orElseThrow(() -> new ResException(ResErrorCode.VOUCHER_NOT_FOUND));
        return mapToResponse(voucher);
    }

    @Override
    public List<VoucherResponse> getAllVouchers() {
        return voucherRepository.findAllByIsDeleteFalse().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<VoucherResponse> getActiveVouchers() {
        return voucherRepository.findActiveVouchers(LocalDateTime.now()).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherValidationResponse validateVoucher(VoucherValidateRequest request) {
        log.info("Validating voucher {} for user {}", request.getCode(), request.getUserId());

        // 1. Tìm voucher
        VoucherEntity voucher = voucherRepository.findByCodeAndIsDeleteFalse(request.getCode())
            .orElseThrow(() -> new ResException(ResErrorCode.VOUCHER_NOT_FOUND));

        // 2. Check voucher active
        if (!voucher.getIsActive() || voucher.getStatus() != PromotionStatus.ACTIVE) {
            return VoucherValidationResponse.invalid("Voucher không hoạt động");
        }

        // 3. Check thời gian
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getStartDate())) {
            return VoucherValidationResponse.invalid("Voucher chưa đến thời gian sử dụng");
        }
        if (now.isAfter(voucher.getEndDate())) {
            return VoucherValidationResponse.invalid("Voucher đã hết hạn");
        }

        // 4. Check số lượng
        if (voucher.getUsedCount() >= voucher.getTotalQuantity()) {
            return VoucherValidationResponse.invalid("Voucher đã hết số lượng");
        }

        // 5. Check user eligibility
        if (!isUserEligible(voucher, request.getUserId())) {
            return VoucherValidationResponse.invalid("Bạn không được phép sử dụng voucher này");
        }

        // 6. Check user usage limit
        int userUsageCount = voucherUsageRepository.countByVoucherIdAndUserIdAndStatus(
            voucher.getId(),
            request.getUserId(),
            VoucherUsageStatus.USED
        );
        if (userUsageCount >= voucher.getUsageLimitPerUser()) {
            return VoucherValidationResponse.invalid(
                "Bạn đã sử dụng hết số lần cho phép (" + voucher.getUsageLimitPerUser() + ")"
            );
        }

        // 7. Check min order value
        if (request.getOrderValue().compareTo(voucher.getMinOrderValue()) < 0) {
            return VoucherValidationResponse.invalid(
                "Đơn hàng chưa đủ giá trị tối thiểu: " + voucher.getMinOrderValue()
            );
        }

        // 8. Calculate discount
        BigDecimal discountAmount = calculateDiscount(voucher, request.getOrderValue());
        BigDecimal finalOrderValue = request.getOrderValue().subtract(discountAmount);

        log.info("Voucher {} is valid. Discount: {}", request.getCode(), discountAmount);

        return VoucherValidationResponse.valid(
            mapToResponse(voucher),
            discountAmount,
            finalOrderValue
        );
    }

    @Override
    @Transactional
    public VoucherApplyResponse applyVoucher(VoucherApplyRequest request) {
        log.info("Applying voucher {} to order {}", request.getCode(), request.getOrderId());

        // 1. Validate voucher
        VoucherValidateRequest validateRequest = new VoucherValidateRequest();
        validateRequest.setCode(request.getCode());
        validateRequest.setUserId(request.getUserId());
        validateRequest.setOrderValue(request.getOrderValue());

        VoucherValidationResponse validation = validateVoucher(validateRequest);

        if (!validation.getIsValid()) {
            throw new ResException(ResErrorCode.VOUCHER_INVALID_OR_EXPIRED);
        }

        // 2. Check order chưa dùng voucher nào
        if (voucherUsageRepository.existsByOrderId(request.getOrderId())) {
            throw new ResException(ResErrorCode.VOUCHER_ALREADY_APPLIED);
        }

        // 3. Lấy voucher
        VoucherEntity voucher = voucherRepository.findByCodeAndIsDeleteFalse(request.getCode())
            .orElseThrow(() -> new ResException(ResErrorCode.VOUCHER_NOT_FOUND));

        // 4. Calculate discount
        BigDecimal discountAmount = validation.getDiscountAmount();
        BigDecimal finalOrderValue = request.getOrderValue().subtract(discountAmount);

        // 5. Tạo voucher usage record
        VoucherUsageEntity usage = new VoucherUsageEntity();
        usage.setVoucherId(voucher.getId());
        usage.setUserId(request.getUserId());
        usage.setOrderId(request.getOrderId());
        usage.setOrderValue(request.getOrderValue());
        usage.setDiscountAmount(discountAmount);
        usage.setStatus(VoucherUsageStatus.USED);

        VoucherUsageEntity savedUsage = voucherUsageRepository.save(usage);

        // 6. Tăng used_count của voucher
        voucher.setUsedCount(voucher.getUsedCount() + 1);

        // 7. Tự động vô hiệu hóa nếu hết số lượng
        if (voucher.getUsedCount() >= voucher.getTotalQuantity()) {
            voucher.setIsActive(false);
            voucher.setStatus(PromotionStatus.EXPIRED);
            log.info("Voucher {} has been fully used", voucher.getCode());
        }

        voucherRepository.save(voucher);

        log.info("Voucher applied successfully: {}", savedUsage.getId());

        return VoucherApplyResponse.builder()
            .voucherUsageId(savedUsage.getId())
            .voucherId(voucher.getId())
            .userId(request.getUserId())
            .orderId(request.getOrderId())
            .orderValue(request.getOrderValue())
            .discountAmount(discountAmount)
            .finalOrderValue(finalOrderValue)
            .appliedAt(savedUsage.getUsedAt())
            .build();
    }

    @Override
    @Transactional
    public void cancelVoucherUsage(Long orderId) {
        log.info("Cancelling voucher usage for order: {}", orderId);

        List<VoucherUsageEntity> usages = voucherUsageRepository.findByOrderId(orderId);

        for (VoucherUsageEntity usage : usages) {
            if (usage.getStatus() == VoucherUsageStatus.USED) {
                // Change status to CANCELLED
                usage.setStatus(VoucherUsageStatus.CANCELLED);
                voucherUsageRepository.save(usage);

                // Giảm used_count của voucher
                VoucherEntity voucher = voucherRepository.findById(usage.getVoucherId())
                    .orElseThrow(() -> new ResException(ResErrorCode.VOUCHER_NOT_FOUND));

                voucher.setUsedCount(Math.max(0, voucher.getUsedCount() - 1));
                voucherRepository.save(voucher);

                log.info("Voucher usage cancelled: {}", usage.getId());
            }
        }
    }

    @Override
    public List<VoucherResponse> getUserVouchers(Long userId) {
        // TODO: Implement based on VoucherUserScope logic
        return List.of();
    }

    @Override
    public List<VoucherResponse> getAvailableVouchersForUser(Long userId) {
        // TODO: Implement - get vouchers that user can use
        return List.of();
    }

    @Override
    @Transactional
    public void assignVoucherToUsers(Long voucherId, List<Long> userIds) {
        VoucherEntity voucher = voucherRepository.findById(voucherId)
            .orElseThrow(() -> new ResException(ResErrorCode.VOUCHER_NOT_FOUND));

        if (voucher.getUserScope() != VoucherUserScope.SPECIFIC_USERS) {
            throw new ResException(ResErrorCode.VOUCHER_INVALID_OPERATION);
        }

        for (Long userId : userIds) {
            // Check nếu chưa tồn tại
            if (!voucherUserRepository.existsByVoucherIdAndUserIdAndIsActiveTrue(voucherId, userId)) {
                VoucherUserEntity voucherUser = new VoucherUserEntity();
                voucherUser.setVoucherId(voucherId);
                voucherUser.setUserId(userId);
                voucherUserRepository.save(voucherUser);
            }
        }

        log.info("Assigned voucher {} to {} users", voucherId, userIds.size());
    }

    @Override
    @Transactional
    public void removeUserFromVoucher(Long voucherId, Long userId) {
        voucherUserRepository.deleteByVoucherIdAndUserId(voucherId, userId);
        log.info("Removed user {} from voucher {}", userId, voucherId);
    }

    // ========== HELPER METHODS ==========

    /**
     * Check xem user có eligible để dùng voucher không
     */
    private boolean isUserEligible(VoucherEntity voucher, Long userId) {
        return switch (voucher.getUserScope()) {
            case ALL_USERS -> true;
            case SPECIFIC_USERS ->
                voucherUserRepository.existsByVoucherIdAndUserIdAndIsActiveTrue(voucher.getId(), userId);
            case NEW_USERS -> {
                // TODO: Check if user is new (registered within X days)
                yield true; // Placeholder
            }
            case VIP_USERS -> {
                // TODO: Check if user is VIP
                yield true; // Placeholder
            }
        };
    }

    /**
     * Tính số tiền giảm giá
     */
    private BigDecimal calculateDiscount(VoucherEntity voucher, BigDecimal orderValue) {
        BigDecimal discount;

        if (voucher.getDiscountType() == DiscountType.PERCENTAGE) {
            // Giảm theo %
            discount = orderValue
                .multiply(voucher.getDiscountValue())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // Apply max discount limit
            if (voucher.getMaxDiscountAmount() != null &&
                discount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
                discount = voucher.getMaxDiscountAmount();
            }
        } else {
            // Giảm số tiền cố định
            discount = voucher.getDiscountValue();
        }

        // Discount không được vượt quá order value
        if (discount.compareTo(orderValue) > 0) {
            discount = orderValue;
        }

        return discount;
    }

    /**
     * Map Entity to Response
     */
    private VoucherResponse mapToResponse(VoucherEntity entity) {
        VoucherResponse response = new VoucherResponse();
        response.setId(entity.getId());
        response.setCode(entity.getCode());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setVoucherType(entity.getVoucherType());
        response.setDiscountType(entity.getDiscountType());
        response.setDiscountValue(entity.getDiscountValue());
        response.setMinOrderValue(entity.getMinOrderValue());
        response.setMaxDiscountAmount(entity.getMaxDiscountAmount());
        response.setTotalQuantity(entity.getTotalQuantity());
        response.setUsedCount(entity.getUsedCount());
        response.setUsageLimitPerUser(entity.getUsageLimitPerUser());
        response.setUserScope(entity.getUserScope());
        response.setStartDate(entity.getStartDate());
        response.setEndDate(entity.getEndDate());
        response.setStatus(entity.getStatus());
        response.setIsActive(entity.getIsActive());
        response.setBannerImageUrl(entity.getBannerImageUrl());
        response.setThumbnailImageUrl(entity.getThumbnailImageUrl());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }

    /**
     * Map Request to Entity
     */
    private VoucherEntity mapToEntity(VoucherCreateRequest request) {
        VoucherEntity entity = new VoucherEntity();
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setVoucherType(request.getVoucherType());
        entity.setDiscountType(request.getDiscountType());
        entity.setDiscountValue(request.getDiscountValue());
        entity.setMinOrderValue(request.getMinOrderValue() != null ? request.getMinOrderValue() : BigDecimal.ZERO);
        entity.setMaxDiscountAmount(request.getMaxDiscountAmount());
        entity.setTotalQuantity(request.getTotalQuantity());
        entity.setUsageLimitPerUser(request.getUsageLimitPerUser());
        entity.setUserScope(request.getUserScope());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setBannerImageUrl(request.getBannerImageUrl());
        entity.setThumbnailImageUrl(request.getThumbnailImageUrl());
        return entity;
    }
}
