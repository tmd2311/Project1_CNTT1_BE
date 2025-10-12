package com.proshop.order.dto.response;

import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    private String brandName;
    private String categoryName;
    private Map<String, Object> specs;
    private Double price;        // lấy giá từ SKU rẻ nhất
    private String thumbnailUrl; // lấy ảnh chính
}