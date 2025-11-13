package com.proshop.review_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseStatus {

    private String code;
    private String message;
    private String description;

    public static final ResponseStatus SUCCESS_STATUS =
            new ResponseStatus("200", "Thành công", "Success");
}