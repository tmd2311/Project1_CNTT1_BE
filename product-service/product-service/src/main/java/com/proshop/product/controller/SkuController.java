package com.proshop.product.controller;

import com.proshop.product.dto.request.SKURequest;
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
    public ResponseEntity<SKUResponse> create(@RequestBody SKURequest request) {
        return ResponseEntity.ok(skuService.createSKU(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SKUResponse> update(@PathVariable UUID id, @RequestBody SKURequest request) {
        return ResponseEntity.ok(skuService.updateSKU(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        skuService.deleteSKU(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SKUResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(skuService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SKUResponse>> getAll() {
        return ResponseEntity.ok(skuService.getAll());
    }
}
