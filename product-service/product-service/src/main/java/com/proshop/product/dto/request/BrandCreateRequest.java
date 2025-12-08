package com.proshop.product.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandCreateRequest {
    private String name;
    private String slug;
}
