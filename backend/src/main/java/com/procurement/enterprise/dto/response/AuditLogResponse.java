package com.procurement.enterprise.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Builder
public class AuditLogResponse {
    private Long id; private Long userId; private String userName; private String action; private String tableName;
    private Long recordId; private String oldValue; private String newValue; private String ipAddress;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
