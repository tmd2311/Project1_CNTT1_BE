package com.proshop.product.dto.response;

import java.util.UUID;
import lombok.Data;

@Data
public class BestSellerResponse {
  private UUID productId;
  private Long totalSold;
}
