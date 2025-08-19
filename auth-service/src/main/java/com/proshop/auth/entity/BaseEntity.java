package com.proshop.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.security.core.context.SecurityContextHolder;

@Data
@MappedSuperclass
public class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "created_date")
  private LocalDateTime createdDate;
  @Column(name = "created_by")
  private String createdBy;
  @Column(name = "modified_date")
  private LocalDateTime modifiedDate;
  @Column(name = "modified_by")
  private String modifiedBy;
  @Column(name = "deleted", columnDefinition = "tinyint(1) default 0")
  private Boolean deleted;
  @PrePersist
  protected void onCreate() {
    this.createdDate = LocalDateTime.now();
    this.modifiedDate = LocalDateTime.now();
    this.createdBy = getCurrentUserName();
    this.modifiedBy = getCurrentUserName();
  }
  @PreUpdate
  protected void onUpdate() {
    this.modifiedDate = LocalDateTime.now();
    this.modifiedBy = getCurrentUserName();
  }


  public String getCurrentUserName() {
    var auth  = SecurityContextHolder.getContext().getAuthentication();
    return (auth != null && auth.isAuthenticated()) ? auth.getName() : "system";
  }
}
