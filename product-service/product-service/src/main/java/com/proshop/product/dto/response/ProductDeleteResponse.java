package com.proshop.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ProductDeleteResponse {
    private UUID id;
    private String name;
}
