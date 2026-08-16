package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReportTableRow {
    private String reference;
    private String title;
    private String status;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
