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
public class WorkflowStatusResponse {

    private Long requestId;

    private String requestStatus;

    private Integer currentApprovalLevel;

    private Long currentApproverId;

    private String message;

    private LocalDateTime actionTime;
}