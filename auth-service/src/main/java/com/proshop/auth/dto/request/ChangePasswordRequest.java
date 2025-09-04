package com.proshop.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {

  @NotBlank(message = "Account is required")
  private String account;

  @NotBlank(message = "Old Password is required")
  private String oldPassword;

  @NotBlank(message = "New Password is required")
  private String newPassword;
}
