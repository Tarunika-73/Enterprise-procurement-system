package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreatePurchaseRequestRequest {
    @NotNull(message = "Requester ID is required") private Long requesterId;
    @NotNull(message = "Department ID is required") private Long departmentId;
    @NotBlank(message = "Justification is required") private String justification;
}
