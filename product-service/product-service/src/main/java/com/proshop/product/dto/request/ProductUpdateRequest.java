package com.proshop.product.dto.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;
import java.util.Map;

@Data
public class ProductUpdateRequest {
    private String name;
    private String description;
    private UUID brandId;
    private UUID categoryId;
    private Map<String, Object> specs;
    private List<String> imageUrls;
}

