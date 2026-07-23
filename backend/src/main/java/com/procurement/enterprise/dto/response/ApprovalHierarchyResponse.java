package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApprovalHierarchyResponse {
    private Long id;
    private Long departmentId;
    private String departmentName;
    private Integer level;
    private Long approverId;
    private String approverName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
