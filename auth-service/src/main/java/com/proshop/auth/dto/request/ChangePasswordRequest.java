package com.proshop.auth.dto.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {

  private String account;
  private String oldPassword;
  private String newPassword;
}
