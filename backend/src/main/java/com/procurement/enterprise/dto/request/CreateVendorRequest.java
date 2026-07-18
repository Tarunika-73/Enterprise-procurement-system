package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/** Request DTO for creating a new vendor. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVendorRequest {

    @NotBlank(message = "Vendor name is required")
    @Size(max = 150, message = "Vendor name must not exceed 150 characters")
    private String vendorName;

    @Size(max = 100, message = "Contact name must not exceed 100 characters")
    private String contactName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    private String address;

    @Size(max = 50, message = "GST number must not exceed 50 characters")
    private String gstNumber;
}
