package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerDecisionRequest {

    @Size(max = 2000, message = "Remarks must not exceed 2000 characters")
    private String remarks;
}
