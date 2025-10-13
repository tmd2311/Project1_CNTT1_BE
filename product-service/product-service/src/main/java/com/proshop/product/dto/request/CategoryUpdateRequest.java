package com.proshop.product.dto.request;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateRequest {
    private String name;
    private String slug;
    private UUID parentId;
    private boolean removeParent; // để remove parent category
    private List<CategoryImageRequest> images;
}
