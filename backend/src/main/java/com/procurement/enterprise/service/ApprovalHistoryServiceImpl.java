package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.ApprovalHistoryResponse;
import com.procurement.enterprise.entity.ApprovalHistory;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.ApprovalHistoryRepository;
import com.procurement.enterprise.repository.UserRepository;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovalHistoryServiceImpl
        implements ApprovalHistoryService {


    private final ApprovalHistoryRepository approvalHistoryRepository;
    private final UserRepository userRepository;


    @Override
    public List<ApprovalHistoryResponse> getApprovalHistory(Long requestId) {

        if (requestId == null) {
            throw new IllegalArgumentException(
                    "Request ID must not be null."
            );
        }


        List<ApprovalHistory> historyList =
                approvalHistoryRepository
                        .findByPurchaseRequestIdAndIsDeletedFalseOrderByCreatedAtDesc(
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


        return historyList.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ApprovalHistoryResponse> getMyHistory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found."));
        return getHistoryByApprover(currentUser.getId());
    }



    private ApprovalHistoryResponse mapToResponse(
            ApprovalHistory history) {


        Long requestId = null;


        /*
         * Approval entity contains PurchaseRequest relation.
         */
        String requestNumber = null;
        String employeeName = null;
        if (history.getPurchaseRequest() != null) {
            requestId = history.getPurchaseRequest().getId();
            requestNumber = history.getPurchaseRequest().getRequestNumber();
            if (history.getPurchaseRequest().getRequester() != null) {
                employeeName = history.getPurchaseRequest().getRequester().getFirstName() + " "
                        + history.getPurchaseRequest().getRequester().getLastName();
            }
        } else if (history.getApproval() != null &&
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

                .requestNumber(requestNumber)

                .employeeName(employeeName)

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
