package com.proshop.auth.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class UpdateUserRequest {

  @Size(max = 100, message = "Full name must not exceed 100 characters")
  private String fullName;

  private LocalDate birthday;

  @Pattern(regexp = "^(\\+84|0)\\d{9,10}$", message = "Invalid phone number format")
  private String phone;

  @Size(max = 500, message = "Current address must not exceed 500 characters")
  private String currentAddress;
}
