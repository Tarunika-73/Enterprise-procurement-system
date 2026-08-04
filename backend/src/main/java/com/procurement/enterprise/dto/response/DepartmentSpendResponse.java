package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Spend and request volume for a single department, used to render the
 * "Budget Utilization" style breakdown on the Reports page.
 */
@Getter
@Builder
public class DepartmentSpendResponse {

    private Long departmentId;
    private String departmentName;
    private long requestCount;
    private BigDecimal totalSpend;

    /**
     * Spend relative to the largest department in the returned set, 0-100.
     * Lets the frontend render a proportional bar without knowing every
     * other department's numbers.
     */
    private int relativePercent;
}
