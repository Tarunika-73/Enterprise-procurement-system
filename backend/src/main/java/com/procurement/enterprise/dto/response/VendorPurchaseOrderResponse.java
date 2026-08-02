package com.procurement.enterprise.dto.response;

import com.procurement.enterprise.enums.PurchaseOrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class VendorPurchaseOrderResponse {
    private Long id;
    private String purchaseOrderNumber;
    private String departmentName;
    private String procurementOfficerName;
    private String deliveryAddress;
    private LocalDate expectedDeliveryDate;
    private BigDecimal totalAmount;
    private PurchaseOrderStatus status;
    private LocalDateTime createdAt;
    private List<PurchaseOrderItemResponse> items;
}
