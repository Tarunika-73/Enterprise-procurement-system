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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class ApprovalWorkflowServiceImpl
        implements ApprovalWorkflowService {


    private static final Logger log =
            LoggerFactory.getLogger(
                    ApprovalWorkflowServiceImpl.class
            );


    private final ApprovalRepository approvalRepository;

    private final ApprovalHistoryRepository approvalHistoryRepository;

    private final UserRepository userRepository;

    private final ApprovalRoutingService approvalRoutingService;



    @Override
    public WorkflowStatusResponse approveRequest(
            ApprovalRequest request) {


        Approval approval =
                getApproval(
                        request.getRequestId(),
                        request.getApproverId()
                );


        User approver =
                getUser(request.getApproverId());


        validateApprover(
                approval,
                approver
        );


        validateWorkflowForApproval(
                approval
        );


        saveApprovalHistory(
                approval,
                approver,
                ApprovalActionTaken.APPROVED,
                request.getRemarks()
        );


        /*
         * Multi-Level Approval Workflow.
         * Delegates to ApprovalRoutingService, which checks the
         * configured approval hierarchy for this request: if there is a
         * next level, it resolves that level's approver and re-opens
         * the approval as PENDING; otherwise it marks the approval (and
         * the parent requisition) fully APPROVED.
         */
        Approval saved =
                approvalRoutingService.progressAfterApproval(
                        approval
                );


        Long nextApproverId =
                saved.getApprover() != null
                        ? saved.getApprover().getId()
                        : null;


        String message =
                saved.getStatus() == ApprovalStatus.APPROVED
                        ? "Purchase request fully approved."
                        : "Purchase request approved successfully and routed to level "
                                + saved.getLevel() + ".";


        return buildWorkflowResponse(
                saved,
                nextApproverId,
                message
        );
    }





    @Override
    public WorkflowStatusResponse rejectRequest(
            RejectionRequest request) {


        Approval approval =
                getApproval(
                        request.getRequestId(),
                        request.getApproverId()
                );


        User approver =
                getUser(request.getApproverId());


        validateApprover(
                approval,
                approver
        );


        validateWorkflowForRejection(
                approval
        );


        saveApprovalHistory(
                approval,
                approver,
                ApprovalActionTaken.REJECTED,
                request.getRemarks()
        );


        approval.setStatus(
                ApprovalStatus.REJECTED
        );


        Approval saved =
                approvalRepository.save(
                        approval
                );


        approvalRoutingService.markRequisitionRejected(
                saved.getPurchaseRequisition()
        );


        return buildWorkflowResponse(
                saved,
                approver.getId(),
                "Purchase request rejected successfully."
        );
    }





    @Override
    @Transactional(readOnly = true)
    public List<PendingApprovalResponse> getPendingRequests(
            Long approverId) {


        getUser(approverId);


        return approvalRepository
                .findByApproverIdAndStatus(
                        approverId,
                        ApprovalStatus.PENDING
                )
                .stream()
                .map(this::mapPendingResponse)
                .toList();
    }





    @Override
    @Transactional(readOnly = true)
    public List<PendingApprovalResponse> getRequestsByApprover(
            Long approverId) {


        getUser(approverId);


        return approvalRepository
                .findByApproverId(
                        approverId
                )
                .stream()
                .map(this::mapPendingResponse)
                .toList();
    }





    @Override
    @Transactional(readOnly = true)
    public WorkflowStatusResponse getWorkflowStatus(
            Long requestId) {


        Approval approval =
                approvalRepository
                        .findByPurchaseRequisitionIdAndIsDeletedFalse(
                                requestId
                        )
                        .stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Approval",
                                        requestId
                                )
                        );


        Long approverId =
                approval.getApprover() != null
                        ?
                        approval.getApprover().getId()
                        :
                        null;


        return buildWorkflowResponse(
                approval,
                approverId,
                "Workflow status fetched successfully."
        );
    }





    private Approval getApproval(
            Long requestId,
            Long approverId) {


        return approvalRepository
                .findByPurchaseRequisitionIdAndApproverIdAndStatusAndIsDeletedFalse(
                        requestId,
                        approverId,
                        ApprovalStatus.PENDING
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pending approval not found for request ID: "
                                        + requestId
                        )
                );
    }





    private User getUser(Long id) {

        return userRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                id
                        )
                );
    }





    private void validateApprover(
            Approval approval,
            User approver) {


        if (approval.getApprover() == null ||
                !approval.getApprover()
                        .getId()
                        .equals(approver.getId())) {


            throw new InvalidRequestException(
                    "You are not the assigned approver."
            );
        }
    }





    private void validateWorkflowForApproval(
            Approval approval) {


        if (approval.getStatus()
                == ApprovalStatus.REJECTED) {


            throw new InvalidRequestException(
                    "Rejected workflow cannot continue."
            );
        }


        if (approval.getStatus()
                == ApprovalStatus.APPROVED) {


            throw new InvalidRequestException(
                    "Workflow already completed."
            );
        }
    }





    private void validateWorkflowForRejection(
            Approval approval) {


        if (approval.getStatus()
                == ApprovalStatus.APPROVED) {


            throw new InvalidRequestException(
                    "Approved workflow cannot be rejected."
            );
        }


        if (approval.getStatus()
                == ApprovalStatus.REJECTED) {


            throw new InvalidRequestException(
                    "Workflow already rejected."
            );
        }
    }





    private void saveApprovalHistory(
            Approval approval,
            User user,
            ApprovalActionTaken action,
            String remarks) {


        ApprovalHistory history =
                ApprovalHistory.builder()
                        .approval(approval)
                        .actionBy(user)
                        .actionTaken(action)
                        .approvalLevel(
                                approval.getLevel()
                        )
                        .remarks(remarks)
                        .isDeleted(false)
                        .build();


        approvalHistoryRepository.save(history);
    }






    private PendingApprovalResponse mapPendingResponse(
            Approval approval) {


        return PendingApprovalResponse.builder()

                .requestId(
                        approval.getPurchaseRequisition()
                                .getId()
                )

                .requestTitle(
                        approval.getPurchaseRequisition()
                                .getDescription()
                )

                .approverId(
                        approval.getApprover() != null
                                ?
                                approval.getApprover().getId()
                                :
                                null
                )

                .approvalLevel(
                        approval.getLevel()
                )

                .status(
                        approval.getStatus()
                                .name()
                )

                .requestedDate(
                        approval.getPurchaseRequisition()
                                .getCreatedAt()
                )

                .build();
    }





    private WorkflowStatusResponse buildWorkflowResponse(
            Approval approval,
            Long approverId,
            String message) {


        return WorkflowStatusResponse.builder()

                .requestId(
                        approval.getPurchaseRequisition()
                                .getId()
                )

                .requestStatus(
                        approval.getStatus()
                                .name()
                )

                .currentApprovalLevel(
                        approval.getLevel()
                )

                .currentApproverId(
                        approverId
                )

                .message(
                        message
                )

                .actionTime(
                        LocalDateTime.now()
                )

                .build();
    }
}
