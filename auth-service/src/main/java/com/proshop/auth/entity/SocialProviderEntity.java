package com.proshop.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "social_provider")
public class SocialProviderEntity extends BaseEntity {

  @Column(name = "provider_name", length = 50)
  private String providerName;

  @Column(name = "provider_code", length = 100)
  private String providerCode;

  @Column(name = "base", length = 100)
  private String base;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity userEntity;
}
