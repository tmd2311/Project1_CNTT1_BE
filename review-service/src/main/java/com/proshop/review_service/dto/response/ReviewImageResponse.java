package com.proshop.review_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewImageResponse {

    private Long id;
    private String imageUrl;
    private Integer displayOrder;
}