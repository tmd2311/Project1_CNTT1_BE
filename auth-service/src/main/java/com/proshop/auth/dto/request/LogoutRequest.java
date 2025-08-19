package com.proshop.auth.dto.request;

import lombok.Data;

@Data
public class LogoutRequest {

  private String code;
  private String token;

}
