package com.proshop.product.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proshop.product.dto.request.BrandCreateRequest;
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

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/brand")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<GeneralResponse<BrandResponse>> createBrand(
        @RequestPart("brand") String brandJson,
        @RequestPart("image") MultipartFile image) throws JsonProcessingException {

        // Convert JSON text → Object
        ObjectMapper mapper = new ObjectMapper();
        BrandCreateRequest request = mapper.readValue(brandJson, BrandCreateRequest.class);

        GeneralResponse<BrandResponse> response = brandService.createBrand(request, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse<BrandResponse>> updateBrand(
        @PathVariable("id") UUID id,
        @RequestPart("brand") String brandJson,
        @RequestPart("image") MultipartFile image) throws JsonProcessingException {

        // Convert JSON text → Object
        ObjectMapper mapper = new ObjectMapper();
        BrandUpdateRequest request = mapper.readValue(brandJson, BrandUpdateRequest.class);

        GeneralResponse<BrandResponse> response = brandService.updateBrand(id, request, image);

        if ("404".equals(response.getStatus().getCode())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<BrandDeleteResponse>> deleteBrand(
        @PathVariable("id") UUID id) {

        GeneralResponse<BrandDeleteResponse> response = brandService.deleteBrand(id);

        switch (response.getStatus().getCode()) {
            case "404":
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            case "400":
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            default:
                return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<BrandResponse>> getBrandById(
        @PathVariable("id") UUID id) {

        GeneralResponse<BrandResponse> response = brandService.getBrandById(id);
        if ("404".equals(response.getStatus().getCode())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<GeneralResponse<Page<BrandResponse>>> searchBrands(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "12") int size) {

        GeneralResponse<Page<BrandResponse>> response = brandService.searchBrands(name, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<GeneralResponse<Page<BrandResponse>>> getAllBrands(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size) {

        GeneralResponse<Page<BrandResponse>> response = brandService.getAllBrands(page, size);
        return ResponseEntity.ok(response);
    }
}
