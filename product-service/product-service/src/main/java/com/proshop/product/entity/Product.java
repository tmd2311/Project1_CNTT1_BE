package com.proshop.product.entity;

import jakarta.persistence.*;
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
public class Product {

  @Id
  @GeneratedValue
  private UUID id;

  private String name;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne
  @JoinColumn(name = "brand_id")
  private Brand brand;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> specs;


  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
