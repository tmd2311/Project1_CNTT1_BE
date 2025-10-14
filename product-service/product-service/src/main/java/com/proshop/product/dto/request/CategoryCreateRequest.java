package com.proshop.product.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Valid
public class CategoryCreateRequest {
    @NotBlank
    private String name;
    @NotBlank private String slug;
    private UUID parentId;
    private CategoryImageRequest image;
    private String primaryImage; // tùy chọn — đường dẫn ảnh chính

}
