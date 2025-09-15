package com.proshop.product.dto.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private UUID id;
    private String name;
    private String slug;
    private UUID parentId;
    private String parentName;
    private String parentSlug;
    private Integer childrenCount;
    private Boolean hasChildren;
    private List<CategoryResponse> children; // For nested category structure
    private Integer hierarchyLevel;
    private String categoryType; // ROOT, LAPTOP_TYPE, COMPONENT_TYPE, etc.
    private Long productCount; // Number of products in this category
    private Boolean isActive; // Category status
    // Constructor for JPQL SELECT NEW - khớp với kiểu dữ liệu JPQL trả về
    public CategoryResponse(UUID id,
                            String name,
                            String slug,
                            UUID parentId,
                            String parentName,
                            String parentSlug,
                            int childrenCount,        // JPQL SIZE() trả về int
                            boolean hasChildren,      // JPQL CASE WHEN trả về boolean
                            int hierarchyLevel,       // JPQL CASE WHEN trả về int
                            String categoryType,
                            long productCount,        // 0L là long
                            boolean isActive) {       // true là boolean
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.parentId = parentId;
        this.parentName = parentName;
        this.parentSlug = parentSlug;
        this.childrenCount = childrenCount;
        this.hasChildren = hasChildren;
        this.hierarchyLevel = hierarchyLevel;
        this.categoryType = categoryType;
        this.productCount = productCount;
        this.isActive = isActive;
    }

    // Simplified constructor for basic category info (commonly used)
    public CategoryResponse(UUID id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.childrenCount = 0;
        this.hasChildren = false;
        this.hierarchyLevel = 1;
        this.isActive = true;
    }

    // Constructor for category with parent info
    public CategoryResponse(UUID id, String name, String slug,
                            UUID parentId, String parentName, String parentSlug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.parentId = parentId;
        this.parentName = parentName;
        this.parentSlug = parentSlug;
        this.childrenCount = 0;
        this.hasChildren = false;
        this.isActive = true;
    }

    // Utility methods
    public boolean isRootCategory() {
        return parentId == null;
    }

    public boolean isLeafCategory() {
        return childrenCount == null || childrenCount == 0;
    }

    public String getDisplayName() {
        return name != null ? name : "Unknown Category";
    }

    public String getFullPath() {
        if (parentName != null) {
            return parentName + " > " + name;
        }
        return name;
    }
}