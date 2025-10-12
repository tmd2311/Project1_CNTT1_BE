package com.proshop.product.dto.request;

import lombok.*;
import java.util.UUID;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateRequest {
    private String name;
    private String description;
    private Map<String, Object> specs; // nếu bạn dùng JSON
    private UUID brandId;
    private UUID categoryId;
}