package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.ApprovalHistoryResponse;
import com.procurement.enterprise.entity.ApprovalHistory;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.ApprovalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovalHistoryServiceImpl
        implements ApprovalHistoryService {


    private final ApprovalHistoryRepository approvalHistoryRepository;


    @Override
    public List<ApprovalHistoryResponse> getApprovalHistory(Long requestId) {

        if (requestId == null) {
            throw new IllegalArgumentException(
                    "Request ID must not be null."
            );
        }


        List<ApprovalHistory> historyList =
                approvalHistoryRepository
                        .findByApprovalIdAndIsDeletedFalseOrderByCreatedAtDesc(
                                requestId
                        );


        if (historyList.isEmpty()) {

            throw new ResourceNotFoundException(
                    "No approval history found for request ID: "
                            + requestId
            );
        }


        return historyList.stream()
                .map(this::mapToResponse)
                .toList();
    }



    @Override
    public List<ApprovalHistoryResponse> getHistoryByApprover(
            Long approverId) {


        if (approverId == null) {

            throw new IllegalArgumentException(
                    "Approver ID must not be null."
            );
        }


        List<ApprovalHistory> historyList =
                approvalHistoryRepository
                        .findByActionByIdAndIsDeletedFalseOrderByCreatedAtDesc(
                                approverId
                        );


        if (historyList.isEmpty()) {

            throw new ResourceNotFoundException(
                    "No approval history found for approver ID: "
                            + approverId
            );
        }


        return historyList.stream()
                .map(this::mapToResponse)
                .toList();
    }



    private ApprovalHistoryResponse mapToResponse(
            ApprovalHistory history) {


        Long requestId = null;


        /*
         * Approval entity contains PurchaseRequest relation.
         */
        if (history.getApproval() != null &&
                history.getApproval().getPurchaseRequisition() != null) {


            requestId =
                    history.getApproval()
                            .getPurchaseRequisition()
                            .getId();

        }



        Long approverId = null;


        if (history.getActionBy() != null) {

            approverId =
                    history.getActionBy()
                            .getId();
        }



        String actionTaken = null;


        if (history.getActionTaken() != null) {

            actionTaken =
                    history.getActionTaken()
                            .name();
        }



        return ApprovalHistoryResponse.builder()

                .historyId(
                        history.getId()
                )

                .requestId(
                        requestId
                )

                .approverId(
                        approverId
                )

                .approvalLevel(
                        history.getApprovalLevel()
                )

                .actionTaken(
                        actionTaken
                )

                .remarks(
                        history.getRemarks()
                )

                .actionTime(
                        history.getCreatedAt()
                )

                .build();
    }
}