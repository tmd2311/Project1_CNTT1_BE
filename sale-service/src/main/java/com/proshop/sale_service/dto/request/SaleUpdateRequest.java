package com.proshop.sale_service.dto.request;

import com.proshop.sale_service.util.enums.SaleType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleUpdateRequest {

    @Size(max = 100, message = "Code không được vượt quá 100 ký tự")
    private String code;

    @Size(max = 200, message = "Name không được vượt quá 200 ký tự")
    private String name;

    private String description;

    private SaleType saleType;

    @DecimalMin(value = "0.00", message = "Sale value phải lớn hơn hoặc bằng 0")
    private BigDecimal saleValue;

    @DecimalMin(value = "0.00", message = "Min order value phải lớn hơn hoặc bằng 0")
    private BigDecimal minOrderValue;

    @DecimalMin(value = "0.00", message = "Max discount amount phải lớn hơn hoặc bằng 0")
    private BigDecimal maxDiscountAmount;

    @Min(value = 0, message = "Quantity phải lớn hơn hoặc bằng 0")
    private Integer quantity;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Boolean isActive;
}