package com.procurement.enterprise.dto.request;

import com.procurement.enterprise.enums.PurchaseRequestStatus;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdatePurchaseRequestRequest {
    private Long departmentId;
    private String justification;
    private PurchaseRequestStatus status;
}
