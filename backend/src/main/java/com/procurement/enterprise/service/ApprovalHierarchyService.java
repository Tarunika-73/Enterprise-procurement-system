
package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateApprovalHierarchyRequest;
import com.procurement.enterprise.dto.request.UpdateApprovalHierarchyRequest;
import com.procurement.enterprise.dto.response.ApprovalHierarchyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/** Service interface for managing per-department approval hierarchies (CRUD). */
public interface ApprovalHierarchyService {

    ApprovalHierarchyResponse create(CreateApprovalHierarchyRequest request);

    ApprovalHierarchyResponse update(Long id, UpdateApprovalHierarchyRequest request);

    void delete(Long id);

    ApprovalHierarchyResponse getById(Long id);

    Page<ApprovalHierarchyResponse> getAll(Pageable pageable);

    /** Returns the full approval chain for a department, ordered level 1 upward. */
    List<ApprovalHierarchyResponse> getByDepartment(Long departmentId);
}
