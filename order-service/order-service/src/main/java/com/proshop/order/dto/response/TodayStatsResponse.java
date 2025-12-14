package com.proshop.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodayStatsResponse {
    private Integer orderCount;
    private BigDecimal revenue;
    private Map<String, Long> ordersByStatus;
}
