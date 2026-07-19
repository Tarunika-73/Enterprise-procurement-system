package com.procurement.enterprise.dto.response;

import lombok.*;
import java.time.*;

@Getter @Builder
public class ReceiptResponse {
    private Long id; private Long deliveryId; private Long receiverId; private String receiverName;
    private LocalDate receiptDate; private String conditionNotes; private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
