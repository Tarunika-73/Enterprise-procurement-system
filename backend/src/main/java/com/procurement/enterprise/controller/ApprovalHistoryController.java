package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.response.ApprovalHistoryResponse;
import com.procurement.enterprise.service.ApprovalHistoryService;
import com.procurement.enterprise.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({
        "/v1/approval-history",
        "/approval-history"
})
@RequiredArgsConstructor
public class ApprovalHistoryController {

    private final ApprovalHistoryService approvalHistoryService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<List<ApprovalHistoryResponse>> getMyHistory() {
        return ApiResponse.success("Manager approval history fetched successfully.", approvalHistoryService.getMyHistory());
    }


    /**
     * Get complete approval history of a purchase request.
     */
    @GetMapping("/request/{requestId}")
    public ApiResponse<List<ApprovalHistoryResponse>> getApprovalHistory(
            @PathVariable Long requestId) {

        List<ApprovalHistoryResponse> response =
                approvalHistoryService.getApprovalHistory(requestId);

        return ApiResponse.success(
                "Approval history fetched successfully.",
                response
        );
    }


    /**
     * Get all approval actions performed by an approver.
     */
    @GetMapping("/approver/{approverId}")
    public ApiResponse<List<ApprovalHistoryResponse>> getHistoryByApprover(
            @PathVariable Long approverId) {

        List<ApprovalHistoryResponse> response =
                approvalHistoryService.getHistoryByApprover(approverId);

        return ApiResponse.success(
                "Approver approval history fetched successfully.",
                response
        );
    }
}
