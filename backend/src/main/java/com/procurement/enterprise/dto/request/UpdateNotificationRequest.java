package com.procurement.enterprise.dto.request;

import com.procurement.enterprise.enums.NotificationType;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateNotificationRequest {
    private NotificationType type;
    @Size(max = 255, message = "Subject must not exceed 255 characters") private String subject;
    private String message; private Boolean isRead;
}
