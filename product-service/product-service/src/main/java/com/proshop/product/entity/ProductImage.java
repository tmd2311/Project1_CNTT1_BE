package com.proshop.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "product_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  private String url;
  private Boolean isPrimary = false;
}

