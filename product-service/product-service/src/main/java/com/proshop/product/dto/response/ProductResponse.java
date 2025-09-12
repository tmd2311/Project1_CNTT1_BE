package com.proshop.product.dto.response;


import java.util.Map;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
  private UUID id;
  private String name;
  private String description;
  private String brandName;
  private String categoryName;
  private Map<String, Object> specs;
  private Double price;        // lấy giá từ SKU rẻ nhất
  private String thumbnailUrl; // lấy ảnh chính
}
