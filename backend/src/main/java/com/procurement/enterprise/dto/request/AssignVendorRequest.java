package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignVendorRequest {

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;
}
