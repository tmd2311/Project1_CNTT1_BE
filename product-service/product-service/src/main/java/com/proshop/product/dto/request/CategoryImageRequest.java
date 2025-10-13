package com.proshop.product.dto.request;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryImageRequest {
    private UUID id;           // null nếu là ảnh mới
    private String url;
    private Boolean isPrimary; // true nếu là ảnh chính
}
