package com.proshop.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatsResponse {
    private Long totalCategories;
    private Long rootCategories;
    private Long maxDepth;
    private Long totalProducts;
    private Map<String, Long> categoriesByType; // LAPTOP_TYPE: 4, COMPONENT_TYPE: 8, etc.
    private Map<String, Long> productsByCategory; // category_id -> product_count
    private Long leafCategories; // Categories with no children
}