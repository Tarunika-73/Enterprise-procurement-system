package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.VendorUpdateDeliveryRequest;
import com.procurement.enterprise.dto.response.DeliveryResponse;
import com.procurement.enterprise.dto.response.VendorDashboardResponse;
import com.procurement.enterprise.dto.response.VendorPurchaseOrderResponse;
import com.procurement.enterprise.dto.response.VendorResponse;
import com.procurement.enterprise.enums.PurchaseOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Vendor-facing portal operations.
 * Vendors may only view their own POs and update delivery information.
 */
public interface VendorPortalService {

    VendorDashboardResponse getDashboard(String vendorEmail);

    Page<VendorPurchaseOrderResponse> getPurchaseOrders(String vendorEmail, PurchaseOrderStatus status, Pageable pageable);

    VendorPurchaseOrderResponse getPurchaseOrderDetail(String vendorEmail, Long purchaseOrderId);

    VendorPurchaseOrderResponse acceptOrder(String vendorEmail, Long purchaseOrderId);

    VendorPurchaseOrderResponse rejectOrder(String vendorEmail, Long purchaseOrderId, String remarks);

    DeliveryResponse updateDelivery(String vendorEmail, VendorUpdateDeliveryRequest request);

    VendorResponse getProfile(String vendorEmail);

    VendorResponse updateProfile(String vendorEmail, String contactName, String phone, String address);
}
