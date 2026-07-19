package com.procurement.enterprise.dto.request;

import com.procurement.enterprise.enums.DeliveryStatus;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateDeliveryRequest {
    @Size(max = 100, message = "Delivery note number must not exceed 100 characters") private String deliveryNoteNumber;
    private LocalDate deliveryDate; private DeliveryStatus status;
    @Size(max = 100, message = "Carrier must not exceed 100 characters") private String carrier;
    @Size(max = 100, message = "Tracking number must not exceed 100 characters") private String trackingNumber;
}
