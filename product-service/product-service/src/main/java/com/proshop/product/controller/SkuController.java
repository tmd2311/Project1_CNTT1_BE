package com.proshop.product.controller;

import com.proshop.product.dto.request.SKURequest;
import com.proshop.product.dto.request.SKUstockRequest;
import com.proshop.product.dto.request.UpdateSKUSalePriceRequest;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.ResponseStatus;
import com.proshop.product.dto.response.SKUResponse;
import com.proshop.product.service.sku.SKUService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sku")
@RequiredArgsConstructor
public class SkuController {

    private final SKUService skuService;

    @PostMapping
    public ResponseEntity<GeneralResponse<SKUResponse>> create(@RequestBody SKURequest request) {
        SKUResponse response = skuService.createSKU(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse<SKUResponse>> update(
        @PathVariable("id") UUID id,
        @RequestBody SKURequest request) {
        SKUResponse response = skuService.updateSKU(id, request);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<Void>> delete(@PathVariable("id") UUID id) {
        skuService.deleteSKU(id);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, null, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<SKUResponse>> getById(@PathVariable("id") UUID id) {
        SKUResponse response = skuService.getById(id);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    @GetMapping
    public ResponseEntity<GeneralResponse<List<SKUResponse>>> getAll() {
        List<SKUResponse> list = skuService.getAll();
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, list, null));
    }

    @PutMapping("/stock/{id}")
    public ResponseEntity<GeneralResponse<SKUResponse>> updateStock(
        @PathVariable("id") UUID id,
        @RequestBody SKUstockRequest request) {
        SKUResponse response = skuService.updateStockSKU(id, request);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null));
    }

    /**
     * Cập nhật giá sale cho SKU (được gọi từ Sale Service)
     */
    @PutMapping("/{id}/sale-price")
    public ResponseEntity<GeneralResponse<Void>> updateSKUSalePrice(
        @PathVariable("id") UUID id,
        @RequestBody UpdateSKUSalePriceRequest request) {
        skuService.updateSalePrice(id, request);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, null, null));
    }

    /**
     * Revert giá SKU về giá gốc (được gọi từ Sale Service)
     */
    @PutMapping("/{id}/revert-price")
    public ResponseEntity<GeneralResponse<Void>> revertSKUPrice(@PathVariable("id") UUID id) {
        skuService.revertPrice(id);
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, null, null));
    }
}