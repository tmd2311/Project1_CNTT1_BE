package com.proshop.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

  @NotBlank(message = "Account is required")
  @Size(min = 4, max = 50, message = "Account must be between 4 and 50 characters")
  @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Account can only contain letters, numbers, dots, underscores and dashes")
  private String account;

  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
      message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character"
  )
  private String password;

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;

  @NotBlank(message = "Full name is required")
  @Size(max = 100, message = "Full name must not exceed 100 characters")
  private String fullName;

  @Pattern(regexp = "^(\\+84|0)[0-9]{9,10}$", message = "Invalid phone number format")
  private String phone;
}
