package com.procurement.enterprise.dto.response;

import com.procurement.enterprise.enums.DeliveryStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class GoodsReceiptWorkflowResponse {
    private Long deliveryId;
    private Long purchaseOrderId;
    private String purchaseOrderNumber;
    private String vendorName;
    private LocalDate deliveryDate;
    private DeliveryStatus deliveryStatus;
    private boolean receiptCreated;
    private Long receiptId;
    private String receiverName;
    private LocalDate receiptDate;
    private String conditionNotes;
    private List<GoodsReceiptWorkflowItemResponse> items;
}
