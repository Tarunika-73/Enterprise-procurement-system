package com.procurement.enterprise.dto.response;

import com.procurement.enterprise.enums.DeliveryStatus;
import lombok.*;
import java.time.*;

@Getter @Builder
public class DeliveryResponse {
    private Long id; private Long purchaseOrderId; private String purchaseOrderNumber;
    private String deliveryNoteNumber; private LocalDate deliveryDate; private DeliveryStatus status;
    private String carrier; private String trackingNumber; private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
