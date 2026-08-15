package com.procurement.enterprise.service;
import com.procurement.enterprise.dto.request.CreateReceiptRequest;
import com.procurement.enterprise.dto.response.ReceiptResponse;
import com.procurement.enterprise.dto.response.GoodsReceiptWorkflowResponse;
import java.util.List;
public interface GoodsReceiptService {
    ReceiptResponse create(CreateReceiptRequest request);
    List<GoodsReceiptWorkflowResponse> getDeliveredReceiptWorkflows();
    GoodsReceiptWorkflowResponse getReceiptWorkflow(Long deliveryId);
    byte[] generatePdf(Long receiptId);
}
