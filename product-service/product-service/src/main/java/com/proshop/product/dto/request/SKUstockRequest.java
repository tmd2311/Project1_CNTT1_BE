package com.proshop.product.dto.request;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SKUstockRequest {
    private UUID productId;
    private Integer stock;
}
