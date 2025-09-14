package com.proshop.product.service.category.impl;

import com.proshop.product.dto.request.CategoryCreateRequest;
import com.proshop.product.dto.request.CategoryUpdateRequest;
import com.proshop.product.dto.response.CategoryDeleteResponse;
import com.proshop.product.dto.response.CategoryResponse;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.ResponseStatus;
import com.proshop.product.entity.CategoryEntity;
import com.proshop.product.repository.CategoryRepository;
import com.proshop.product.exceptions.ResException;
import com.proshop.product.service.category.CategoryService;
import com.proshop.product.utils.enums.ResErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public GeneralResponse<CategoryDeleteResponse> deleteCategory(UUID id) {
        CategoryEntity category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return new GeneralResponse<>(
                    new ResponseStatus("404", "Không tìm thấy danh mục", "Category Not Found"),
                    null,
                    null
            );
        }

        // Check if category has children (subcategories)
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            return new GeneralResponse<>(
                    new ResponseStatus("400", "Không thể xóa danh mục có danh mục con", "Cannot delete category with subcategories"),
                    null,
                    null
            );
        }

        // TODO: Check if category has products (should be validated in business logic)
        // This would require a ProductRepository to check if any products are assigned to this category

        CategoryDeleteResponse data = new CategoryDeleteResponse(category.getId(), category.getName());
        categoryRepository.deleteById(id);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                data,
                null
        );
    }

    @Override
    public GeneralResponse<CategoryResponse> createCategory(CategoryCreateRequest request) {
        return null;
    }

    @Override
    @Transactional
    public GeneralResponse<CategoryResponse> updateCategory(UUID id, CategoryUpdateRequest request) {
        // Find category
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.CATEGORY_NOT_FOUND));

        // Validation
        validateCategoryUpdateRequest(request);

        // Update fields
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            category.setName(request.getName().trim());
        }

        if (request.getSlug() != null && !request.getSlug().trim().isEmpty()) {
            // Validate slug uniqueness (excluding current category)
            if (categoryRepository.existsBySlugAndIdNot(request.getSlug().trim(), id)) {
                throw new ResException(ResErrorCode.CATEGORY_SLUG_ALREADY_EXISTS);
            }
            category.setSlug(request.getSlug().trim());
        }

        // Handle parent category changes
        if (request.getParentId() != null) {
            // Validate parent exists
            CategoryEntity parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResException(ResErrorCode.CATEGORY_PARENT_NOT_FOUND));

            // Prevent circular reference (category cannot be its own parent or descendant)
            if (parent.getId().equals(id) || isDescendant(parent, id)) {
                throw new ResException(ResErrorCode.CATEGORY_CIRCULAR_REFERENCE);
            }

            // Validate hierarchy depth (PC store usually doesn't need more than 3 levels)
            if (getHierarchyDepth(parent) >= 3) {
                throw new ResException(ResErrorCode.CATEGORY_MAX_DEPTH_EXCEEDED);
            }

            category.setParent(parent);
        } else if (request.isRemoveParent()) {
            category.setParent(null);
        }

        CategoryEntity updated = categoryRepository.save(category);
        CategoryResponse categoryResponse = convertToDTO(updated);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                categoryResponse,
                null
        );
    }

    @Override
    public GeneralResponse<Page<CategoryResponse>> searchCategories(String name, int page, int size) {
        // Clean parameters
        if (name != null) {
            name = name.trim();
            if (name.isEmpty()) {
                name = null;
            }
        }

        // Create Pageable with sorting by hierarchy (parent categories first, then by name)
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.asc("parent.name").nullsFirst(), Sort.Order.asc("name")));

        // Call repository với pagination
        Page<CategoryResponse> categories = categoryRepository.searchCategories(name, pageable);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                categories,
                null
        );
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getRootCategories() {
        return null;
    }

    @Override
    public GeneralResponse<CategoryResponse> getCategoryWithChildren(UUID id) {
        return null;
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getCategoryChildren(UUID id) {
        return null;
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getCategoryBreadcrumb(UUID id) {
        return null;
    }

    @Override
    public GeneralResponse<CategoryResponse> getCategoryBySlug(String slug) {
        return null;
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getCategoriesByType(String type, int page, int size) {
        return null;
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getLaptopCategories() {
        return null;
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getComponentCategories() {
        return null;
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getPeripheralCategories() {
        return null;
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getDesktopPCCategories() {
        return null;
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getStorageCategories() {
        return null;
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getCoolingCategories() {
        return null;
    }

    @Override
    public boolean existsById(UUID id) {
        return false;
    }

    @Override
    public boolean existsBySlug(String slug) {
        return false;
    }

    @Override
    public GeneralResponse<CategoryResponse> getCategoryById(UUID id) {
        return null;
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getAllCategories() {
        return null;
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getCategoriesByParentId(UUID parentId) {
        return null;
    }

    @Override
    public GeneralResponse<Long> getProductCountInCategory(UUID categoryId, boolean includeSubcategories) {
        return null;
    }

    private void validateCategoryUpdateRequest(CategoryUpdateRequest request) {
        if (request.getName() != null && request.getName().trim().length() < 2) {
            throw new ResException(ResErrorCode.CATEGORY_NAME_TOO_SHORT);
        }

        if (request.getSlug() != null && request.getSlug().trim().length() < 2) {
            throw new ResException(ResErrorCode.CATEGORY_SLUG_TOO_SHORT);
        }

        // Validate slug format (only lowercase letters, numbers, and hyphens)
        if (request.getSlug() != null && !request.getSlug().matches("^[a-z0-9-]+$")) {
            throw new ResException(ResErrorCode.CATEGORY_SLUG_INVALID_FORMAT);
        }
    }

    /**
     * Check if potential parent is a descendant of current category (prevent circular reference)
     */
    private boolean isDescendant(CategoryEntity potentialParent, UUID categoryId) {
        if (potentialParent.getParent() == null) {
            return false;
        }

        if (potentialParent.getParent().getId().equals(categoryId)) {
            return true;
        }

        return isDescendant(potentialParent.getParent(), categoryId);
    }

    /**
     * Get the depth of category hierarchy
     */
    private int getHierarchyDepth(CategoryEntity category) {
        int depth = 1;
        CategoryEntity current = category;

        while (current.getParent() != null) {
            depth++;
            current = current.getParent();
        }

        return depth;
    }

    private CategoryResponse convertToDTO(CategoryEntity category) {
        CategoryResponse.CategoryResponseBuilder builder = CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug());

        // Add parent info if exists
        if (category.getParent() != null) {
            builder.parentId(category.getParent().getId())
                    .parentName(category.getParent().getName())
                    .parentSlug(category.getParent().getSlug());
        }

        // Add children count and basic info
        if (category.getChildren() != null) {
            builder.childrenCount(category.getChildren().size())
                    .hasChildren(!category.getChildren().isEmpty());
        } else {
            builder.childrenCount(0)
                    .hasChildren(false);
        }

        // Add hierarchy level
        builder.hierarchyLevel(getHierarchyDepth(category));

        // Add category type based on parent structure (for PC store context)
        String categoryType = determineCategoryType(category);
        builder.categoryType(categoryType);

        return builder.build();
    }

    /**
     * Determine category type based on hierarchy for PC store
     */
    private String determineCategoryType(CategoryEntity category) {
        if (category.getParent() == null) {
            return "ROOT"; // Laptops, Desktop PCs, PC Components, Peripherals
        }

        // Get root category name
        CategoryEntity root = category;
        while (root.getParent() != null) {
            root = root.getParent();
        }

        String rootName = root.getName().toLowerCase();
        int hierarchyLevel = getHierarchyDepth(category);

        if (rootName.contains("laptop")) {
            return hierarchyLevel == 2 ? "LAPTOP_TYPE" : "LAPTOP_SUBTYPE";
        } else if (rootName.contains("desktop") || rootName.contains("pc")) {
            return hierarchyLevel == 2 ? "PC_CATEGORY" : "PC_TYPE";
        } else if (rootName.contains("component")) {
            return hierarchyLevel == 2 ? "COMPONENT_TYPE" : "COMPONENT_SUBTYPE";
        } else if (rootName.contains("peripheral")) {
            return hierarchyLevel == 2 ? "PERIPHERAL_TYPE" : "PERIPHERAL_SUBTYPE";
        }

        return "SUBCATEGORY";
    }
}