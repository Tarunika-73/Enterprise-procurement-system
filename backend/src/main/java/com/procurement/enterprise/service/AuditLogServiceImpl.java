package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.AuditLogResponse;
import com.procurement.enterprise.entity.AuditLog;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.repository.AuditLogRepository;
import com.procurement.enterprise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void record(String action, String tableName, Long recordId, String oldValue, String newValue) {
        AuditLog log = AuditLog.builder().user(currentInternalUser()).action(action).tableName(tableName)
                .recordId(recordId).oldValue(oldValue).newValue(newValue).build();
        auditLogRepository.save(log);
    }

    @Override @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAll(Pageable pageable) { return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::map); }
    @Override @Transactional(readOnly = true)
    public Page<AuditLogResponse> getByUser(Long userId, Pageable pageable) { return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable).map(this::map); }
    @Override @Transactional(readOnly = true)
    public Page<AuditLogResponse> getByTable(String tableName, Pageable pageable) { return auditLogRepository.findByTableNameOrderByCreatedAtDesc(tableName, pageable).map(this::map); }
    @Override @Transactional(readOnly = true)
    public Page<AuditLogResponse> getByRecord(String tableName, Long recordId, Pageable pageable) { return auditLogRepository.findByRecordIdAndTableNameOrderByCreatedAtDesc(recordId, tableName, pageable).map(this::map); }

    private User currentInternalUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return null;
        return userRepository.findByEmailAndIsDeletedFalse(authentication.getName()).orElse(null);
    }

    private AuditLogResponse map(AuditLog log) {
        User user = log.getUser();
        return AuditLogResponse.builder().id(log.getId()).userId(user == null ? null : user.getId())
                .userName(user == null ? null : user.getFirstName() + " " + user.getLastName())
                .action(log.getAction()).tableName(log.getTableName()).recordId(log.getRecordId())
                .oldValue(log.getOldValue()).newValue(log.getNewValue()).ipAddress(log.getIpAddress())
                .createdAt(log.getCreatedAt()).updatedAt(log.getUpdatedAt()).build();
    }
}
