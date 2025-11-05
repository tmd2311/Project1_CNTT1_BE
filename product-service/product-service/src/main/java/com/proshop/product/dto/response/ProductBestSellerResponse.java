package com.proshop.product.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductBestSellerResponse {
  private String id;
  private String name;
  private Double price;
  private String imageUrl;
  private Long totalSold;
}

