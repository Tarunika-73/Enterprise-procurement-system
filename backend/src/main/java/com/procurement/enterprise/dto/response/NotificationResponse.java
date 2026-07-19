package com.procurement.enterprise.dto.response;

import com.procurement.enterprise.enums.NotificationType;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Builder
public class NotificationResponse {
    private Long id; private Long userId; private NotificationType type; private String subject;
    private String message; private Boolean isRead; private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
