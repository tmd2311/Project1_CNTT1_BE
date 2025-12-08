package com.proshop.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSKUSalePriceRequest {
    private Double originalPrice;
    private Double salePrice;
    private Long saleId;
}
