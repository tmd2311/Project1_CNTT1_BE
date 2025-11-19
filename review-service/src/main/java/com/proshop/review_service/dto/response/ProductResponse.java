package com.proshop.review_service.dto.response;

import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
}