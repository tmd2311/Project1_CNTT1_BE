package com.proshop.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
@Entity
@Table(name = "api")
public class ApiEntity extends BaseEntity {

  @Column(name = "code", length = 100, unique = true)
  private String code;

  @Column(name = "path", length = 255)
  private String path;

  @Column(name = "method", length = 10)
  private String method;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "rate_limit")
  private Integer rateLimit;

  @Column(name = "rate_limit_period")
  private Integer rateLimitPeriod;

  @Column(name = "status", length = 20)
  private String status;

  @ManyToMany(mappedBy = "apiEntities")
  private Set<PermissionEntity> permissionEntities = new HashSet<>();
}
