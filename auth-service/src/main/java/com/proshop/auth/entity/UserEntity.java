package com.proshop.auth.entity;


import com.proshop.auth.utils.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity extends BaseEntity implements UserDetails {

  @Column(name = "code", nullable = false, unique = true, length = 100)
  private String code;
  @Column(name = "account", nullable = false, unique = true, length = 100)
  private String account;
  @Column(name = "username", length = 100)
  private String username;
  @Column(name = "email", length = 255)
  private String email;
  @Column(name = "full_name")
  private String fullName;
  @Column(name = "password_hash", length = 255)
  private String passwordHash;
  @Column(name = "phone", length = 20)
  private String phone;
  @Column(name = "avatar_url", length = 255)
  private String avatarUrl;
  @Column(name = "current_address", columnDefinition = "TEXT")
  private String currentAddress;
  @Column(name = "last_login")
  private LocalDateTime lastLogin;
  @Column(name = "status", length = 20)
  private String status = UserStatus.ACTIVE.name();

  @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
  private List<UserRoleEntity> userRoles = new ArrayList<>();


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> authorities = new ArrayList<>();
    for (UserRoleEntity userRole : userRoles) {
      RoleEntity role = userRole.getRoleEntity();
      authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
      role.getPermissions().forEach(permission ->
          authorities.add(new SimpleGrantedAuthority(permission.getCode()))
      );
    }
    return authorities;
  }

  @Override
  public String getPassword() {
    return this.passwordHash;
  }

  @Override
  public String getUsername() { return  this.username; }

  @Override
  public boolean isAccountNonExpired() {
    return status.equals(UserStatus.ACTIVE.name());
  }

  @Override
  public boolean isAccountNonLocked() {
    return UserDetails.super.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }
}
