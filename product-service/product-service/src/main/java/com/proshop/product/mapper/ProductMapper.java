package com.proshop.product.mapper;

import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.entity.ProductEntity;
import com.proshop.product.entity.ProductImageEntity;
import com.proshop.product.entity.SKUEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  // Chỉ map các field trực tiếp
  ProductResponse toDTO(ProductEntity entity);

  ProductEntity toEntity(ProductResponse dto);

  @AfterMapping
  default void fillExtraFields(ProductEntity entity, @MappingTarget ProductResponse dto) {
    // brandName
    if (entity.getBrand() != null) {
      dto.setBrandName(entity.getBrand().getName());
    }

    // categoryName
    if (entity.getCategory() != null) {
      dto.setCategoryName(entity.getCategory().getName());
    }

    // min price từ SKU
    if (entity.getSkus() != null && !entity.getSkus().isEmpty()) {
      dto.setPrice(entity.getSkus().stream()
          .filter(SKUEntity::getIsActive)
          .map(SKUEntity::getPrice)
          .min(Double::compareTo)
          .orElse(null));
    }

    // thumbnail chính
    if (entity.getImages() != null && !entity.getImages().isEmpty()) {
      dto.setThumbnailUrl(entity.getImages().stream()
          .filter(ProductImageEntity::getIsPrimary)
          .findFirst()
          .map(ProductImageEntity::getUrl)
          .orElse(entity.getImages().get(0).getUrl()));
    }
  }
}
