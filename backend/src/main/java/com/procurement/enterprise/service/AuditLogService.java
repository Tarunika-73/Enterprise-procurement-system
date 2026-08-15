package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Records non-sensitive business events and exposes admin-only audit queries. */
public interface AuditLogService {
    void record(String action, String tableName, Long recordId, String oldValue, String newValue);
    Page<AuditLogResponse> getAll(Pageable pageable);
    Page<AuditLogResponse> getByUser(Long userId, Pageable pageable);
    Page<AuditLogResponse> getByTable(String tableName, Pageable pageable);
    Page<AuditLogResponse> getByRecord(String tableName, Long recordId, Pageable pageable);
}
