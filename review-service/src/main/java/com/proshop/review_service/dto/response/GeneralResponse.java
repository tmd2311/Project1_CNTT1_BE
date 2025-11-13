package com.proshop.review_service.dto.response;

import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneralResponse<T> implements Serializable {

    private ResponseStatus status;
    private T data;
    private Map<String, Object> extraData;

    public GeneralResponse() {}

    public GeneralResponse(ResponseStatus status, T data, Map<String, Object> extraData) {
        this.status = status;
        this.data = data;
        this.extraData = extraData;
    }
    }
