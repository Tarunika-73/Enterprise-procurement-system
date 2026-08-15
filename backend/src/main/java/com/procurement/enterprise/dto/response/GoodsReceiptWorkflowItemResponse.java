package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class GoodsReceiptWorkflowItemResponse {
    private Long productId;
    private String productName;
    private String productSku;
    private Integer orderedQuantity;
    private Integer deliveredQuantity;
    private BigDecimal unitPrice;
}
