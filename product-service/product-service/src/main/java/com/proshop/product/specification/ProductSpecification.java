package com.proshop.product.specification;

import com.proshop.product.entity.ProductEntity;
import com.proshop.product.entity.SKUEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

  public static Specification<ProductEntity> hasName(String name) {
    return ((root, query, criteriaBuilder) ->
        name == null ? criteriaBuilder.conjunction() :
            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                "%" + name.toLowerCase() + "%"));
  }

  public static Specification<ProductEntity> hasBrand(String brand) {
    return ((root, query, criteriaBuilder) ->
        brand == null ? criteriaBuilder.conjunction()
            : criteriaBuilder.like(criteriaBuilder.lower(root.get("brand").get("name")),
                "%" + brand.toLowerCase() + "%"));
  }

  public static Specification<ProductEntity> hasCategory(String category) {
    return ((root, query, criteriaBuilder) ->
        category == null ? criteriaBuilder.conjunction()
            : criteriaBuilder.like(criteriaBuilder.lower(root.get("category").get("name")),
                "%" + category.toLowerCase() + "%"));
  }

  public static Specification<ProductEntity> priceBetween(Double minPrice, Double maxPrice) {
    return (root, query, criteriaBuilder) -> {
      if (minPrice == null && maxPrice == null) {
        return criteriaBuilder.conjunction();
      }
      Join<ProductEntity, SKUEntity> skuJoin = root.join("skus", JoinType.LEFT);
      if (minPrice != null && maxPrice != null) {
        return criteriaBuilder.between(skuJoin.get("price"), minPrice, maxPrice);
      } else if (minPrice != null) {
        return criteriaBuilder.greaterThanOrEqualTo(skuJoin.get("price"), minPrice);
      } else {
        return criteriaBuilder.lessThanOrEqualTo(skuJoin.get("price"), maxPrice);
      }
    };
  }

}
