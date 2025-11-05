package com.proshop.product.dto.request;

import java.util.UUID;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductImageUpdateRequest {
  /** new image */
  private UUID id;

  /** new image */
  private MultipartFile file;
}