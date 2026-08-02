package com.procurement.enterprise.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VendorRegisterResponse {

    private Long id;
    private String vendorName;
    private String contactName;
    private String email;
}
