package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VendorResponse {
    private Long id;
    private String vendorName;
    private String contactName;
    private String email;
    private String phone;
    private String address;
    private String gstNumber;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
