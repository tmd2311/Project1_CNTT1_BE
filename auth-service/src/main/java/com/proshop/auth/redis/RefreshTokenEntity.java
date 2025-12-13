package com.proshop.auth.redis;

import static com.proshop.auth.utils.enums.UserStatus.ACTIVE;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenEntity {
  private String refreshToken;
  private String userCode;
  private List<String> roles;
  private Date expiryDate;
  private String status;
  private boolean deleted;

  public RefreshTokenEntity(String refreshToken, String userCode, List<String> roles, Date expiryDate) {
    this.refreshToken = refreshToken;
    this.userCode = userCode;
    this.roles = roles;
    this.expiryDate = expiryDate;
    this.status = ACTIVE.name();
    this.deleted = false;
  }
}
