package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.response.AuditLogResponse;
import com.procurement.enterprise.service.AuditLogService;
import com.procurement.enterprise.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AuditLogController {
    private final AuditLogService auditLogService;
    @GetMapping public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> all(@ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable p) { return ok(auditLogService.getAll(p)); }
    @GetMapping("/user/{userId}") public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> byUser(@PathVariable Long userId, @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable p) { return ok(auditLogService.getByUser(userId, p)); }
    @GetMapping("/table/{tableName}") public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> byTable(@PathVariable String tableName, @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable p) { return ok(auditLogService.getByTable(tableName, p)); }
    @GetMapping("/record/{tableName}/{recordId}") public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> byRecord(@PathVariable String tableName, @PathVariable Long recordId, @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable p) { return ok(auditLogService.getByRecord(tableName, recordId, p)); }
    private ResponseEntity<ApiResponse<Page<AuditLogResponse>>> ok(Page<AuditLogResponse> page) { return ResponseEntity.ok(ApiResponse.success("Audit logs fetched successfully.", page)); }
}
