package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreatePurchaseRequestRequest;
import com.procurement.enterprise.dto.request.ManagerDecisionRequest;
import com.procurement.enterprise.dto.response.EmployeeDashboardStatsResponse;
import com.procurement.enterprise.dto.response.ManagerDashboardStatsResponse;
import com.procurement.enterprise.dto.response.PurchaseRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseRequestService {

    PurchaseRequestResponse create(CreatePurchaseRequestRequest request);

    PurchaseRequestResponse updatePendingRequest(Long id, CreatePurchaseRequestRequest request);

    PurchaseRequestResponse getById(Long id);

    Page<PurchaseRequestResponse> getMyRequests(Pageable pageable);

    EmployeeDashboardStatsResponse getMyDashboardStats();

    Page<PurchaseRequestResponse> getManagerInbox(Pageable pageable);

    ManagerDashboardStatsResponse getManagerDashboardStats();

    PurchaseRequestResponse approve(Long id, ManagerDecisionRequest request);

    PurchaseRequestResponse reject(Long id, ManagerDecisionRequest request);

    PurchaseRequestResponse returnForModification(Long id, ManagerDecisionRequest request);

    PurchaseRequestResponse getAssignmentPreview(Long productId);
}
