package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

/** Request DTO for updating an existing category. All fields are optional. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCategoryRequest {

    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    private String description;
}
