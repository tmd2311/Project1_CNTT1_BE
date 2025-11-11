package com.proshop.auth.redis;

import static com.proshop.auth.utils.enums.UserStatus.ACTIVE;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenEntity {
  private  String token;
  private List<String> roles;
  private String status;
  private boolean deleted;

  public TokenEntity() {
    this.status = ACTIVE.name();
    this.deleted = false;
  }

}
