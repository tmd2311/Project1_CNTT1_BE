package com.proshop.auth_lib.dto.response;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse<T> implements Serializable {

  private ResponseStatus status;
  private T data;

  public void setStatus(ResponseStatus status) {
    this.status = status;
  }

  public void setData(T data) {
    this.data = data;
  }
}