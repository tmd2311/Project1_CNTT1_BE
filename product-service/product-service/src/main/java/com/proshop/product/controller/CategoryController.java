package com.proshop.product.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proshop.product.dto.request.CategoryCreateRequest;
import com.proshop.product.dto.request.CategoryUpdateRequest;
import com.proshop.product.dto.response.CategoryDeleteResponse;
import com.proshop.product.dto.response.CategoryResponse;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @DeleteMapping("/category/delete")
    public ResponseEntity<GeneralResponse<CategoryDeleteResponse>> deleteCategory(
        @RequestParam("id") UUID id) {

        GeneralResponse<CategoryDeleteResponse> response = categoryService.deleteCategory(id);
        if ("404".equals(response.getStatus().getCode())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if ("400".equals(response.getStatus().getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/category/{id}")
    public ResponseEntity<GeneralResponse<CategoryResponse>> updateCategory(
        @PathVariable("id") UUID id,
        @RequestPart("category") String categoryJson,
        @RequestPart("image") MultipartFile image) throws JsonProcessingException {

        // Convert JSON text → Object
        ObjectMapper mapper = new ObjectMapper();
        CategoryUpdateRequest request = mapper.readValue(categoryJson, CategoryUpdateRequest.class);

        GeneralResponse<CategoryResponse> response = categoryService.updateCategory(id, request, image);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/search")
    public ResponseEntity<GeneralResponse<Page<CategoryResponse>>> searchCategories(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "12") int size) {

        GeneralResponse<Page<CategoryResponse>> response = categoryService.searchCategories(name, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<GeneralResponse<CategoryResponse>> getCategoryById(
        @PathVariable("id") UUID id) {

        GeneralResponse<CategoryResponse> response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getAllCategories() {
        GeneralResponse<List<CategoryResponse>> response = categoryService.getAllCategories();
        return ResponseEntity.ok(response);
    }

    // ========== ADDITIONAL ENDPOINTS FOR PC STORE ==========
    @GetMapping("/category/roots")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getRootCategories() {
        GeneralResponse<List<CategoryResponse>> response = categoryService.getRootCategories();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{id}/tree")
    public ResponseEntity<GeneralResponse<CategoryResponse>> getCategoryTree(
        @PathVariable("id") UUID id) {

        GeneralResponse<CategoryResponse> response = categoryService.getCategoryWithChildren(id);
        if ("404".equals(response.getStatus().getCode())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{id}/children")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getCategoryChildren(
        @PathVariable("id") UUID id) {

        GeneralResponse<List<CategoryResponse>> response = categoryService.getCategoryChildren(id);
        if ("404".equals(response.getStatus().getCode())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/by-type")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getCategoriesByType(
        @RequestParam("type") String type,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "20") int size) {

        GeneralResponse<List<CategoryResponse>> response = categoryService.getCategoriesByType(type, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{id}/breadcrumb")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getCategoryBreadcrumb(
        @PathVariable("id") UUID id) {

        GeneralResponse<List<CategoryResponse>> response = categoryService.getCategoryBreadcrumb(id);
        if ("404".equals(response.getStatus().getCode())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/category")
    public ResponseEntity<GeneralResponse<CategoryResponse>> createCategory(
        @RequestPart("category") String categoryJson,
        @RequestPart("image") MultipartFile image) throws JsonProcessingException {

        // Convert JSON text → Object
        ObjectMapper mapper = new ObjectMapper();
        CategoryCreateRequest request = mapper.readValue(categoryJson, CategoryCreateRequest.class);

        GeneralResponse<CategoryResponse> response = categoryService.createCategory(request, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/category/slug/{slug}")
    public ResponseEntity<GeneralResponse<CategoryResponse>> getCategoryBySlug(
        @PathVariable("slug") String slug) {

        GeneralResponse<CategoryResponse> response = categoryService.getCategoryBySlug(slug);
        if ("404".equals(response.getStatus().getCode())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/laptops")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getLaptopCategories() {
        GeneralResponse<List<CategoryResponse>> response = categoryService.getLaptopCategories();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/components")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getComponentCategories() {
        GeneralResponse<List<CategoryResponse>> response = categoryService.getComponentCategories();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/peripherals")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getPeripheralCategories() {
        GeneralResponse<List<CategoryResponse>> response = categoryService.getPeripheralCategories();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/desktop-pcs")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getDesktopPCCategories() {
        GeneralResponse<List<CategoryResponse>> response = categoryService.getDesktopPCCategories();
        return ResponseEntity.ok(response);
    }
}
