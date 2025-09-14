package com.proshop.product.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.Map;
import lombok.*;

import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

  @Id
  @GeneratedValue
  private UUID id;

  private String name;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private CategoryEntity category;

  @ManyToOne
  @JoinColumn(name = "brand_id")
  private BrandEntity brand;

  @Column(columnDefinition = "TEXT")
  private String description;

  @JdbcTypeCode(SqlTypes.JSON)  // Thay đổi ở đây
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> specs;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<SKUEntity> skus;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ProductImageEntity> images;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}