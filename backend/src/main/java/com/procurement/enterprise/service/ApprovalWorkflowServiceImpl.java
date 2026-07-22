```java
        package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.approvalworkflow.ApprovalRequest;
import com.procurement.enterprise.dto.approvalworkflow.PendingApprovalResponse;
import com.procurement.enterprise.dto.approvalworkflow.RejectionRequest;
import com.procurement.enterprise.dto.approvalworkflow.WorkflowStatusResponse;
import com.procurement.enterprise.entity.Approval;
import com.procurement.enterprise.entity.ApprovalHistory;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.enums.ApprovalActionTaken;
import com.procurement.enterprise.enums.ApprovalStatus;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.ApprovalHistoryRepository;
import com.procurement.enterprise.repository.ApprovalRepository;
import com.procurement.enterprise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ApprovalWorkflowServiceImpl implements ApprovalWorkflowService {

    private final ApprovalRepository approvalRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;
    private final UserRepository userRepository;

    @Override
    public WorkflowStatusResponse approveRequest(ApprovalRequest request) {

        Approval approval = getApproval(request.getRequestId());

        User approver = getUser(request.getApproverId());

        validateApprover(approval, approver);

        validateWorkflowForApproval(approval);

        validateDuplicateAction(
                approval.getId(),
                approver.getId()
        );

        ApprovalHistory history = ApprovalHistory.builder()
                .approval(approval)
                .actionBy(approver)
                .actionTaken(ApprovalActionTaken.APPROVED)
                .approvalLevel(approval.getApprovalLevel())
                .remarks(request.getRemarks())
                .isDeleted(false)
                .build();

        approvalHistoryRepository.save(history);

        /*
         * If there is another approval level, move the workflow
         * to the next approval level.
         *
         * Otherwise, complete the workflow.
         */
        if (approval.getApprovalLevel() < approval.getTotalApprovalLevels()) {

            approval.setApprovalLevel(
                    approval.getApprovalLevel() + 1
            );

            approval.setStatus(ApprovalStatus.PENDING);

        } else {

            approval.setStatus(ApprovalStatus.APPROVED);
        }

        Approval savedApproval = approvalRepository.save(approval);

        return buildWorkflowStatusResponse(
                savedApproval,
                approver.getId(),
                "Purchase requisition approved successfully."
        );
    }

    @Override
    public WorkflowStatusResponse rejectRequest(RejectionRequest request) {

        Approval approval = getApproval(request.getRequestId());

        User approver = getUser(request.getApproverId());

        validateApprover(approval, approver);

        validateWorkflowForRejection(approval);

        validateDuplicateAction(
                approval.getId(),
                approver.getId()
        );

        ApprovalHistory history = ApprovalHistory.builder()
                .approval(approval)
                .actionBy(approver)
                .actionTaken(ApprovalActionTaken.REJECTED)
                .approvalLevel(approval.getApprovalLevel())
                .remarks(request.getRemarks())
                .isDeleted(false)
                .build();

        approvalHistoryRepository.save(history);

        approval.setStatus(ApprovalStatus.REJECTED);

        Approval savedApproval = approvalRepository.save(approval);

        return buildWorkflowStatusResponse(
                savedApproval,
                approver.getId(),
                "Purchase requisition rejected successfully."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingApprovalResponse> getPendingRequests(Long approverId) {

        getUser(approverId);

        List<Approval> approvals =
                approvalRepository.findByApproverIdAndStatus(
                        approverId,
                        ApprovalStatus.PENDING
                );

        return approvals.stream()
                .map(this::mapToPendingApprovalResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingApprovalResponse> getRequestsByApprover(Long approverId) {

        getUser(approverId);

        List<Approval> approvals =
                approvalRepository.findByApproverId(approverId);

        return approvals.stream()
                .map(this::mapToPendingApprovalResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowStatusResponse getWorkflowStatus(Long requestId) {

        Approval approval = getApproval(requestId);

        Long currentApproverId = null;

        if (approval.getApprover() != null) {
            currentApproverId = approval.getApprover().getId();
        }

        return buildWorkflowStatusResponse(
                approval,
                currentApproverId,
                "Workflow status fetched successfully."
        );
    }

    private Approval getApproval(Long requestId) {

        return approvalRepository.findByRequestId(requestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Approval workflow not found for request ID: "
                                        + requestId
                        )
                );
    }

    private User getUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + userId
                        )
                );
    }

    private void validateApprover(
            Approval approval,
            User approver
    ) {

        if (approval.getApprover() == null ||
                !approval.getApprover().getId()
                        .equals(approver.getId())) {

            throw new InvalidRequestException(
                    "You are not the assigned approver for this request."
            );
        }

        if (approval.getRequestedBy() != null &&
                approval.getRequestedBy().getId()
                        .equals(approver.getId())) {

            throw new InvalidRequestException(
                    "You cannot approve or reject your own request."
            );
        }
    }

    private void validateWorkflowForApproval(Approval approval) {

        if (approval.getStatus() == ApprovalStatus.APPROVED) {
            throw new InvalidRequestException(
                    "The approval workflow is already completed."
            );
        }

        if (approval.getStatus() == ApprovalStatus.REJECTED) {
            throw new InvalidRequestException(
                    "A rejected workflow cannot be approved."
            );
        }
    }

    private void validateWorkflowForRejection(Approval approval) {

        if (approval.getStatus() == ApprovalStatus.APPROVED) {
            throw new InvalidRequestException(
                    "A completed workflow cannot be rejected."
            );
        }

        if (approval.getStatus() == ApprovalStatus.REJECTED) {
            throw new InvalidRequestException(
                    "The approval workflow is already rejected."
            );
        }
    }

    private void validateDuplicateAction(
            Long approvalId,
            Long approverId
    ) {

        boolean actionAlreadyTaken =
                approvalHistoryRepository
                        .existsByApprovalIdAndActionByIdAndIsDeletedFalse(
                                approvalId,
                                approverId
                        );

        if (actionAlreadyTaken) {
            throw new InvalidRequestException(
                    "You have already taken action on this request."
            );
        }
    }

    private PendingApprovalResponse mapToPendingApprovalResponse(
            Approval approval
    ) {

        Long approverId = null;
        String requestedBy = null;
        String requestTitle = null;
        LocalDateTime requestedDate = null;

        if (approval.getApprover() != null) {
            approverId = approval.getApprover().getId();
        }

        if (approval.getRequestedBy() != null) {
            requestedBy = approval.getRequestedBy().getName();
        }

        if (approval.getPurchaseRequisition() != null) {
            requestTitle =
                    approval.getPurchaseRequisition().getTitle();

            requestedDate =
                    approval.getPurchaseRequisition().getCreatedAt();
        }

        return PendingApprovalResponse.builder()
                .requestId(approval.getRequestId())
                .requestTitle(requestTitle)
                .requestedBy(requestedBy)
                .approverId(approverId)
                .approvalLevel(approval.getApprovalLevel())
                .status(approval.getStatus().name())
                .requestedDate(requestedDate)
                .build();
    }

    private WorkflowStatusResponse buildWorkflowStatusResponse(
            Approval approval,
            Long approverId,
            String message
    ) {

        return WorkflowStatusResponse.builder()
                .requestId(approval.getRequestId())
                .requestStatus(approval.getStatus().name())
                .currentApprovalLevel(
                        approval.getApprovalLevel()
                )
                .currentApproverId(approverId)
                .message(message)
                .actionTime(LocalDateTime.now())
                .build();
    }
}
```
