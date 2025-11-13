package com.proshop.review_service.dto.request;

import com.proshop.review_service.util.enums.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReviewStatusRequest {
    @NotNull(message = "Status không được để trống")
    private ReviewStatus status;

    private String rejectionReason; // Lý do từ chối (nếu status = REJECTED)
}