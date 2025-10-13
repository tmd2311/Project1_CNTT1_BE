package com.proshop.product.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageRequest {
    private UUID Id;
    private String url;
    private Boolean isPrimary;
}
