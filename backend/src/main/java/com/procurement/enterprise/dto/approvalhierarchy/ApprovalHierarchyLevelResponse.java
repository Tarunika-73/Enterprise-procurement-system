package com.procurement.enterprise.dto.approvalhierarchy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalHierarchyLevelResponse {

    private Long id;

    private Integer levelNumber;

    private Long approverRoleId;

    private String approverRoleName;
}
