package com.proshop.product.controller;

import com.proshop.product.dto.request.BrandUpdateRequest;
import com.proshop.product.dto.response.BrandDeleteResponse;
import com.proshop.product.dto.response.BrandResponse;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.service.brand.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BrandController {


    private final BrandService brandService;

    @DeleteMapping("/brand/delete")
    public ResponseEntity<GeneralResponse<BrandDeleteResponse>> deleteBrand(@RequestParam("id") UUID id) {
        GeneralResponse<BrandDeleteResponse> response = brandService.deleteBrand(id);
        if (response.getStatus().getCode().equals("404")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/brand/{id}")
    public ResponseEntity<GeneralResponse<BrandResponse>> updateBrand(
            @PathVariable UUID id,
            @RequestBody BrandUpdateRequest request) {
        GeneralResponse<BrandResponse> response = brandService.updateBrand(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/brand/search")
    public ResponseEntity<GeneralResponse<Page<BrandResponse>>> searchBrands(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        GeneralResponse<Page<BrandResponse>> response = brandService.searchBrands(name, page, size);
        return ResponseEntity.ok(response);
    }

}
