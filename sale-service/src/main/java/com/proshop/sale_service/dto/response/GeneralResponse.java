package com.proshop.sale_service.dto.response;

import com.proshop.exceptionlib.dto.response.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralResponse<T> {

    private ResponseStatus status;
    private T data;
    private Object error;

    public GeneralResponse(ResponseStatus status, T data) {
        this.status = status;
        this.data = data;
        this.error = null;
    }
}
