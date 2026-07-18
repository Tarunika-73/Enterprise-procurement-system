package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/** Request DTO for updating an existing user. All fields are optional. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    private Long roleId;

    private Long departmentId;

    private Boolean isActive;
}
