package com.proshop.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Entity
@Data
@Table(name = "permission")
public class PermissionEntity extends BaseEntity {

  @Column(name = "code", length = 100, unique = true)
  private String code;

  @Column(name = "name", length = 255)
  private String name;

  @Column(name = "action", length = 100)
  private String action;

  @Column(name = "resource", length = 100)
  private String resource;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @ManyToMany(mappedBy = "permissions")
  private Set<RoleEntity> roleEntities = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "permission_api",
      joinColumns = @JoinColumn(name = "permission_id"),
      inverseJoinColumns = @JoinColumn(name = "api_id")
  )
  private Set<ApiEntity> apiEntities = new HashSet<>();
}