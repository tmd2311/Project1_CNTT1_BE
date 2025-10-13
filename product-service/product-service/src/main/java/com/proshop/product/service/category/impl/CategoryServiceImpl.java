package com.proshop.product.service.category.impl;

import com.proshop.product.dto.request.CategoryCreateRequest;
import com.proshop.product.dto.request.CategoryImageRequest;
import com.proshop.product.dto.request.CategoryUpdateRequest;
import com.proshop.product.dto.response.CategoryDeleteResponse;
import com.proshop.product.dto.response.CategoryResponse;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.ResponseStatus;
import com.proshop.product.entity.CategoryEntity;
import com.proshop.product.entity.CategoryImageEntity;
import com.proshop.product.repository.CategoryImageRepository;
import com.proshop.product.repository.CategoryRepository;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.product.service.category.CategoryService;
import com.proshop.exceptionlib.enums.ResErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryImageRepository categoryImageRepository;

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

        // Check if category has products
        if (categoryRepository.hasProducts(id)) {
            return new GeneralResponse<>(
                    new ResponseStatus("400", "Không thể xóa danh mục đang có sản phẩm", "Cannot delete category with products"),
                    null,
                    null
            );
        }

        CategoryDeleteResponse data = new CategoryDeleteResponse(category.getId(), category.getName());
        categoryRepository.deleteById(id);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                data,
                null
        );
    }

    @Override
    @Transactional
    public GeneralResponse<CategoryResponse> createCategory(CategoryCreateRequest request) {
        // --- Validation cơ bản ---
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ResException(ResErrorCode.CATEGORY_NAME_REQUIRED);
        }

        if (request.getName().trim().length() < 2) {
            throw new ResException(ResErrorCode.CATEGORY_NAME_TOO_SHORT);
        }

        if (request.getSlug() != null && !request.getSlug().trim().isEmpty()) {
            if (categoryRepository.existsBySlug(request.getSlug().trim())) {
                throw new ResException(ResErrorCode.CATEGORY_SLUG_ALREADY_EXISTS);
            }

            if (!request.getSlug().matches("^[a-z0-9-]+$")) {
                throw new ResException(ResErrorCode.CATEGORY_SLUG_INVALID_FORMAT);
            }
        }

        // --- Tạo category ---
        CategoryEntity category = new CategoryEntity();
        category.setName(request.getName().trim());
        category.setSlug(request.getSlug() != null ? request.getSlug().trim() : generateSlugFromName(request.getName()));

        // --- Gán parent nếu có ---
        if (request.getParentId() != null) {
            CategoryEntity parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResException(ResErrorCode.CATEGORY_PARENT_NOT_FOUND));

            if (getHierarchyDepth(parent) >= 3) {
                throw new ResException(ResErrorCode.CATEGORY_MAX_DEPTH_EXCEEDED);
            }

            category.setParent(parent);
        }

        // --- Lưu category trước để có ID ---
        CategoryEntity saved = categoryRepository.save(category);

        // --- Thêm danh sách ảnh ---
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<CategoryImageEntity> imageEntities = request.getImageUrls().stream()
                    .map(url -> CategoryImageEntity.builder()
                            .url(url)
                            .isPrimary(request.getPrimaryImage() != null && request.getPrimaryImage().equals(url))
                            .category(saved)
                            .build())
                    .toList();

            // Lưu ảnh vào DB
            categoryImageRepository.saveAll(imageEntities);

            // Gán danh sách ảnh lại cho category (để trả về trong response)
            saved.setImages(imageEntities);
        }

        CategoryResponse response = convertToDTO(saved);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                response,
                null
        );
    }

    @Override
    @Transactional
    public GeneralResponse<CategoryResponse> updateCategory(UUID id, CategoryUpdateRequest request) {
        // 🔹 1. Tìm category cần cập nhật
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.CATEGORY_NOT_FOUND));

        // 🔹 2. Validate request
        validateCategoryUpdateRequest(request);

        // 🔹 3. Cập nhật các field cơ bản
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            category.setName(request.getName().trim());
        }

        if (request.getSlug() != null && !request.getSlug().trim().isEmpty()) {
            String slug = request.getSlug().trim();
            // Check slug trùng (ngoại trừ chính nó)
            if (categoryRepository.existsBySlugAndIdNot(slug, id)) {
                throw new ResException(ResErrorCode.CATEGORY_SLUG_ALREADY_EXISTS);
            }
            category.setSlug(slug);
        }

        // 🔹 4. Xử lý parent category
        if (request.getParentId() != null) {
            CategoryEntity parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResException(ResErrorCode.CATEGORY_PARENT_NOT_FOUND));

            // Không được tự làm parent chính mình
            if (parent.getId().equals(id) || isDescendant(parent, id)) {
                throw new ResException(ResErrorCode.CATEGORY_CIRCULAR_REFERENCE);
            }

            // Giới hạn độ sâu phân cấp
            if (getHierarchyDepth(parent) >= 3) {
                throw new ResException(ResErrorCode.CATEGORY_MAX_DEPTH_EXCEEDED);
            }

            category.setParent(parent);
        } else if (request.isRemoveParent()) {
            category.setParent(null);
        }

        // 🔹 5. Cập nhật danh sách ảnh (nếu có)
        if (request.getImages() != null) {
            List<CategoryImageRequest> imageRequests = request.getImages();

            // Lấy danh sách ảnh hiện có
            List<CategoryImageEntity> existingImages = category.getImages();
            if (existingImages == null) existingImages = new ArrayList<>();

            // 5.1 Xóa ảnh không còn trong request
            List<UUID> newIds = imageRequests.stream()
                    .map(CategoryImageRequest::getId)
                    .filter(Objects::nonNull)
                    .toList();

            existingImages.removeIf(img -> img.getId() != null && !newIds.contains(img.getId()));

            // 5.2 Cập nhật hoặc thêm ảnh mới
            for (CategoryImageRequest imgReq : imageRequests) {
                if (imgReq.getUrl() == null || imgReq.getUrl().trim().isEmpty()) continue;

                CategoryImageEntity image;
                if (imgReq.getId() != null) {
                    // Cập nhật ảnh cũ
                    image = existingImages.stream()
                            .filter(i -> i.getId().equals(imgReq.getId()))
                            .findFirst()
                            .orElseThrow(() -> new ResException(ResErrorCode.CATEGORY_IMAGE_NOT_FOUND));

                    image.setUrl(imgReq.getUrl().trim());
                    image.setIsPrimary(imgReq.getIsPrimary() != null && imgReq.getIsPrimary());
                } else {
                    // Thêm ảnh mới
                    image = CategoryImageEntity.builder()
                            .url(imgReq.getUrl().trim())
                            .isPrimary(imgReq.getIsPrimary() != null && imgReq.getIsPrimary())
                            .category(category)
                            .build();
                    existingImages.add(image);
                }
            }

            // 5.3 Chỉ cho phép 1 ảnh chính
            long primaryCount = existingImages.stream()
                    .filter(CategoryImageEntity::getIsPrimary)
                    .count();
            if (primaryCount > 1) {
                throw new ResException(ResErrorCode.CATEGORY_MULTIPLE_PRIMARY_IMAGES);
            }

            category.setImages(existingImages);
        }

        // 🔹 6. Lưu và trả kết quả
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
        List<CategoryEntity> rootCategories = categoryRepository.findByParentIsNull(
                Sort.by(Sort.Direction.ASC, "name"));

        List<CategoryResponse> responses = rootCategories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                responses,
                null
        );
    }

    @Override
    public GeneralResponse<CategoryResponse> getCategoryWithChildren(UUID id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.CATEGORY_NOT_FOUND));

        CategoryResponse response = convertToDTOWithChildren(category);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                response,
                null
        );
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getCategoryChildren(UUID id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.CATEGORY_NOT_FOUND));

        List<CategoryResponse> children = category.getChildren().stream()
                .map(this::convertToDTO)
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .collect(Collectors.toList());

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                children,
                null
        );
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getCategoryBreadcrumb(UUID id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.CATEGORY_NOT_FOUND));

        List<CategoryResponse> breadcrumb = new ArrayList<>();
        CategoryEntity current = category;

        // Build breadcrumb from current to root
        while (current != null) {
            breadcrumb.add(0, convertToDTO(current)); // Add to beginning
            current = current.getParent();
        }

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                breadcrumb,
                null
        );
    }

    @Override
    public GeneralResponse<CategoryResponse> getCategoryBySlug(String slug) {
        CategoryEntity category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResException(ResErrorCode.CATEGORY_NOT_FOUND));

        CategoryResponse response = convertToDTO(category);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                response,
                null
        );
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getCategoriesByType(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));

        List<CategoryEntity> categories;
        switch (type.toUpperCase()) {
            case "ROOT":
                categories = categoryRepository.findByParentIsNull(Sort.by(Sort.Direction.ASC, "name"));
                break;
            case "LAPTOP":
                categories = categoryRepository.findCategoriesByRootName("laptop", pageable).getContent();
                break;
            case "COMPONENT":
                categories = categoryRepository.findCategoriesByRootName("component", pageable).getContent();
                break;
            default:
                categories = categoryRepository.findAll(pageable).getContent();
        }

        List<CategoryResponse> responses = categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                responses,
                null
        );
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getLaptopCategories() {
        List<CategoryEntity> laptopCategories = categoryRepository.findCategoriesByRootName("laptop");

        List<CategoryResponse> responses = laptopCategories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                responses,
                null
        );
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getComponentCategories() {
        List<CategoryEntity> componentCategories = categoryRepository.findCategoriesByRootName("component");

        List<CategoryResponse> responses = componentCategories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                responses,
                null
        );
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getPeripheralCategories() {
        List<CategoryEntity> peripheralCategories = categoryRepository.findCategoriesByRootName("peripheral");

        List<CategoryResponse> responses = peripheralCategories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                responses,
                null
        );
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getDesktopPCCategories() {
        List<CategoryEntity> desktopCategories = categoryRepository.findCategoriesByRootName("desktop");

        List<CategoryResponse> responses = desktopCategories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                responses,
                null
        );
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getStorageCategories() {
        List<CategoryEntity> storageCategories = categoryRepository.findCategoriesByRootName("storage");

        List<CategoryResponse> responses = storageCategories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                responses,
                null
        );
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getCoolingCategories() {
        List<CategoryEntity> coolingCategories = categoryRepository.findCategoriesByRootName("cooling");

        List<CategoryResponse> responses = coolingCategories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                responses,
                null
        );
    }

    @Override
    public boolean existsById(UUID id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return categoryRepository.existsBySlug(slug);
    }

    @Override
    public GeneralResponse<CategoryResponse> getCategoryById(UUID id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.CATEGORY_NOT_FOUND));


        CategoryResponse response = convertToDTO(category);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                response,
                null
        );
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getAllCategories() {
        List<CategoryEntity> categories = categoryRepository.findAll(
                Sort.by(Sort.Order.asc("parent.name").nullsFirst(), Sort.Order.asc("name")));

        List<CategoryResponse> responses = categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                responses,
                null
        );
    }

    @Override
    public GeneralResponse<List<CategoryResponse>> getCategoriesByParentId(UUID parentId) {
        List<CategoryEntity> categories = categoryRepository.findByParentId(parentId,
                Sort.by(Sort.Direction.ASC, "name"));

        List<CategoryResponse> responses = categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                responses,
                null
        );
    }

    @Override
    public GeneralResponse<Long> getProductCountInCategory(UUID categoryId, boolean includeSubcategories) {
        // Validate category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResException(ResErrorCode.CATEGORY_NOT_FOUND);
        }

        Long count;
        if (includeSubcategories) {
            // Count products in category and all its subcategories
            count = categoryRepository.countProductsInCategoryTree(categoryId);
        } else {
            // Count products only in this specific category
            count = categoryRepository.countProductsInCategory(categoryId);
        }

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                count,
                null
        );
    }

    // Helper methods
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

    private boolean isDescendant(CategoryEntity potentialParent, UUID categoryId) {
        if (potentialParent.getParent() == null) {
            return false;
        }

        if (potentialParent.getParent().getId().equals(categoryId)) {
            return true;
        }

        return isDescendant(potentialParent.getParent(), categoryId);
    }

    private int getHierarchyDepth(CategoryEntity category) {
        int depth = 1;
        CategoryEntity current = category;

        while (current.getParent() != null) {
            depth++;
            current = current.getParent();
        }

        return depth;
    }

    private String generateSlugFromName(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
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

    private CategoryResponse convertToDTOWithChildren(CategoryEntity category) {
        CategoryResponse response = convertToDTO(category);

        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            List<CategoryResponse> children = category.getChildren().stream()
                    .map(this::convertToDTO)
                    .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                    .collect(Collectors.toList());

            // Assuming CategoryResponse has a children field
            // response.setChildren(children);
        }

        return response;
    }

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