package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.ApprovalHierarchyResponse;
import com.procurement.enterprise.dto.response.PurchaseRequestResponse;
import com.procurement.enterprise.entity.Approval;
import com.procurement.enterprise.entity.ApprovalHierarchy;
import com.procurement.enterprise.entity.Department;
import com.procurement.enterprise.entity.PurchaseRequest;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.enums.ApprovalStatus;
import com.procurement.enterprise.enums.PurchaseRequestStatus;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.ApprovalHierarchyRepository;
import com.procurement.enterprise.repository.ApprovalRepository;
import com.procurement.enterprise.repository.PurchaseRequestRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * Implements automatic routing of purchase requests through the department's
 * configured approval hierarchy.
 */
@Service
@RequiredArgsConstructor
public class RequestRoutingServiceImpl implements RequestRoutingService {

    private static final Logger log =
            LoggerFactory.getLogger(RequestRoutingServiceImpl.class);

    /** Statuses from which a request is still eligible to be (re-)routed. */
    private static final Set<PurchaseRequestStatus> ROUTABLE_STATUSES =
            EnumSet.of(PurchaseRequestStatus.DRAFT, PurchaseRequestStatus.SUBMITTED);

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final ApprovalHierarchyRepository approvalHierarchyRepository;
    private final ApprovalRepository approvalRepository;

    @Override
    @Transactional
    public PurchaseRequestResponse routeNewRequest(Long purchaseRequestId) {

        PurchaseRequest purchaseRequest = purchaseRequestRepository
                .findByIdAndIsDeletedFalse(purchaseRequestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("PurchaseRequest", purchaseRequestId));

        if (purchaseRequest.getCurrentApprover() != null) {
            throw new InvalidRequestException(
                    "Purchase request " + purchaseRequestId + " has already been routed");
        }

        if (!ROUTABLE_STATUSES.contains(purchaseRequest.getStatus())) {
            throw new InvalidRequestException(
                    "Purchase request " + purchaseRequestId
                            + " cannot be routed from status " + purchaseRequest.getStatus());
        }

        Department department = purchaseRequest.getDepartment();

        ApprovalHierarchy firstLevel = approvalHierarchyRepository
                .findFirstByDepartmentIdAndIsDeletedFalseOrderByLevelAsc(department.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ApprovalHierarchy",
                                "department",
                                department.getName()));

        User approver = firstLevel.getApprover();

        if (approver.getId().equals(purchaseRequest.getRequester().getId())) {
            throw new InvalidRequestException(
                    "The configured level-1 approver for department " + department.getName()
                            + " is the same as the requester; an employee cannot approve their own request");
        }

        approvalRepository
                .findByPurchaseRequestIdAndLevelAndIsDeletedFalse(
                        purchaseRequest.getId(), firstLevel.getLevel())
                .ifPresent(existing -> {
                    throw new InvalidRequestException(
                            "An approval record already exists at level " + firstLevel.getLevel()
                                    + " for this request");
                });

        Approval approval = Approval.builder()
                .purchaseRequest(purchaseRequest)
                .level(firstLevel.getLevel())
                .approver(approver)
                .status(ApprovalStatus.PENDING)
                .isDeleted(false)
                .build();

        approvalRepository.save(approval);

        purchaseRequest.setCurrentApprover(approver);
        purchaseRequest.setCurrentLevel(firstLevel.getLevel());
        purchaseRequest.setStatus(PurchaseRequestStatus.PENDING);

        PurchaseRequest saved = purchaseRequestRepository.save(purchaseRequest);

        log.info("Routed purchase request {} to approver {} at level {}",
                saved.getId(), approver.getId(), firstLevel.getLevel());

        return mapToPurchaseRequestResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApprovalHierarchyResponse> getNextLevel(Long departmentId, Integer currentLevel) {

        return approvalHierarchyRepository
                .findFirstByDepartmentIdAndLevelGreaterThanAndIsDeletedFalseOrderByLevelAsc(
                        departmentId, currentLevel)
                .map(this::mapToHierarchyResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApprovalHierarchyResponse> getFirstLevel(Long departmentId) {

        return approvalHierarchyRepository
                .findFirstByDepartmentIdAndIsDeletedFalseOrderByLevelAsc(departmentId)
                .map(this::mapToHierarchyResponse);
    }

    private ApprovalHierarchyResponse mapToHierarchyResponse(ApprovalHierarchy hierarchy) {

        Department department = hierarchy.getDepartment();
        User approver = hierarchy.getApprover();

        return ApprovalHierarchyResponse.builder()
                .id(hierarchy.getId())
                .departmentId(department != null ? department.getId() : null)
                .departmentName(department != null ? department.getName() : null)
                .level(hierarchy.getLevel())
                .approverId(approver != null ? approver.getId() : null)
                .approverName(approver != null
                        ? approver.getFirstName() + " " + approver.getLastName()
                        : null)
                .createdAt(hierarchy.getCreatedAt())
                .updatedAt(hierarchy.getUpdatedAt())
                .build();
    }

    private PurchaseRequestResponse mapToPurchaseRequestResponse(PurchaseRequest purchaseRequest) {

        User requester = purchaseRequest.getRequester();
        Department department = purchaseRequest.getDepartment();
        User currentApprover = purchaseRequest.getCurrentApprover();

        return PurchaseRequestResponse.builder()
                .id(purchaseRequest.getId())
                .requestNumber(purchaseRequest.getRequestNumber())
                .requesterId(requester != null ? requester.getId() : null)
                .requesterName(requester != null
                        ? requester.getFirstName() + " " + requester.getLastName()
                        : null)
                .departmentId(department != null ? department.getId() : null)
                .departmentName(department != null ? department.getName() : null)
                .justification(purchaseRequest.getJustification())
                .status(purchaseRequest.getStatus())
                .totalAmount(purchaseRequest.getTotalAmount())
                .currentApproverId(currentApprover != null ? currentApprover.getId() : null)
                .currentApproverName(currentApprover != null
                        ? currentApprover.getFirstName() + " " + currentApprover.getLastName()
                        : null)
                .currentLevel(purchaseRequest.getCurrentLevel())
                .createdAt(purchaseRequest.getCreatedAt())
                .updatedAt(purchaseRequest.getUpdatedAt())
                .build();
    }
}
