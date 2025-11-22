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

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final com.proshop.sale_service.client.ProductClient productClient;

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
                applySaleToProducts(sale);

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

                // 1. Revert giá sản phẩm về giá gốc TRƯỚC
                revertSalePrices(sale);

                // 2. Chuyển status sang EXPIRED
                sale.setStatus(PromotionStatus.EXPIRED);
                sale.setIsActive(false);
                saleRepository.save(sale);

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
        log.info("Applying sale {} to products", sale.getCode());

        // 1. Lấy danh sách SaleProduct chưa apply
        List<SaleProductEntity> saleProducts = saleProductRepository
            .findBySaleIdAndIsAppliedFalse(sale.getId());

        int successCount = 0;
        int failCount = 0;

        for (SaleProductEntity sp : saleProducts) {
            try {
                if (sp.getSkuId() != null) {
                    // Apply cho SKU cụ thể
                    applySaleToSKU(sale, sp);
                    successCount++;
                }
                // TODO: Handle product, category, brand
            } catch (Exception e) {
                failCount++;
                log.error("Failed to apply sale to SKU {}", sp.getSkuId(), e);
            }
        }

        log.info("Applied sale {} to products: {} success, {} failed",
            sale.getCode(), successCount, failCount);
    }

    /**
     * Apply sale to specific SKU
     */
    private void applySaleToSKU(SaleEntity sale, SaleProductEntity saleProduct) {
        try {
            // 1. Lấy thông tin SKU từ Product Service
            com.proshop.sale_service.client.ProductClient.SKUResponse sku =
                productClient.getSKU(saleProduct.getSkuId()).getData();

            if (sku == null) {
                log.error("SKU not found: {}", saleProduct.getSkuId());
                return;
            }

            // 2. Tính giá sale
            java.math.BigDecimal originalPrice = java.math.BigDecimal.valueOf(sku.getPrice());
            java.math.BigDecimal salePrice = calculateSalePrice(originalPrice, sale);
            java.math.BigDecimal discountAmount = originalPrice.subtract(salePrice);

            // 3. Cập nhật SaleProduct entity
            saleProduct.setOriginalPrice(originalPrice);
            saleProduct.setSalePrice(salePrice);
            saleProduct.setDiscountAmount(discountAmount);
            saleProduct.setIsApplied(true);
            saleProduct.setAppliedAt(LocalDateTime.now());
            saleProductRepository.save(saleProduct);

            // 4. Gọi Product Service để update giá SKU
            com.proshop.sale_service.client.ProductClient.UpdateSKUSalePriceRequest request =
                new com.proshop.sale_service.client.ProductClient.UpdateSKUSalePriceRequest(
                    originalPrice.doubleValue(),
                    salePrice.doubleValue(),
                    sale.getId()
                );

            productClient.updateSKUSalePrice(saleProduct.getSkuId(), request);

            log.debug("Applied sale to SKU {}: {} -> {}",
                saleProduct.getSkuId(), originalPrice, salePrice);

        } catch (Exception e) {
            log.error("Failed to apply sale to SKU {}", saleProduct.getSkuId(), e);
            throw e;
        }
    }

    /**
     * Calculate sale price based on sale type
     */
    private java.math.BigDecimal calculateSalePrice(java.math.BigDecimal originalPrice, SaleEntity sale) {
        java.math.BigDecimal discount;

        if (sale.getSaleType() == com.proshop.sale_service.util.enums.SaleType.PERCENTAGE) {
            // Giảm theo %
            discount = originalPrice
                .multiply(sale.getSaleValue())
                .divide(java.math.BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);

            // Apply max discount limit
            if (sale.getMaxDiscountAmount() != null &&
                discount.compareTo(sale.getMaxDiscountAmount()) > 0) {
                discount = sale.getMaxDiscountAmount();
            }
        } else {
            // Giảm số tiền cố định
            discount = sale.getSaleValue();
        }

        java.math.BigDecimal salePrice = originalPrice.subtract(discount);

        // Giá sau sale không được âm
        if (salePrice.compareTo(java.math.BigDecimal.ZERO) < 0) {
            salePrice = java.math.BigDecimal.ZERO;
        }

        return salePrice;
    }

    /**
     * Revert sale prices back to original
     * @param sale
     */
    private void revertSalePrices(SaleEntity sale) {
        log.info("Reverting prices for sale: {}", sale.getCode());

        // 1. Lấy danh sách SaleProduct đã applied
        List<SaleProductEntity> appliedProducts = saleProductRepository
            .findBySaleIdAndIsAppliedTrue(sale.getId());

        int successCount = 0;
        int failCount = 0;

        for (SaleProductEntity sp : appliedProducts) {
            try {
                if (sp.getSkuId() != null) {
                    // 2. Gọi Product Service để revert về giá gốc
                    productClient.revertSKUPrice(sp.getSkuId());

                    // 3. Cập nhật trạng thái SaleProduct
                    sp.setIsApplied(false);
                    sp.setRevertedAt(LocalDateTime.now());
                    saleProductRepository.save(sp);

                    successCount++;
                    log.debug("Reverted price for SKU: {}", sp.getSkuId());
                }
            } catch (Exception e) {
                failCount++;
                log.error("Failed to revert price for SKU {}", sp.getSkuId(), e);
                // Không throw exception để tiếp tục revert các SKU khác
            }
        }

        log.info("Reverted prices for sale {}: {} success, {} failed",
            sale.getCode(), successCount, failCount);
    }
}
