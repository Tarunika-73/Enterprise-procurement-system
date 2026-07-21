package com.procurement.enterprise.dto.request;

import com.procurement.enterprise.enums.ApprovalActionTaken;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for actioning an approval step (approve / reject / escalate).
 * Every action is recorded as an immutable {@code approval_history} entry.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalActionRequest {

    @NotNull(message = "Action-by user ID is required")
    private Long actionById;

    @NotNull(message = "Action taken is required")
    private ApprovalActionTaken actionTaken;

    private String comments;
}
