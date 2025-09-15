package com.proshop.product.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDeleteResponse {
    private UUID id;
    private String name;
    private String message;

    public CategoryDeleteResponse(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.message = "Đã xóa danh mục: " + name;
    }
}