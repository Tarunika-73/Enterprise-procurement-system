package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateApprovalRequest;
import com.procurement.enterprise.dto.request.UpdateApprovalRequest;
import com.procurement.enterprise.dto.response.ApprovalResponse;
import com.procurement.enterprise.entity.Approval;
import com.procurement.enterprise.entity.PurchaseRequest;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.enums.ApprovalStatus;
import com.procurement.enterprise.exception.DuplicateResourceException;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.ApprovalRepository;
import com.procurement.enterprise.repository.PurchaseRequestRepository;
import com.procurement.enterprise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private static final Logger log =
            LoggerFactory.getLogger(ApprovalServiceImpl.class);

    private final ApprovalRepository approvalRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ApprovalResponse create(CreateApprovalRequest request) {

        validateCreateRequest(request);

        PurchaseRequest purchaseRequest = purchaseRequestRepository
                .findByIdAndIsDeletedFalse(request.getPurchaseRequestId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "PurchaseRequest",
                                request.getPurchaseRequestId()
                        ));

        User approver = null;
        if (request.getApproverId() != null) {
            approver = userRepository
                    .findByIdAndIsDeletedFalse(request.getApproverId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User",
                                    request.getApproverId()
                            ));
        }

        approvalRepository
                .findByPurchaseRequestIdAndLevelAndIsDeletedFalse(
                        request.getPurchaseRequestId(),
                        request.getLevel()
                )
                .ifPresent(existingApproval -> {
                    throw new DuplicateResourceException(
                            "Approval",
                            "level",
                            request.getLevel().toString()
                    );
                });

        Approval approval = Approval.builder()
                .purchaseRequest(purchaseRequest)
                .level(request.getLevel())
                .approver(approver)
                .status(request.getStatus() != null
                        ? request.getStatus()
                        : ApprovalStatus.PENDING)
                .comments(request.getComments())
                .isDeleted(false)
                .build();

        Approval saved = approvalRepository.save(approval);

        log.info("Created approval with id {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ApprovalResponse update(Long id, UpdateApprovalRequest request) {

        Approval approval = findApproval(id);

        if (request.getLevel() != null) {
            approval.setLevel(request.getLevel());
        }

        if (request.getApproverId() != null) {
            User approver = userRepository
                    .findByIdAndIsDeletedFalse(request.getApproverId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User",
                                    request.getApproverId()
                            ));
            approval.setApprover(approver);
        }

        if (request.getStatus() != null) {
            approval.setStatus(request.getStatus());
        }

        if (request.getComments() != null) {
            approval.setComments(request.getComments());
        }

        Approval updated = approvalRepository.save(approval);

        log.info("Updated approval {}", updated.getId());

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Approval approval = findApproval(id);

        approval.setIsDeleted(true);

        approvalRepository.save(approval);

        log.info("Soft deleted approval {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ApprovalResponse getById(Long id) {

        return mapToResponse(findApproval(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalResponse> getAll(Pageable pageable) {

        return approvalRepository
                .findAllByIsDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalResponse> getByPurchaseRequest(
            Long purchaseRequestId,
            Pageable pageable) {

        purchaseRequestRepository
                .findByIdAndIsDeletedFalse(purchaseRequestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "PurchaseRequest",
                                purchaseRequestId
                        ));

        return approvalRepository
                .findByPurchaseRequestIdAndIsDeletedFalse(
                        purchaseRequestId,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalResponse> getByApprover(
            Long approverId,
            Pageable pageable) {

        userRepository
                .findByIdAndIsDeletedFalse(approverId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                approverId
                        ));

        return approvalRepository
                .findByApproverIdAndIsDeletedFalse(
                        approverId,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalResponse> getByStatus(
            ApprovalStatus status,
            Pageable pageable) {

        return approvalRepository
                .findByStatusAndIsDeletedFalse(status, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalResponse> getByApproverAndStatus(
            Long approverId,
            ApprovalStatus status,
            Pageable pageable) {

        userRepository
                .findByIdAndIsDeletedFalse(approverId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                approverId
                        ));

        return approvalRepository
                .findByApproverIdAndStatusAndIsDeletedFalse(
                        approverId,
                        status,
                        pageable
                )
                .map(this::mapToResponse);
    }

    private Approval findApproval(Long id) {

        return approvalRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Approval",
                                id
                        ));
    }

    private void validateCreateRequest(CreateApprovalRequest request) {

        if (request.getPurchaseRequestId() == null) {
            throw new InvalidRequestException(
                    "Purchase request ID is required"
            );
        }

        if (request.getLevel() == null) {
            throw new InvalidRequestException(
                    "Approval level is required"
            );
        }
    }

    private ApprovalResponse mapToResponse(Approval approval) {

        PurchaseRequest purchaseRequest = approval.getPurchaseRequest();
        User approver = approval.getApprover();

        String approverName = null;
        if (approver != null) {
            approverName = approver.getFirstName() + " " + approver.getLastName();
        }

        return ApprovalResponse.builder()
                .id(approval.getId())
                .purchaseRequestId(
                        purchaseRequest != null
                                ? purchaseRequest.getId()
                                : null
                )
                .level(approval.getLevel())
                .approverId(
                        approver != null
                                ? approver.getId()
                                : null
                )
                .approverName(approverName)
                .status(approval.getStatus())
                .comments(approval.getComments())
                .createdAt(approval.getCreatedAt())
                .updatedAt(approval.getUpdatedAt())
                .build();
    }
}
