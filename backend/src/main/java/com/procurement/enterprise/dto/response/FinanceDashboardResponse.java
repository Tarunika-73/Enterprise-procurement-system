package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class FinanceDashboardResponse {

    private long pendingPayments;
    private long completedPayments;
    private BigDecimal pendingAmount;
    private BigDecimal totalAmountPaid;
}
