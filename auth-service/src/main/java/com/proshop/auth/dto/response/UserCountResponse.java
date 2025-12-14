package com.proshop.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCountResponse {
    private Long totalUsers;
    private Long activeUsers;
    private Long inactiveUsers;
}
