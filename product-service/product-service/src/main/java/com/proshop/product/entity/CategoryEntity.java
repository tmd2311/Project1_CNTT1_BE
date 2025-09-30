package com.proshop.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryEntity {

  @Id
  @GeneratedValue
  private UUID id;

  private String name;

  @Column(unique = true)
  private String slug;

  @ManyToOne
  @JoinColumn(name = "parent_id")
  private CategoryEntity parent;

  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
  private List<CategoryEntity> children;

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CategoryImageEntity> images;
}
