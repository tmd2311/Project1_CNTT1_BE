package com.proshop.product.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.*;

import java.util.UUID;
import org.hibernate.annotations.Type;


@Entity
@Table(name = "sku")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SKU {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(unique = true)
  private String skuCode;

  // variant JSONB: màu, RAM, SSD…
  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> specs;


  private Double price;
  private Double discountPrice;
  private Integer stock;
  private String barcode;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}

