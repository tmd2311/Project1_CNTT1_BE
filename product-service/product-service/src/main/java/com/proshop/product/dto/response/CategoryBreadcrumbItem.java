package com.proshop.product.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryBreadcrumbItem {
    private UUID id;
    private String name;
    private String slug;
    private Integer level;
    private Boolean isLast; // True if this is the current/last item in breadcrumb
    private String url; // Full URL path for this category
}