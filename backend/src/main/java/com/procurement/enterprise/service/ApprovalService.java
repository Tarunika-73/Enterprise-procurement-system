package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateApprovalRequest;
import com.procurement.enterprise.dto.request.UpdateApprovalRequest;
import com.procurement.enterprise.dto.response.ApprovalResponse;
import com.procurement.enterprise.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApprovalService {

    ApprovalResponse create(CreateApprovalRequest request);

    ApprovalResponse update(Long id, UpdateApprovalRequest request);

    void delete(Long id);

    ApprovalResponse getById(Long id);

    Page<ApprovalResponse> getAll(Pageable pageable);

    Page<ApprovalResponse> getByPurchaseRequest(Long purchaseRequestId, Pageable pageable);

    Page<ApprovalResponse> getByApprover(Long approverId, Pageable pageable);

    Page<ApprovalResponse> getByStatus(ApprovalStatus status, Pageable pageable);

    Page<ApprovalResponse> getByApproverAndStatus(
            Long approverId,
            ApprovalStatus status,
            Pageable pageable
    );
}