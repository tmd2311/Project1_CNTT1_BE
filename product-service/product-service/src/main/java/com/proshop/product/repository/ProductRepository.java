package com.proshop.product.repository;

import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.entity.ProductEntity;


import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

  @Query("""
          SELECT new com.proshop.product.dto.response.ProductResponse(
              p.id, p.name, p.description,
              b.name, c.name,
              (SELECT MIN(s.price) FROM SKUEntity s WHERE s.product = p AND s.isActive = true),
              (SELECT i.url FROM ProductImageEntity i WHERE i.product = p AND i.isPrimary = true)
          )
          FROM ProductEntity p
          LEFT JOIN p.brand b
          LEFT JOIN p.category c
      """)
  Page<ProductResponse> findAllProductDTO(Pageable pageable);

  ProductEntity findProductById(UUID id);

  @Query("""
   SELECT new com.proshop.product.dto.response.ProductResponse(
       p.id,
       p.name,
       p.description,
       b.name,
       c.name,
       (SELECT MIN(s.price) FROM SKUEntity s WHERE s.product = p AND s.isActive = true),
       (SELECT i.url FROM ProductImageEntity i WHERE i.product = p AND i.isPrimary = true)
   )
   FROM ProductEntity p
   LEFT JOIN p.brand b
   LEFT JOIN p.category c
   WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
   AND (:minPrice IS NULL OR
        (SELECT MIN(s.price) FROM SKUEntity s WHERE s.product = p AND s.isActive = true) >= :minPrice)
   AND (:maxPrice IS NULL OR
        (SELECT MIN(s.price) FROM SKUEntity s WHERE s.product = p AND s.isActive = true) <= :maxPrice)
   """)
  Page<ProductResponse> searchProducts(@Param("name") String name,
                                       @Param("minPrice") Double minPrice,
                                       @Param("maxPrice") Double maxPrice,
                                       Pageable pageable);
}
