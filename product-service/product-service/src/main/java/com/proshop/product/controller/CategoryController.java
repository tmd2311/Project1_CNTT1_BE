package com.proshop.product.controller;

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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @DeleteMapping("/category/delete")
    public ResponseEntity<GeneralResponse<CategoryDeleteResponse>> deleteCategory(@RequestParam("id") UUID id) {
        GeneralResponse<CategoryDeleteResponse> response = categoryService.deleteCategory(id);
        if (response.getStatus().getCode().equals("404")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if (response.getStatus().getCode().equals("400")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/category/{id}")
    public ResponseEntity<GeneralResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @RequestBody @Valid CategoryUpdateRequest request) {
        GeneralResponse<CategoryResponse> response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/search")
    public ResponseEntity<GeneralResponse<Page<CategoryResponse>>> searchCategories(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        GeneralResponse<Page<CategoryResponse>> response = categoryService.searchCategories(name, page, size);
        return ResponseEntity.ok(response);
    }

    // ========== ADDITIONAL ENDPOINTS FOR PC STORE ==========

    /**
     * Get root categories (Laptops, Desktop PCs, PC Components, Peripherals)
     */
    @GetMapping("/category/roots")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getRootCategories() {
        GeneralResponse<List<CategoryResponse>> response = categoryService.getRootCategories();
        return ResponseEntity.ok(response);
    }

    /**
     * Get category hierarchy tree starting from a specific category
     */
    @GetMapping("/category/{id}/tree")
    public ResponseEntity<GeneralResponse<CategoryResponse>> getCategoryTree(@PathVariable UUID id) {
        GeneralResponse<CategoryResponse> response = categoryService.getCategoryWithChildren(id);
        if (response.getStatus().getCode().equals("404")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Get all children of a category (one level only)
     */
    @GetMapping("/category/{id}/children")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getCategoryChildren(@PathVariable UUID id) {
        GeneralResponse<List<CategoryResponse>> response = categoryService.getCategoryChildren(id);
        if (response.getStatus().getCode().equals("404")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Get categories by type (for PC store: LAPTOP_TYPE, PC_TYPE, COMPONENT_TYPE, PERIPHERAL_TYPE)
     */
    @GetMapping("/category/by-type")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getCategoriesByType(
            @RequestParam String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        GeneralResponse<List<CategoryResponse>> response = categoryService.getCategoriesByType(type, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Get full category breadcrumb path
     */
    @GetMapping("/category/{id}/breadcrumb")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getCategoryBreadcrumb(@PathVariable UUID id) {
        GeneralResponse<List<CategoryResponse>> response = categoryService.getCategoryBreadcrumb(id);
        if (response.getStatus().getCode().equals("404")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Create new category
     */
    @PostMapping("/category")
    public ResponseEntity<GeneralResponse<CategoryResponse>> createCategory(
            @RequestBody @Valid CategoryCreateRequest request) {
        GeneralResponse<CategoryResponse> response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get category by slug (SEO friendly)
     */
    @GetMapping("/category/slug/{slug}")
    public ResponseEntity<GeneralResponse<CategoryResponse>> getCategoryBySlug(@PathVariable String slug) {
        GeneralResponse<CategoryResponse> response = categoryService.getCategoryBySlug(slug);
        if (response.getStatus().getCode().equals("404")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Get categories suitable for laptops (Gaming Laptops, Office Laptops, etc.)
     */
    @GetMapping("/category/laptops")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getLaptopCategories() {
        GeneralResponse<List<CategoryResponse>> response = categoryService.getLaptopCategories();
        return ResponseEntity.ok(response);
    }

    /**
     * Get categories suitable for PC components
     */
    @GetMapping("/category/components")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getComponentCategories() {
        GeneralResponse<List<CategoryResponse>> response = categoryService.getComponentCategories();
        return ResponseEntity.ok(response);
    }

    /**
     * Get categories for peripherals
     */
    @GetMapping("/category/peripherals")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getPeripheralCategories() {
        GeneralResponse<List<CategoryResponse>> response = categoryService.getPeripheralCategories();
        return ResponseEntity.ok(response);
    }
    /**
    * Get categories suitable for desktop-pcs
     */
    @GetMapping("/category/desktop-pcs")
    public ResponseEntity<GeneralResponse<List<CategoryResponse>>> getDesktopPCCategories() {
        GeneralResponse<List<CategoryResponse>> response = categoryService.getDesktopPCCategories();
        return ResponseEntity.ok(response);
    }

}