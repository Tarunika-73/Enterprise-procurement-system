package com.procurement.enterprise.service;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.procurement.enterprise.dto.request.ApprovalActionRequest;
import com.procurement.enterprise.dto.request.CreateApprovalRequest;
import com.procurement.enterprise.dto.request.UpdateApprovalRequest;
import com.procurement.enterprise.dto.response.ApprovalHistoryResponse;
import com.procurement.enterprise.dto.response.ApprovalResponse;
import com.procurement.enterprise.entity.Approval;
import com.procurement.enterprise.entity.ApprovalHistory;
import com.procurement.enterprise.entity.PurchaseRequest;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.enums.ApprovalActionTaken;
import com.procurement.enterprise.enums.ApprovalStatus;
import com.procurement.enterprise.exception.DuplicateResourceException;
import com.procurement.enterprise.exception.ForbiddenException;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.ApprovalHistoryRepository;
import com.procurement.enterprise.repository.ApprovalRepository;
import com.procurement.enterprise.repository.PurchaseRequestRepository;
import com.procurement.enterprise.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Business logic implementation for approval hierarchy management.
 *
 * <p>An {@link Approval} represents one level in the multi-step sign-off
 * workflow attached to a {@link PurchaseRequest}. Every decision made
 * against an approval step (approve / reject / escalate) is additionally
 * captured as an immutable {@link ApprovalHistory} audit entry.</p>
 */
@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private static final Logger log = LoggerFactory.getLogger(ApprovalServiceImpl.class);

    /** Approval steps in these statuses are final and can no longer be modified or actioned again. */
    private static final Set<ApprovalStatus> FINAL_STATUSES = EnumSet.of(ApprovalStatus.APPROVED, ApprovalStatus.REJECTED);

    private final ApprovalRepository approvalRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ApprovalResponse createApproval(CreateApprovalRequest request) {
        PurchaseRequest purchaseRequest = findActivePurchaseRequest(request.getPurchaseRequestId());

        validateUniqueLevel(request.getPurchaseRequestId(), request.getLevel(), null);

        User approver = null;
        if (request.getApproverId() != null) {
            approver = findActiveUser(request.getApproverId());
        }

        Approval approval = Approval.builder()
                .purchaseRequest(purchaseRequest)
                .level(request.getLevel())
                .approver(approver)
                .status(request.getStatus() != null ? request.getStatus() : ApprovalStatus.PENDING)
                .comments(request.getComments())
                .isDeleted(false)
                .build();

        Approval saved = approvalRepository.save(approval);
        log.info("Created approval step with id: {} (purchaseRequestId={}, level={})",
                saved.getId(), request.getPurchaseRequestId(), request.getLevel());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ApprovalResponse updateApproval(Long id, UpdateApprovalRequest request) {
        Approval approval = findActiveApprovalById(id);

        if (FINAL_STATUSES.contains(approval.getStatus())) {
            throw new InvalidRequestException(
                    "Cannot modify an approval step that has already been " + approval.getStatus().name().toLowerCase());
        }

        if (request.getLevel() != null) {
            validateUniqueLevel(approval.getPurchaseRequest().getId(), request.getLevel(), id);
            approval.setLevel(request.getLevel());
        }

        if (request.getApproverId() != null) {
            approval.setApprover(findActiveUser(request.getApproverId()));
        }

        if (request.getStatus() != null) {
            approval.setStatus(request.getStatus());
        }

        if (request.getComments() != null) {
            approval.setComments(request.getComments());
        }

        Approval updated = approvalRepository.save(approval);
        log.info("Updated approval step with id: {}", updated.getId());
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteApproval(Long id) {
        Approval approval = findActiveApprovalById(id);

        if (FINAL_STATUSES.contains(approval.getStatus())) {
            throw new InvalidRequestException(
                    "Cannot delete an approval step that has already been " + approval.getStatus().name().toLowerCase()
                            + ". Finalized approvals must be retained for audit purposes.");
        }

        approval.setIsDeleted(true);
        approvalRepository.save(approval);
        log.info("Soft deleted approval step with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ApprovalResponse getApprovalById(Long id) {
        return mapToResponse(findActiveApprovalById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalResponse> getAllApprovals(Pageable pageable) {
        return approvalRepository.findAllByIsDeletedFalse(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalResponse> getApprovalsByPurchaseRequest(Long purchaseRequestId, Pageable pageable) {
        findActivePurchaseRequest(purchaseRequestId);
        return approvalRepository.findByPurchaseRequestIdAndIsDeletedFalse(purchaseRequestId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalResponse> getApprovalsByApprover(Long approverId, Pageable pageable) {
        findActiveUser(approverId);
        return approvalRepository.findByApproverIdAndIsDeletedFalse(approverId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalResponse> getApprovalsByStatus(ApprovalStatus status, Pageable pageable) {
        return approvalRepository.findByStatusAndIsDeletedFalse(status, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public ApprovalResponse takeAction(Long id, ApprovalActionRequest request) {
        Approval approval = findActiveApprovalById(id);

        if (FINAL_STATUSES.contains(approval.getStatus())) {
            throw new InvalidRequestException(
                    "Cannot action an approval step that has already been " + approval.getStatus().name().toLowerCase());
        }

        User actionBy = findActiveUser(request.getActionById());

        // If an approver has already been assigned to this step, only that approver may action it.
        if (approval.getApprover() != null && !Objects.equals(approval.getApprover().getId(), actionBy.getId())) {
            throw new ForbiddenException("Only the assigned approver can action this approval step");
        }

        approval.setStatus(mapActionToStatus(request.getActionTaken()));
        approval.setComments(request.getComments());
        if (approval.getApprover() == null) {
            approval.setApprover(actionBy);
        }
        Approval savedApproval = approvalRepository.save(approval);

        ApprovalHistory history = ApprovalHistory.builder()
                .approval(savedApproval)
                .actionBy(actionBy)
                .actionTaken(request.getActionTaken())
                .comments(request.getComments())
                .isDeleted(false)
                .build();
        approvalHistoryRepository.save(history);

        log.info("Approval step {} actioned as {} by user {}", id, request.getActionTaken(), actionBy.getId());
        return mapToResponse(savedApproval);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalHistoryResponse> getApprovalHistory(Long approvalId, Pageable pageable) {
        findActiveApprovalById(approvalId);
        return approvalHistoryRepository.findByApprovalIdAndIsDeletedFalse(approvalId, pageable)
                .map(this::mapToHistoryResponse);
    }

    private ApprovalStatus mapActionToStatus(ApprovalActionTaken actionTaken) {
        return switch (actionTaken) {
            case APPROVED -> ApprovalStatus.APPROVED;
            case REJECTED -> ApprovalStatus.REJECTED;
            case ESCALATED -> ApprovalStatus.ESCALATED;
        };
    }

    private void validateUniqueLevel(Long purchaseRequestId, Integer level, Long excludeApprovalId) {
        approvalRepository.findByPurchaseRequestIdAndLevelAndIsDeletedFalse(purchaseRequestId, level)
                .filter(existing -> !existing.getId().equals(excludeApprovalId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Approval level " + level + " already exists for purchase request " + purchaseRequestId);
                });
    }

    private Approval findActiveApprovalById(Long id) {
        return approvalRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Approval", id));
    }

    private PurchaseRequest findActivePurchaseRequest(Long id) {
        return purchaseRequestRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase request", id));
    }

    private User findActiveUser(Long id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new InvalidRequestException("User with id " + id + " is not active");
        }
        return user;
    }

    private ApprovalResponse mapToResponse(Approval approval) {
        User approver = approval.getApprover();
        return ApprovalResponse.builder()
                .id(approval.getId())
                .purchaseRequestId(approval.getPurchaseRequest().getId())
                .level(approval.getLevel())
                .approverId(approver != null ? approver.getId() : null)
                .approverName(approver != null ? approver.getFirstName() + " " + approver.getLastName() : null)
                .status(approval.getStatus())
                .comments(approval.getComments())
                .createdAt(approval.getCreatedAt())
                .updatedAt(approval.getUpdatedAt())
                .build();
    }

    private ApprovalHistoryResponse mapToHistoryResponse(ApprovalHistory history) {
        User actionBy = history.getActionBy();
        return ApprovalHistoryResponse.builder()
                .id(history.getId())
                .approvalId(history.getApproval().getId())
                .actionById(actionBy.getId())
                .actionByName(actionBy.getFirstName() + " " + actionBy.getLastName())
                .actionTaken(history.getActionTaken())
                .comments(history.getComments())
                .createdAt(history.getCreatedAt())
                .updatedAt(history.getUpdatedAt())
                .build();
    }
}
