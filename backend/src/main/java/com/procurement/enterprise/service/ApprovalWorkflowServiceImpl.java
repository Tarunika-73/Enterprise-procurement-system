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


        Integer maxLevel =
                getMaximumApprovalLevel(
                        approval.getPurchaseRequisition().getId()
                );


        if (approval.getLevel() < maxLevel) {

            approval.setLevel(
                    approval.getLevel() + 1
            );

            approval.setStatus(
                    ApprovalStatus.PENDING
            );


            log.info(
                    "Moved approval {} to next level {}",
                    approval.getId(),
                    approval.getLevel()
            );


        } else {


            approval.setStatus(
                    ApprovalStatus.APPROVED
            );


            log.info(
                    "Approval completed for requisition {}",
                    approval.getPurchaseRequisition().getId()
            );
        }


        Approval saved =
                approvalRepository.save(
                        approval
                );


        return buildWorkflowResponse(
                saved,
                approver.getId(),
                "Purchase request approved successfully."
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





    private Integer getMaximumApprovalLevel(
            Long requestId) {


        return approvalRepository
                .findByPurchaseRequisitionIdAndIsDeletedFalse(
                        requestId
                )
                .stream()
                .map(Approval::getLevel)
                .max(Integer::compareTo)
                .orElse(1);
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