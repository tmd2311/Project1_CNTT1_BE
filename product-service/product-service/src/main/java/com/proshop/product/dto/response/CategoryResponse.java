package com.proshop.product.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CategoryResponse {
    private UUID id;
    private String name;
    private String slug;
    private UUID parentId;
    private String parentName;
    private String parentSlug;
    private Integer childrenCount;
    private Boolean hasChildren;
    private Integer hierarchyLevel;
    private String categoryType; // ROOT, LAPTOP_TYPE, COMPONENT_TYPE, etc.
}
