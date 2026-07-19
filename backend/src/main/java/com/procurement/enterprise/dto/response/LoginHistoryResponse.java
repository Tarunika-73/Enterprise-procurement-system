package com.procurement.enterprise.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Builder
public class LoginHistoryResponse {
    private Long id; private Long userId; private String userName; private LocalDateTime loginTime;
    private LocalDateTime logoutTime; private String ipAddress; private String userAgent; private String status;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
