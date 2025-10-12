package com.proshop.product.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.*;

import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


@Entity
@Table(name = "sku")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SKUEntity {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private ProductEntity product;

  @Column(unique = true)
  private String skuCode;

  // variant JSONB: màu, RAM, SSD…
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> specs;


  private Double price;
  private Double discountPrice;
  private Integer stock;
  private String barcode;
  private Boolean isActive = true;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}

