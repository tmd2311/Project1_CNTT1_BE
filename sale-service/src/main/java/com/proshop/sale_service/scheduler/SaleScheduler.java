package com.proshop.sale_service.scheduler;

import com.proshop.sale_service.entity.SaleEntity;
import com.proshop.sale_service.entity.SaleProductEntity;
import com.proshop.sale_service.repository.SaleProductRepository;
import com.proshop.sale_service.repository.SaleRepository;
import com.proshop.sale_service.util.enums.PromotionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler để tự động quản lý Sale
 * - Tự động activate sale khi đến start_date
 * - Tự động expire sale khi đến end_date
 * - Tự động apply/revert giá sản phẩm
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaleScheduler {

    private final SaleRepository saleRepository;
    private final SaleProductRepository saleProductRepository;
    // TODO: Inject ProductClient để update giá SKU

    /**
     * Tự động ACTIVATE các sale đã đến thời điểm bắt đầu
     * Chạy mỗi 5 phút
     */
    @Scheduled(cron = "0 */5 * * * *") // Mỗi 5 phút
    @Transactional
    public void activateScheduledSales() {
        log.info("========== Running Sale Activation Job ==========");
        LocalDateTime now = LocalDateTime.now();

        // Tìm các sale đang SCHEDULED và đã đến startDate
        List<SaleEntity> salesToActivate = saleRepository.findByStatusAndStartDateLessThanEqual(
            PromotionStatus.SCHEDULED,
            now
        );

        log.info("Found {} sales to activate", salesToActivate.size());

        for (SaleEntity sale : salesToActivate) {
            try {
                log.info("Activating sale: {} ({})", sale.getName(), sale.getCode());

                // 1. Chuyển status sang ACTIVE
                sale.setStatus(PromotionStatus.ACTIVE);
                saleRepository.save(sale);

                // 2. Apply giá sale cho các sản phẩm
                // TODO: Implement applySaleToProducts(sale)

                log.info("Sale activated successfully: {}", sale.getCode());
            } catch (Exception e) {
                log.error("Failed to activate sale: {}", sale.getCode(), e);
            }
        }

        log.info("========== Sale Activation Job Completed ==========");
    }

    /**
     * Tự động EXPIRE các sale đã hết hạn
     * Chạy mỗi 5 phút
     */
    @Scheduled(cron = "0 */5 * * * *") // Mỗi 5 phút
    @Transactional
    public void expireActiveSales() {
        log.info("========== Running Sale Expiration Job ==========");
        LocalDateTime now = LocalDateTime.now();

        // Tìm các sale đang ACTIVE và đã qua endDate
        List<SaleEntity> salesToExpire = saleRepository.findByStatusAndEndDateLessThan(
            PromotionStatus.ACTIVE,
            now
        );

        log.info("Found {} sales to expire", salesToExpire.size());

        for (SaleEntity sale : salesToExpire) {
            try {
                log.info("Expiring sale: {} ({})", sale.getName(), sale.getCode());

                // 1. Chuyển status sang EXPIRED
                sale.setStatus(PromotionStatus.EXPIRED);
                sale.setIsActive(false);
                saleRepository.save(sale);

                // 2. Revert giá sản phẩm về giá gốc
                // TODO: Implement revertSalePrices(sale)

                log.info("Sale expired successfully: {}", sale.getCode());
            } catch (Exception e) {
                log.error("Failed to expire sale: {}", sale.getCode(), e);
            }
        }

        log.info("========== Sale Expiration Job Completed ==========");
    }

    /**
     * Tự động vô hiệu hóa sale đã hết số lượng
     * Chạy mỗi giờ
     */
    @Scheduled(cron = "0 0 * * * *") // Mỗi giờ
    @Transactional
    public void deactivateFullySales() {
        log.info("========== Running Sales Full Quantity Check ==========");

        // TODO: Tìm các sale đã hết quota và vô hiệu hóa

        log.info("========== Sales Full Quantity Check Completed ==========");
    }

    /**
     * Apply sale prices to products
     * @param sale
     */
    private void applySaleToProducts(SaleEntity sale) {
        // TODO: Implement logic
        // 1. Lấy danh sách SaleProduct của sale này
        // 2. Với mỗi product/SKU:
        //    - Gọi Product Service để lấy giá gốc
        //    - Tính giá sau sale
        //    - Cập nhật SaleProduct (originalPrice, salePrice, discountAmount)
        //    - Gọi Product Service để update giá SKU
        //    - Set isApplied = true, appliedAt = now
    }

    /**
     * Revert sale prices back to original
     * @param sale
     */
    private void revertSalePrices(SaleEntity sale) {
        // TODO: Implement logic
        // 1. Lấy danh sách SaleProduct đã applied (isApplied = true)
        // 2. Với mỗi product/SKU:
        //    - Gọi Product Service để revert về originalPrice
        //    - Set isApplied = false, revertedAt = now
    }
}
