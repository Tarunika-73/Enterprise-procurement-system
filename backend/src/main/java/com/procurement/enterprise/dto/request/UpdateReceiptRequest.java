package com.procurement.enterprise.dto.request;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateReceiptRequest {
    private Long receiverId; private LocalDate receiptDate; private String conditionNotes;
}
