package com.proshop.sale_service.scheduler;

import com.proshop.sale_service.entity.VoucherEntity;
import com.proshop.sale_service.repository.VoucherRepository;
import com.proshop.sale_service.util.enums.PromotionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler để tự động quản lý Voucher status
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VoucherScheduler {

    private final VoucherRepository voucherRepository;

    /**
     * Tự động ACTIVATE các voucher đã đến thời điểm bắt đầu
     * Chạy mỗi 10 phút
     */
    @Scheduled(cron = "0 */10 * * * *") // Mỗi 10 phút
    @Transactional
    public void activateScheduledVouchers() {
        log.info("========== Running Voucher Activation Job ==========");
        LocalDateTime now = LocalDateTime.now();

        List<VoucherEntity> vouchersToActivate = voucherRepository.findByStatusAndStartDateLessThanEqual(
            PromotionStatus.SCHEDULED,
            now
        );

        log.info("Found {} vouchers to activate", vouchersToActivate.size());

        for (VoucherEntity voucher : vouchersToActivate) {
            try {
                log.info("Activating voucher: {} ({})", voucher.getName(), voucher.getCode());
                voucher.setStatus(PromotionStatus.ACTIVE);
                voucherRepository.save(voucher);
                log.info("Voucher activated successfully: {}", voucher.getCode());
            } catch (Exception e) {
                log.error("Failed to activate voucher: {}", voucher.getCode(), e);
            }
        }

        log.info("========== Voucher Activation Job Completed ==========");
    }

    /**
     * Tự động EXPIRE các voucher đã hết hạn
     * Chạy mỗi 10 phút
     */
    @Scheduled(cron = "0 */10 * * * *") // Mỗi 10 phút
    @Transactional
    public void expireActiveVouchers() {
        log.info("========== Running Voucher Expiration Job ==========");
        LocalDateTime now = LocalDateTime.now();

        List<VoucherEntity> vouchersToExpire = voucherRepository.findByStatusAndEndDateLessThan(
            PromotionStatus.ACTIVE,
            now
        );

        log.info("Found {} vouchers to expire", vouchersToExpire.size());

        for (VoucherEntity voucher : vouchersToExpire) {
            try {
                log.info("Expiring voucher: {} ({})", voucher.getName(), voucher.getCode());
                voucher.setStatus(PromotionStatus.EXPIRED);
                voucher.setIsActive(false);
                voucherRepository.save(voucher);
                log.info("Voucher expired successfully: {}", voucher.getCode());
            } catch (Exception e) {
                log.error("Failed to expire voucher: {}", voucher.getCode(), e);
            }
        }

        log.info("========== Voucher Expiration Job Completed ==========");
    }

    /**
     * Tự động vô hiệu hóa voucher đã hết số lượng
     * Chạy mỗi giờ
     */
    @Scheduled(cron = "0 0 * * * *") // Mỗi giờ
    @Transactional
    public void deactivateFullyUsedVouchers() {
        log.info("========== Running Voucher Full Quantity Check ==========");

        List<VoucherEntity> vouchersToDeactivate = voucherRepository.findFullyUsedVouchers();

        log.info("Found {} vouchers to deactivate (fully used)", vouchersToDeactivate.size());

        for (VoucherEntity voucher : vouchersToDeactivate) {
            try {
                log.info("Deactivating fully used voucher: {}", voucher.getCode());
                voucher.setStatus(PromotionStatus.EXPIRED);
                voucher.setIsActive(false);
                voucherRepository.save(voucher);
                log.info("Voucher deactivated successfully: {}", voucher.getCode());
            } catch (Exception e) {
                log.error("Failed to deactivate voucher: {}", voucher.getCode(), e);
            }
        }

        log.info("========== Voucher Full Quantity Check Completed ==========");
    }

    /**
     * Cleanup: Xóa mềm các voucher đã hết hạn quá lâu (> 90 ngày)
     * Chạy mỗi ngày lúc 2h sáng
     */
    @Scheduled(cron = "0 0 2 * * *") // 2h sáng mỗi ngày
    @Transactional
    public void cleanupExpiredVouchers() {
        log.info("========== Running Voucher Cleanup Job ==========");
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);

        List<VoucherEntity> vouchersToCleanup = voucherRepository.findExpiredBefore(cutoffDate);

        log.info("Found {} vouchers to cleanup (expired > 90 days)", vouchersToCleanup.size());

        for (VoucherEntity voucher : vouchersToCleanup) {
            try {
                log.info("Soft deleting old voucher: {}", voucher.getCode());
                voucher.setIsDelete(true);
                voucherRepository.save(voucher);
            } catch (Exception e) {
                log.error("Failed to cleanup voucher: {}", voucher.getCode(), e);
            }
        }

        log.info("========== Voucher Cleanup Job Completed ==========");
    }
}
