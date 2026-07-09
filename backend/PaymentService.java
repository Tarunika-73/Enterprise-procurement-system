package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // TODO: add custom query methods as needed
}
