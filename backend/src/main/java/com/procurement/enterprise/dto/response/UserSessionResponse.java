package com.procurement.enterprise.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Builder
public class UserSessionResponse {
    private Long id; private Long userId; private String sessionToken; private LocalDateTime expiresAt;
    private Boolean isActive; private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
