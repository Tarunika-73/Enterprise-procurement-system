package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

/** Request DTO for updating an existing role. All fields are optional. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRoleRequest {

    @Size(max = 50, message = "Role name must not exceed 50 characters")
    private String name;

    private String description;
}
