package com.proshop.product.controller;

import com.proshop.product.dto.request.SKURequest;
import com.proshop.product.dto.request.SKUstockRequest;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.ResponseStatus;
import com.proshop.product.dto.response.SKUResponse;
import com.proshop.product.service.sku.SKUService;
import lombok.RequiredArgsConstructor;
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
        return success(skuService.createSKU(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse<SKUResponse>> update(
            @PathVariable UUID id,
            @RequestBody SKURequest request) {
        return success(skuService.updateSKU(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<Void>> delete(@PathVariable UUID id) {
        skuService.deleteSKU(id);
        return success(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<SKUResponse>> getById(@PathVariable UUID id) {
        return success(skuService.getById(id));
    }

    @GetMapping
    public ResponseEntity<GeneralResponse<List<SKUResponse>>> getAll() {
        return success(skuService.getAll());
    }

    @PutMapping("/stock/{id}")
    public ResponseEntity<GeneralResponse<SKUResponse>> updateStock(
            @PathVariable UUID id,
            @RequestBody SKUstockRequest request) {
        return success(skuService.updateStockSKU(id, request));
    }

    // Helper method
    private <T> ResponseEntity<GeneralResponse<T>> success(T data) {
        return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null));
    }
}