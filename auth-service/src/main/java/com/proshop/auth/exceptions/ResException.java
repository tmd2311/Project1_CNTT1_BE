package com.proshop.auth.exceptions;

import com.proshop.auth.utils.enums.ResErrorCode;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ResException extends RuntimeException {

  private String code;

  private String message;

  private HttpStatus status;

  private String label;

  private Object data;

  public ResException() {
    super();
  }

  public ResException(ResErrorCode code) {
    super();
    this.code = code.code();
    this.message = code.message();
    this.status = code.status();
    this.label = code.label();
  }

  public ResException(ResErrorCode code, Map<?, ?> data) {
    super();
    this.code = code.code();
    this.message = code.message();
    this.status = code.status();
    this.data = data;
    this.label = code.label();
  }

  public ResException(ResErrorCode code, HttpStatus status, Map<?, ?> data) {
    super();
    this.code = code.code();
    this.message = code.message();
    this.status = status;
    this.data = data;
    this.label = code.label();
  }


  public ResException(ResErrorCode code, String message) {
    super();
    this.code = code.code();
    this.message = message;
    this.status = code.status();
    this.label = code.label();
  }

  public ResException(ResErrorCode code, String message, String label) {
    super();
    this.code = code.code();
    this.message = message;
    this.status = code.status();
    this.label = label;
  }

  public ResException(ResErrorCode code, String... errors) {
    super();
    this.code = code.code();
    this.message = code.message();
    this.status = code.status();
    this.label = code.label();

    if (errors != null) {
      Map<String, String> map = new HashMap<String, String>();
      for (int i = 0; i < errors.length / 2; i++) {
        map.put(errors[i * 2], errors[i * 2 + 1]);
      }
      this.data = map;
    }
  }

}
