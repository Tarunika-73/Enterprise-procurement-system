package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/** Request DTO for updating an existing product. All fields are optional. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {

    @Size(max = 150, message = "Product name must not exceed 150 characters")
    private String name;

    private String description;

    private Long categoryId;

    private Long departmentId;

    private Boolean isActive;
}
