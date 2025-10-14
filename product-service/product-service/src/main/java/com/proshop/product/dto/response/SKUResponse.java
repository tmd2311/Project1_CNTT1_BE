// SKUResponse.java
package com.proshop.product.dto.response;

import lombok.*;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SKUResponse {
    private UUID id;
    private UUID productId;
    private String skuCode;
    private Map<String, Object> specs;
    private Double price;
    private Double discountPrice;
    private Integer stock;
    private String barcode;
    private Boolean isActive;
}
