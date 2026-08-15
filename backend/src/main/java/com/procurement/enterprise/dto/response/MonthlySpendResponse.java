package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MonthlySpendResponse {
    private String month;
    private BigDecimal amount;
}
