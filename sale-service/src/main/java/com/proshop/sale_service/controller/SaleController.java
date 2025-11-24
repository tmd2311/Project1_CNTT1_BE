package com.proshop.sale_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.proshop.exceptionlib.dto.response.ResponseStatus;
import com.proshop.sale_service.dto.request.AddProductsToSaleRequest;
import com.proshop.sale_service.dto.request.SaleCreateRequest;
import com.proshop.sale_service.dto.response.GeneralResponse;
import com.proshop.sale_service.dto.response.SaleProductResponse;
import com.proshop.sale_service.dto.response.SaleResponse;
import com.proshop.sale_service.service.sale.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Controller quản lý Sale (Chương trình giảm giá sản phẩm)
 */
@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Slf4j
public class SaleController {

    private final SaleService saleService;

    /**
     * Tạo sale mới
     * POST /api/v1/sales
     * Form-data format:
     * - sale: JSON string
     * - bannerImage: file (optional)
     * - thumbnailImage: file (optional)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneralResponse<SaleResponse>> createSale(
        @RequestPart("sale") String saleJson,
        @RequestPart(value = "bannerImage", required = false) MultipartFile bannerImage,
        @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage
    ) throws JsonProcessingException {
        log.debug("Received sale JSON: {}", saleJson);

        // Convert JSON text → Object
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Support LocalDateTime

        try {
            SaleCreateRequest request = mapper.readValue(saleJson, SaleCreateRequest.class);

            log.info("Creating sale: {}", request.getCode());
            SaleResponse response = saleService.createSale(request, bannerImage, thumbnailImage);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
        } catch (JsonProcessingException e) {
            log.error("Failed to parse sale JSON. Received: {}", saleJson, e);
            throw e;
        }
    }

    /**
     * Lấy danh sách tất cả sales
     * GET /api/v1/sales
     */
    @GetMapping
    public ResponseEntity<GeneralResponse<List<SaleResponse>>> getAllSales() {
        log.info("Getting all sales");
        List<SaleResponse> response = saleService.getAllSales();
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    /**
     * Lấy sale theo ID
     * GET /api/v1/sales/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<SaleResponse>> getSaleById(@PathVariable Long id) {
        log.info("Getting sale by id: {}", id);
        SaleResponse response = saleService.getSaleById(id);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    /**
     * Lấy sale theo code
     * GET /api/v1/sales/code/{code}
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<GeneralResponse<SaleResponse>> getSaleByCode(@PathVariable String code) {
        log.info("Getting sale by code: {}", code);
        SaleResponse response = saleService.getSaleByCode(code);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    /**
     * Lấy danh sách sales đang active
     * GET /api/v1/sales/active
     */
    @GetMapping("/active")
    public ResponseEntity<GeneralResponse<List<SaleResponse>>> getActiveSales() {
        log.info("Getting active sales");
        List<SaleResponse> response = saleService.getActiveSales();
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    /**
     * Cập nhật sale
     * PUT /api/v1/sales/{id}
     * Form-data format:
     * - sale: JSON string
     * - bannerImage: file (optional)
     * - thumbnailImage: file (optional)
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneralResponse<SaleResponse>> updateSale(
        @PathVariable Long id,
        @RequestPart("sale") String saleJson,
        @RequestPart(value = "bannerImage", required = false) MultipartFile bannerImage,
        @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage
    ) throws JsonProcessingException {
        log.debug("Received sale JSON for update: {}", saleJson);

        // Convert JSON text → Object
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Support LocalDateTime

        try {
            SaleCreateRequest request = mapper.readValue(saleJson, SaleCreateRequest.class);

            log.info("Updating sale: {}", id);
            SaleResponse response = saleService.updateSale(id, request, bannerImage, thumbnailImage);
            return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
        } catch (JsonProcessingException e) {
            log.error("Failed to parse sale JSON. Received: {}", saleJson, e);
            throw e;
        }
    }

    /**
     * Xóa mềm sale
     * DELETE /api/v1/sales/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<Void>> deleteSale(@PathVariable Long id) {
        log.info("Deleting sale: {}", id);
        saleService.deleteSale(id);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, null, null));
    }

    // ========== QUẢN LÝ SẢN PHẨM TRONG SALE ==========

    /**
     * Thêm sản phẩm vào sale
     * POST /api/v1/sales/{id}/products
     */
    @PostMapping("/{id}/products")
    public ResponseEntity<GeneralResponse<Void>> addProductsToSale(
        @PathVariable Long id,
        @RequestBody AddProductsToSaleRequest request
    ) {
        log.info("Adding products to sale: {}", id);
        saleService.addProductsToSale(id, request);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, null, null));
    }

    /**
     * Lấy danh sách sản phẩm trong sale
     * GET /api/v1/sales/{id}/products
     */
    @GetMapping("/{id}/products")
    public ResponseEntity<GeneralResponse<List<SaleProductResponse>>> getSaleProducts(
        @PathVariable Long id
    ) {
        log.info("Getting products in sale: {}", id);
        List<SaleProductResponse> response = saleService.getSaleProducts(id);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    /**
     * Xóa sản phẩm khỏi sale
     * DELETE /api/v1/sales/{id}/products/{productId}
     */
    @DeleteMapping("/{id}/products/{productId}")
    public ResponseEntity<GeneralResponse<Void>> removeProductFromSale(
        @PathVariable Long id,
        @PathVariable Long productId
    ) {
        log.info("Removing product {} from sale {}", productId, id);
        saleService.removeProductFromSale(id, productId);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, null, null));
    }

    /**
     * Xóa SKU khỏi sale
     * DELETE /api/v1/sales/{id}/skus/{skuId}
     */
    @DeleteMapping("/{id}/skus/{skuId}")
    public ResponseEntity<GeneralResponse<Void>> removeSKUFromSale(
        @PathVariable Long id,
        @PathVariable UUID skuId
    ) {
        log.info("Removing SKU {} from sale {}", skuId, id);
        saleService.removeSKUFromSale(id, skuId);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, null, null));
    }

    // ========== ĐIỀU KHIỂN SALE ==========

    /**
     * Kích hoạt sale (manual)
     * POST /api/v1/sales/{id}/activate
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<GeneralResponse<SaleResponse>> activateSale(@PathVariable Long id) {
        log.info("Manually activating sale: {}", id);
        SaleResponse response = saleService.activateSale(id);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    /**
     * Tạm dừng sale
     * POST /api/v1/sales/{id}/pause
     */
    @PostMapping("/{id}/pause")
    public ResponseEntity<GeneralResponse<SaleResponse>> pauseSale(@PathVariable Long id) {
        log.info("Pausing sale: {}", id);
        SaleResponse response = saleService.pauseSale(id);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    /**
     * Áp dụng sale vào sản phẩm (update giá SKU)
     * POST /api/v1/sales/{id}/apply
     */
    @PostMapping("/{id}/apply")
    public ResponseEntity<GeneralResponse<Void>> applySale(@PathVariable Long id) {
        log.info("Applying sale to products: {}", id);
        saleService.applySaleToProducts(id);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, null, null));
    }

    /**
     * Hoàn giá gốc (revert sale)
     * POST /api/v1/sales/{id}/revert
     */
    @PostMapping("/{id}/revert")
    public ResponseEntity<GeneralResponse<Void>> revertSale(@PathVariable Long id) {
        log.info("Reverting sale prices: {}", id);
        saleService.revertSalePrices(id);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, null, null));
    }
}
