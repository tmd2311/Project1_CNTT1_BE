package com.proshop.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "brand")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {

  @Id
  @GeneratedValue
  private UUID id;

  private String name;

  @Column(unique = true)
  private String slug;

  private String logoUrl;
}

