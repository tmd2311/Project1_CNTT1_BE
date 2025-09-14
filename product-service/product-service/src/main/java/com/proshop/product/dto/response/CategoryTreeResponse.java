package com.proshop.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeResponse {
    private UUID id;
    private String name;
    private String slug;
    private Integer level;
    private String categoryType;
    private Long productCount;
    private List<CategoryTreeResponse> children;
    private Boolean expanded = false; // For UI tree expansion state
}
