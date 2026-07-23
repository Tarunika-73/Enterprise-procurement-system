package com.procurement.enterprise.dto.approvalworkflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingApprovalResponse {

    private Long requestId;

    private String requestTitle;

    private String requestedBy;

    private Long approverId;

    private Integer approvalLevel;

    private String status;

    private LocalDateTime requestedDate;
}