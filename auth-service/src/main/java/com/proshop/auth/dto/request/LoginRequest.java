package com.proshop.auth.dto.request;

import lombok.Data;

@Data
public class LoginRequest {

  String account;
  String password;

}
