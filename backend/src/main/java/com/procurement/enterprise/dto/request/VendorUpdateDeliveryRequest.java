package com.procurement.enterprise.dto.request;

import com.procurement.enterprise.enums.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class VendorUpdateDeliveryRequest {

    @NotNull(message = "Purchase order ID is required")
    private Long purchaseOrderId;

    @NotNull(message = "Delivery status is required")
    private DeliveryStatus deliveryStatus;

    private String dispatchNumber;
    private LocalDate dispatchDate;
    private LocalDate expectedDeliveryDate;
    private String remarks;
}
