package com.procurement.enterprise.dto.request;

import com.procurement.enterprise.enums.NotificationType;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateNotificationRequest {
    @NotNull(message = "User ID is required") private Long userId;
    @NotNull(message = "Notification type is required") private NotificationType type;
    @NotBlank(message = "Subject is required") @Size(max = 255, message = "Subject must not exceed 255 characters") private String subject;
    @NotBlank(message = "Message is required") private String message;
}
