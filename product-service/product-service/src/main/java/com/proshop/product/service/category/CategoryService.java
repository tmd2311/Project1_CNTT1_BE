package com.proshop.product.service.category;

import com.proshop.product.dto.request.CategoryCreateRequest;
import com.proshop.product.dto.request.CategoryUpdateRequest;
import com.proshop.product.dto.response.CategoryDeleteResponse;
import com.proshop.product.dto.response.CategoryResponse;
import com.proshop.product.dto.response.GeneralResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface CategoryService {

    // ========== BASIC CRUD OPERATIONS ==========

    /**
     * Create a new category
     */
    GeneralResponse<CategoryResponse> createCategory(CategoryCreateRequest request, MultipartFile image);

    /**
     * Update an existing category
     */
    GeneralResponse<CategoryResponse> updateCategory(UUID id, CategoryUpdateRequest request, MultipartFile image);

    /**
     * Delete a category by ID
     */
    GeneralResponse<CategoryDeleteResponse> deleteCategory(UUID id);

    /**
     * Search categories with pagination and optional name filter
     */
    GeneralResponse<Page<CategoryResponse>> searchCategories(String name, int page, int size);

    // ========== CATEGORY HIERARCHY OPERATIONS ==========

    /**
     * Get all root categories (categories without parent)
     */
    GeneralResponse<List<CategoryResponse>> getRootCategories();

    /**
     * Get category with all its children (full tree structure)
     */
    GeneralResponse<CategoryResponse> getCategoryWithChildren(UUID id);

    /**
     * Get direct children of a category (one level only)
     */
    GeneralResponse<List<CategoryResponse>> getCategoryChildren(UUID id);

    /**
     * Get category breadcrumb path from root to current category
     */
    GeneralResponse<List<CategoryResponse>> getCategoryBreadcrumb(UUID id);

    // ========== QUERY BY ATTRIBUTES ==========

    /**
     * Get category by slug (SEO friendly URL)
     */
    GeneralResponse<CategoryResponse> getCategoryBySlug(String slug);

    /**
     * Get categories by type with pagination
     * Types: ROOT, LAPTOP_TYPE, PC_TYPE, COMPONENT_TYPE, PERIPHERAL_TYPE, etc.
     */
    GeneralResponse<List<CategoryResponse>> getCategoriesByType(String type, int page, int size);

    // ========== PC STORE SPECIFIC OPERATIONS ==========

    /**
     * Get all laptop categories (Gaming Laptops, Office Laptops, Workstation Laptops, Ultrabooks)
     */
    GeneralResponse<List<CategoryResponse>> getLaptopCategories();

    /**
     * Get all PC component categories (CPU, GPU, RAM, Storage, etc.)
     */
    GeneralResponse<List<CategoryResponse>> getComponentCategories();

    /**
     * Get all peripheral categories (Monitors, Keyboards, Mice, Headphones)
     */
    GeneralResponse<List<CategoryResponse>> getPeripheralCategories();

    /**
     * Get desktop PC categories (Gaming PCs, Office PCs, Workstation PCs)
     */
    GeneralResponse<List<CategoryResponse>> getDesktopPCCategories();

    /**
     * Get storage categories (SSDs, HDDs with their subcategories)
     */
    GeneralResponse<List<CategoryResponse>> getStorageCategories();

    /**
     * Get cooling system categories (CPU Coolers, Case Fans, Liquid Cooling)
     */
    GeneralResponse<List<CategoryResponse>> getCoolingCategories();

    // ========== UTILITY OPERATIONS ==========

    /**
     * Check if category exists by ID
     */
    boolean existsById(UUID id);

    /**
     * Check if category exists by slug
     */
    boolean existsBySlug(String slug);

    /**
     * Get category by ID (internal use)
     */
    GeneralResponse<CategoryResponse> getCategoryById(UUID id);

    /**
     * Get all categories in flat list (no pagination)
     */
    GeneralResponse<List<CategoryResponse>> getAllCategories();

    /**
     * Get categories by parent ID
     */
    GeneralResponse<List<CategoryResponse>> getCategoriesByParentId(UUID parentId);

    /**
     * Count products in category (including subcategories)
     */
    GeneralResponse<Long> getProductCountInCategory(UUID categoryId, boolean includeSubcategories);
}