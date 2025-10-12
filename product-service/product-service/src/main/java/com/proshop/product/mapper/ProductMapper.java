package com.proshop.product.mapper;

import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  ProductResponse toDTO(ProductEntity entity);

  ProductEntity toEntity(ProductResponse dto);
}
