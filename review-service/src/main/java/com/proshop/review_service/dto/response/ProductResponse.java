package com.proshop.review_service.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
}