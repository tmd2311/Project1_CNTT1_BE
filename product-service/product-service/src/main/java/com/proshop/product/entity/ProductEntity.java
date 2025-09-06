package com.proshop.product.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.Map;
import lombok.*;

import java.util.UUID;
import org.hibernate.annotations.Type;
import java.time.LocalDateTime;
import com.vladmihalcea.hibernate.type.json.JsonType;


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

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> specs;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<SKUEntity> skus;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ProductImageEntity> images;


  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
