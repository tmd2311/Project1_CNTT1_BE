package com.proshop.auth.redis;

import java.util.Set;
import lombok.Data;

@Data
public class SystemPolicy {

  private String domain; // domain user
  private String role; // role user on domain
  private String permission; // permission on role
  private Set<String> apis; // api on permission
}
