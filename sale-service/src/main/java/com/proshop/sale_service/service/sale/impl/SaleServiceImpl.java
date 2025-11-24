package com.proshop.sale_service.service.sale.impl;

import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.sale_service.client.ProductClient;
import com.proshop.sale_service.dto.request.AddProductsToSaleRequest;
import com.proshop.sale_service.dto.request.SaleCreateRequest;
import com.proshop.sale_service.dto.response.SaleProductResponse;
import com.proshop.sale_service.dto.response.SaleResponse;
import com.proshop.sale_service.entity.SaleEntity;
import com.proshop.sale_service.entity.SaleProductEntity;
import com.proshop.sale_service.repository.SaleProductRepository;
import com.proshop.sale_service.repository.SaleRepository;
import com.proshop.sale_service.service.sale.SaleService;
import com.proshop.sale_service.util.FileUtil;
import com.proshop.sale_service.util.enums.SaleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final SaleProductRepository saleProductRepository;
    private final ProductClient productClient;
    private final FileUtil fileUtil;

    @Override
    @Transactional
    public SaleResponse createSale(SaleCreateRequest request, MultipartFile bannerImage, MultipartFile thumbnailImage) {
        log.info("Creating sale with code: {}", request.getCode());

        // Check code đã tồn tại chưa
        if (saleRepository.existsByCodeAndIsDeleteFalse(request.getCode())) {
            throw new ResException(ResErrorCode.SALE_CODE_ALREADY_EXISTS);
        }

        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ResException(ResErrorCode.SALE_INVALID_DATE);
        }

        // Upload images if provided
        String bannerImageUrl = null;
        String thumbnailImageUrl = null;

        if (bannerImage != null && !bannerImage.isEmpty()) {
            bannerImageUrl = fileUtil.uploadSingleImage(bannerImage);
            log.info("Uploaded banner image: {}", bannerImageUrl);
        }

        if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
            thumbnailImageUrl = fileUtil.uploadSingleImage(thumbnailImage);
            log.info("Uploaded thumbnail image: {}", thumbnailImageUrl);
        }

        SaleEntity sale = mapToEntity(request);

        // Set image URLs
        sale.setBannerImageUrl(bannerImageUrl);
        sale.setThumbnailImageUrl(thumbnailImageUrl);

        // Auto set status based on dates
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(sale.getStartDate())) {
            sale.setIsActive(true); // Will be activated by scheduler
        } else if (now.isAfter(sale.getEndDate())) {
            sale.setIsActive(false);
        }

        SaleEntity savedSale = saleRepository.save(sale);

        log.info("Sale created successfully: {}", savedSale.getId());
        return mapToResponse(savedSale);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getSaleById(Long id) {
        SaleEntity sale = saleRepository.findById(id)
            .orElseThrow(() -> new ResException(ResErrorCode.SALE_NOT_FOUND));
        return mapToResponse(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getSaleByCode(String code) {
        SaleEntity sale = saleRepository.findByCodeAndIsDeleteFalse(code)
            .orElseThrow(() -> new ResException(ResErrorCode.SALE_NOT_FOUND));
        return mapToResponse(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getAllSales() {
        return saleRepository.findAllByIsDeleteFalse().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getActiveSales() {
        return saleRepository.findActiveSales(LocalDateTime.now()).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SaleResponse updateSale(Long id, SaleCreateRequest request, MultipartFile bannerImage, MultipartFile thumbnailImage) {
        log.info("Updating sale with id: {}", id);

        SaleEntity sale = saleRepository.findById(id)
            .orElseThrow(() -> new ResException(ResErrorCode.SALE_NOT_FOUND));

        if (sale.getIsDelete()) {
            throw new ResException(ResErrorCode.SALE_ALREADY_DELETED);
        }

        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ResException(ResErrorCode.SALE_INVALID_DATE);
        }

        // Check code trùng lặp (exclude ID hiện tại)
        if (saleRepository.existsByCodeAndIdNotAndIsDeleteFalse(request.getCode(), id)) {
            throw new ResException(ResErrorCode.SALE_CODE_ALREADY_EXISTS);
        }

        try {
            // Update fields
            sale.setCode(request.getCode());
            sale.setName(request.getName());
            sale.setDescription(request.getDescription());
            sale.setSaleType(request.getSaleType());
            sale.setSaleValue(request.getSaleValue());
            sale.setApplyScope(request.getApplyScope());
            sale.setMinPurchaseQuantity(request.getMinPurchaseQuantity());
            sale.setMaxDiscountAmount(request.getMaxDiscountAmount());
            sale.setQuantity(request.getQuantity());
            sale.setMinOrderValue(request.getMinOrderValue());
            sale.setStartDate(request.getStartDate());
            sale.setEndDate(request.getEndDate());
            sale.setPriority(request.getPriority());

            // Handle banner image upload
            if (bannerImage != null && !bannerImage.isEmpty()) {
                // Delete old banner image if exists
                if (sale.getBannerImageUrl() != null) {
                    try {
                        fileUtil.deleteFileByUrl(sale.getBannerImageUrl());
                    } catch (Exception e) {
                        log.warn("Failed to delete old banner image: {}", e.getMessage());
                    }
                }
                // Upload new banner image
                String newBannerUrl = fileUtil.uploadSingleImage(bannerImage);
                sale.setBannerImageUrl(newBannerUrl);
                log.info("Updated banner image: {}", newBannerUrl);
            }

            // Handle thumbnail image upload
            if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
                // Delete old thumbnail image if exists
                if (sale.getThumbnailImageUrl() != null) {
                    try {
                        fileUtil.deleteFileByUrl(sale.getThumbnailImageUrl());
                    } catch (Exception e) {
                        log.warn("Failed to delete old thumbnail image: {}", e.getMessage());
                    }
                }
                // Upload new thumbnail image
                String newThumbnailUrl = fileUtil.uploadSingleImage(thumbnailImage);
                sale.setThumbnailImageUrl(newThumbnailUrl);
                log.info("Updated thumbnail image: {}", newThumbnailUrl);
            }

            SaleEntity updatedSale = saleRepository.save(sale);

            log.info("Sale updated successfully: {}", updatedSale.getId());
            return mapToResponse(updatedSale);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Data integrity violation when updating sale: {}", e.getMessage());
            // Catch duplicate key constraint violation
            if (e.getMessage() != null && e.getMessage().contains("uk4j8ofden1k7wcurqkrimot5xo")) {
                throw new ResException(ResErrorCode.SALE_CODE_ALREADY_EXISTS);
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteSale(Long id) {
        log.info("Deleting sale with id: {}", id);

        SaleEntity sale = saleRepository.findById(id)
            .orElseThrow(() -> new ResException(ResErrorCode.SALE_NOT_FOUND));

        sale.setIsDelete(true);
        sale.setIsActive(false);
        saleRepository.save(sale);

        // TODO: Revert giá sản phẩm nếu sale đang active
        if (sale.getIsActive()) {
            revertSalePrices(id);
        }

        log.info("Sale deleted successfully: {}", id);
    }

    @Override
    @Transactional
    public void addProductsToSale(Long saleId, AddProductsToSaleRequest request) {
        log.info("Adding products to sale: {}", saleId);

        SaleEntity sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new ResException(ResErrorCode.SALE_NOT_FOUND));

        List<SaleProductEntity> saleProducts = new ArrayList<>();

        // Add products
        if (request.getProductIds() != null) {
            for (UUID productId : request.getProductIds()) {
                SaleProductEntity sp = new SaleProductEntity();
                sp.setSaleId(saleId);
                sp.setProductId(productId);
                saleProducts.add(sp);
            }
        }

        // Add SKUs
        if (request.getSkuIds() != null) {
            for (UUID skuId : request.getSkuIds()) {
                SaleProductEntity sp = new SaleProductEntity();
                sp.setSaleId(saleId);
                sp.setSkuId(skuId);
                saleProducts.add(sp);
            }
        }

        // Add categories
        if (request.getCategoryIds() != null) {
            for (UUID categoryId : request.getCategoryIds()) {
                SaleProductEntity sp = new SaleProductEntity();
                sp.setSaleId(saleId);
                sp.setCategoryId(categoryId);
                saleProducts.add(sp);
            }
        }

        // Add brands
        if (request.getBrandIds() != null) {
            for (UUID brandId : request.getBrandIds()) {
                SaleProductEntity sp = new SaleProductEntity();
                sp.setSaleId(saleId);
                sp.setBrandId(brandId);
                saleProducts.add(sp);
            }
        }

        saleProductRepository.saveAll(saleProducts);

        log.info("Added {} products to sale {}", saleProducts.size(), saleId);
    }

    @Override
    @Transactional
    public void removeProductFromSale(Long saleId, Long productId) {
        log.info("Removing product {} from sale {}", productId, saleId);
        saleProductRepository.deleteBySaleIdAndProductId(saleId, productId);
    }

    @Override
    @Transactional
    public void removeSKUFromSale(Long saleId, UUID skuId) {
        log.info("Removing SKU {} from sale {}", skuId, saleId);
        saleProductRepository.deleteBySaleIdAndSkuId(saleId, skuId);
    }

    @Override
    public List<SaleProductResponse> getSaleProducts(Long saleId) {
        return saleProductRepository.findBySaleId(saleId).stream()
            .map(this::mapToSaleProductResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SaleResponse activateSale(Long saleId) {
        log.info("Manually activating sale: {}", saleId);

        SaleEntity sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new ResException(ResErrorCode.SALE_NOT_FOUND));

        sale.setIsActive(true);
        SaleEntity activatedSale = saleRepository.save(sale);

        // Apply sale to products
        applySaleToProducts(saleId);

        log.info("Sale activated successfully: {}", saleId);
        return mapToResponse(activatedSale);
    }

    @Override
    @Transactional
    public SaleResponse pauseSale(Long saleId) {
        log.info("Pausing sale: {}", saleId);

        SaleEntity sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new ResException(ResErrorCode.SALE_NOT_FOUND));

        sale.setIsActive(false);
        SaleEntity pausedSale = saleRepository.save(sale);

        // Revert sale prices
        revertSalePrices(saleId);

        log.info("Sale paused successfully: {}", saleId);
        return mapToResponse(pausedSale);
    }

    @Override
    @Transactional
    public void applySaleToProducts(Long saleId) {
        log.info("Applying sale {} to products", saleId);

        SaleEntity sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new ResException(ResErrorCode.SALE_NOT_FOUND));

        // Lấy danh sách sản phẩm chưa apply
        List<SaleProductEntity> saleProducts = saleProductRepository
            .findBySaleIdAndIsAppliedFalse(saleId);

        for (SaleProductEntity sp : saleProducts) {
            try {
                if (sp.getSkuId() != null) {
                    // Apply cho SKU cụ thể
                    applySaleToSKU(sale, sp);
                } else if (sp.getProductId() != null) {
                    // Apply cho tất cả SKU của product
                    applySaleToProduct(sale, sp);
                } else if (sp.getCategoryId() != null) {
                    // Apply cho tất cả SKU trong category
                    applySaleToCategory(sale, sp);
                } else if (sp.getBrandId() != null) {
                    // Apply cho tất cả SKU của brand
                    applySaleToBrand(sale, sp);
                }
            } catch (Exception e) {
                log.error("Failed to apply sale to product/SKU/category/brand", e);
            }
        }

        log.info("Sale {} applied to {} products", saleId, saleProducts.size());
    }

    @Override
    @Transactional
    public void revertSalePrices(Long saleId) {
        log.info("Reverting sale prices for sale: {}", saleId);

        // Lấy danh sách sản phẩm đã apply
        List<SaleProductEntity> appliedProducts = saleProductRepository
            .findBySaleIdAndIsAppliedTrue(saleId);

        for (SaleProductEntity sp : appliedProducts) {
            try {
                if (sp.getSkuId() != null) {
                    // Revert SKU price
                    productClient.revertSKUPrice(sp.getSkuId());

                    sp.setIsApplied(false);
                    sp.setRevertedAt(LocalDateTime.now());
                    saleProductRepository.save(sp);

                    log.info("Reverted price for SKU: {}", sp.getSkuId());
                }
            } catch (Exception e) {
                log.error("Failed to revert price for SKU {}", sp.getSkuId(), e);
            }
        }

        log.info("Reverted prices for {} products", appliedProducts.size());
    }

    // ========== HELPER METHODS ==========

    /**
     * Apply sale to all SKUs of a product
     */
    private void applySaleToProduct(SaleEntity sale, SaleProductEntity saleProduct) {
        try {
            log.info("Applying sale {} to all SKUs of product {}", sale.getId(), saleProduct.getProductId());

            // Lấy tất cả SKU của product
            List<ProductClient.SKUResponse> skus = productClient.getSKUsByProductId(saleProduct.getProductId())
                .getData();

            if (skus == null || skus.isEmpty()) {
                log.warn("No SKUs found for product {}", saleProduct.getProductId());
                return;
            }

            // Apply sale cho từng SKU
            for (ProductClient.SKUResponse sku : skus) {
                try {
                    applySaleToSingleSKU(sale, saleProduct, sku);
                } catch (Exception e) {
                    log.error("Failed to apply sale to SKU {} of product {}",
                        sku.getId(), saleProduct.getProductId(), e);
                }
            }

            log.info("Applied sale to {} SKUs of product {}", skus.size(), saleProduct.getProductId());
        } catch (Exception e) {
            log.error("Failed to apply sale to product {}", saleProduct.getProductId(), e);
            throw e;
        }
    }

    /**
     * Apply sale to all SKUs in a category
     */
    private void applySaleToCategory(SaleEntity sale, SaleProductEntity saleProduct) {
        try {
            log.info("Applying sale {} to all SKUs in category {}", sale.getId(), saleProduct.getCategoryId());

            // Lấy tất cả SKU trong category
            List<ProductClient.SKUResponse> skus = productClient.getSKUsByCategoryId(saleProduct.getCategoryId())
                .getData();

            if (skus == null || skus.isEmpty()) {
                log.warn("No SKUs found for category {}", saleProduct.getCategoryId());
                return;
            }

            // Apply sale cho từng SKU
            for (ProductClient.SKUResponse sku : skus) {
                try {
                    applySaleToSingleSKU(sale, saleProduct, sku);
                } catch (Exception e) {
                    log.error("Failed to apply sale to SKU {} of category {}",
                        sku.getId(), saleProduct.getCategoryId(), e);
                }
            }

            log.info("Applied sale to {} SKUs in category {}", skus.size(), saleProduct.getCategoryId());
        } catch (Exception e) {
            log.error("Failed to apply sale to category {}", saleProduct.getCategoryId(), e);
            throw e;
        }
    }

    /**
     * Apply sale to all SKUs of a brand
     */
    private void applySaleToBrand(SaleEntity sale, SaleProductEntity saleProduct) {
        try {
            log.info("Applying sale {} to all SKUs of brand {}", sale.getId(), saleProduct.getBrandId());

            // Lấy tất cả SKU của brand
            List<ProductClient.SKUResponse> skus = productClient.getSKUsByBrandId(saleProduct.getBrandId())
                .getData();

            if (skus == null || skus.isEmpty()) {
                log.warn("No SKUs found for brand {}", saleProduct.getBrandId());
                return;
            }

            // Apply sale cho từng SKU
            for (ProductClient.SKUResponse sku : skus) {
                try {
                    applySaleToSingleSKU(sale, saleProduct, sku);
                } catch (Exception e) {
                    log.error("Failed to apply sale to SKU {} of brand {}",
                        sku.getId(), saleProduct.getBrandId(), e);
                }
            }

            log.info("Applied sale to {} SKUs of brand {}", skus.size(), saleProduct.getBrandId());
        } catch (Exception e) {
            log.error("Failed to apply sale to brand {}", saleProduct.getBrandId(), e);
            throw e;
        }
    }

    /**
     * Apply sale to a single SKU (helper method for product/category/brand)
     */
    private void applySaleToSingleSKU(SaleEntity sale, SaleProductEntity saleProduct, ProductClient.SKUResponse sku) {
        try {
            // Calculate sale price
            BigDecimal originalPrice = BigDecimal.valueOf(sku.getPrice());
            BigDecimal salePrice = calculateSalePrice(originalPrice, sale);
            BigDecimal discountAmount = originalPrice.subtract(salePrice);

            // Update SaleProduct entity
            saleProduct.setOriginalPrice(originalPrice);
            saleProduct.setSalePrice(salePrice);
            saleProduct.setDiscountAmount(discountAmount);
            saleProduct.setIsApplied(true);
            saleProduct.setAppliedAt(LocalDateTime.now());
            saleProductRepository.save(saleProduct);

            // Call Product Service to update SKU price
            ProductClient.UpdateSKUSalePriceRequest request =
                new ProductClient.UpdateSKUSalePriceRequest(
                    originalPrice.doubleValue(),
                    salePrice.doubleValue(),
                    sale.getId()
                );

            productClient.updateSKUSalePrice(sku.getId(), request);

            log.debug("Applied sale to SKU {}: {} -> {}", sku.getId(), originalPrice, salePrice);
        } catch (Exception e) {
            log.error("Failed to apply sale to SKU {}", sku.getId(), e);
            throw e;
        }
    }

    /**
     * Apply sale to specific SKU
     */
    private void applySaleToSKU(SaleEntity sale, SaleProductEntity saleProduct) {
        try {
            // 1. Get SKU info from Product Service
            ProductClient.SKUResponse sku = productClient.getSKU(saleProduct.getSkuId())
                .getData();

            if (sku == null) {
                log.error("SKU not found: {}", saleProduct.getSkuId());
                return;
            }

            // 2. Calculate sale price
            BigDecimal originalPrice = BigDecimal.valueOf(sku.getPrice());
            BigDecimal salePrice = calculateSalePrice(originalPrice, sale);
            BigDecimal discountAmount = originalPrice.subtract(salePrice);

            // 3. Update SaleProduct entity
            saleProduct.setOriginalPrice(originalPrice);
            saleProduct.setSalePrice(salePrice);
            saleProduct.setDiscountAmount(discountAmount);
            saleProduct.setIsApplied(true);
            saleProduct.setAppliedAt(LocalDateTime.now());
            saleProductRepository.save(saleProduct);

            // 4. Call Product Service to update SKU price
            ProductClient.UpdateSKUSalePriceRequest request =
                new ProductClient.UpdateSKUSalePriceRequest(
                    originalPrice.doubleValue(),
                    salePrice.doubleValue(),
                    sale.getId()
                );

            productClient.updateSKUSalePrice(saleProduct.getSkuId(), request);

            log.info("Applied sale to SKU {}: {} -> {}",
                saleProduct.getSkuId(), originalPrice, salePrice);

        } catch (Exception e) {
            log.error("Failed to apply sale to SKU {}", saleProduct.getSkuId(), e);
            throw e;
        }
    }

    /**
     * Calculate sale price based on sale type
     */
    private BigDecimal calculateSalePrice(BigDecimal originalPrice, SaleEntity sale) {
        BigDecimal discount;

        if (sale.getSaleType() == SaleType.PERCENTAGE) {
            // Giảm theo %
            discount = originalPrice
                .multiply(sale.getSaleValue())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // Apply max discount limit
            if (sale.getMaxDiscountAmount() != null &&
                discount.compareTo(sale.getMaxDiscountAmount()) > 0) {
                discount = sale.getMaxDiscountAmount();
            }
        } else {
            // Giảm số tiền cố định
            discount = sale.getSaleValue();
        }

        BigDecimal salePrice = originalPrice.subtract(discount);

        // Giá sau sale không được âm
        if (salePrice.compareTo(BigDecimal.ZERO) < 0) {
            salePrice = BigDecimal.ZERO;
        }

        return salePrice;
    }

    /**
     * Map Entity to Response
     */
    private SaleResponse mapToResponse(SaleEntity entity) {
        SaleResponse response = new SaleResponse();
        response.setId(entity.getId());
        response.setCode(entity.getCode());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setSaleType(entity.getSaleType());
        response.setSaleValue(entity.getSaleValue());
        response.setApplyScope(entity.getApplyScope());
        response.setMinPurchaseQuantity(entity.getMinPurchaseQuantity());
        response.setMaxDiscountAmount(entity.getMaxDiscountAmount());
        response.setQuantity(entity.getQuantity());
        response.setUsedCount(entity.getUsedCount());
        response.setMinOrderValue(entity.getMinOrderValue());
        response.setStartDate(entity.getStartDate());
        response.setEndDate(entity.getEndDate());
        response.setStatus(entity.getStatus());
        response.setIsActive(entity.getIsActive());
        response.setPriority(entity.getPriority());
        response.setBannerImageUrl(entity.getBannerImageUrl());
        response.setThumbnailImageUrl(entity.getThumbnailImageUrl());
        response.setTotalAppliedCount(entity.getTotalAppliedCount());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }

    /**
     * Map Request to Entity
     */
    private SaleEntity mapToEntity(SaleCreateRequest request) {
        SaleEntity entity = new SaleEntity();
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setSaleType(request.getSaleType());
        entity.setSaleValue(request.getSaleValue());
        entity.setApplyScope(request.getApplyScope());
        entity.setMinPurchaseQuantity(request.getMinPurchaseQuantity());
        entity.setMaxDiscountAmount(request.getMaxDiscountAmount());
        entity.setQuantity(request.getQuantity());
        entity.setMinOrderValue(request.getMinOrderValue());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setPriority(request.getPriority());
        entity.setBannerImageUrl(request.getBannerImageUrl());
        entity.setThumbnailImageUrl(request.getThumbnailImageUrl());
        return entity;
    }

    /**
     * Map SaleProduct to Response
     */
    private SaleProductResponse mapToSaleProductResponse(SaleProductEntity entity) {
        SaleProductResponse response = new SaleProductResponse();
        response.setId(entity.getId());
        response.setSaleId(entity.getSaleId());
        response.setProductId(entity.getProductId());
        response.setSkuId(entity.getSkuId());
        response.setCategoryId(entity.getCategoryId());
        response.setBrandId(entity.getBrandId());
        response.setOriginalPrice(entity.getOriginalPrice());
        response.setSalePrice(entity.getSalePrice());
        response.setDiscountAmount(entity.getDiscountAmount());
        response.setIsApplied(entity.getIsApplied());
        response.setAppliedAt(entity.getAppliedAt());
        response.setRevertedAt(entity.getRevertedAt());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }
}
