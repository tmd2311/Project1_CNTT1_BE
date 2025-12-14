package com.proshop.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenueSummaryResponse {
    private BigDecimal currentRevenue;
    private BigDecimal previousRevenue;
    private Double percentChange;
    private Long orderCount;
    private String comparedTo;
}
