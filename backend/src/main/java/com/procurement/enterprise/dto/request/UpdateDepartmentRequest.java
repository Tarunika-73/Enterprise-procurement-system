package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

/** Request DTO for updating an existing department. All fields are optional. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDepartmentRequest {

    @Size(max = 100, message = "Department name must not exceed 100 characters")
    private String name;

    @Size(max = 20, message = "Department code must not exceed 20 characters")
    private String code;

    private Long managerId;
}
