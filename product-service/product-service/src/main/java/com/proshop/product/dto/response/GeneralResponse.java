package com.proshop.product.dto.response;

import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GeneralResponse<T> implements Serializable {

  private ResponseStatus status;

  private T data;

  private Map<String, Object> extraData;

  public GeneralResponse() {
  }

  public GeneralResponse(ResponseStatus status) {
    this.status = status;
  }

  public GeneralResponse(ResponseStatus status, T data, Map<String, Object> extraData) {
    this.status = status;
    this.data = data;
    this.extraData = extraData;
  }


}
