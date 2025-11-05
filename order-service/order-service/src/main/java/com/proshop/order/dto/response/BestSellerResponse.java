package com.proshop.order.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BestSellerResponse {
  private UUID productId;
  private Long totalSold;
}
