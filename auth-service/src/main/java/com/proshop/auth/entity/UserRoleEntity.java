package com.proshop.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user_role")
public class UserRoleEntity extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity userEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id")
  private RoleEntity roleEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "domain_id")
  private DomainEntity domainEntity;

  @Column(name = "context_data", columnDefinition = "jsonb") // jsonb tốt hơn
  private String contextData;
}
