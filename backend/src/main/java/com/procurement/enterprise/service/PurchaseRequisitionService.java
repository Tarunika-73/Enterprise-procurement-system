package com.procurement.enterprise.service;
import com.procurement.enterprise.dto.purchaserequisition.PurchaseRequisitionRequest;
import com.procurement.enterprise.dto.purchaserequisition.PurchaseRequisitionUpdateRequest;
import com.procurement.enterprise.dto.purchaserequisition.PurchaseRequisitionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseRequisitionService {

    PurchaseRequisitionResponse createPurchaseRequisition(PurchaseRequisitionRequest request);

    Page<PurchaseRequisitionResponse> getAllPurchaseRequisitions(Pageable pageable);

    PurchaseRequisitionResponse getPurchaseRequisitionById(Long id);

    PurchaseRequisitionResponse updatePurchaseRequisition(
            Long id,
            PurchaseRequisitionUpdateRequest request);

    void deletePurchaseRequisition(Long id);

    Page<PurchaseRequisitionResponse> searchPurchaseRequisitions(
            String keyword,
            Pageable pageable);
}