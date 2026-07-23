package com.procurement.enterprise.service;
import com.procurement.enterprise.dto.purchaserequisition.PurchaseRequisitionRequest;
import com.procurement.enterprise.dto.purchaserequisition.PurchaseRequisitionResponse;
import com.procurement.enterprise.dto.purchaserequisition.PurchaseRequisitionUpdateRequest;
import com.procurement.enterprise.entity.PurchaseRequisition;
import com.procurement.enterprise.enums.RequisitionStatus;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.PurchaseRequisitionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class PurchaseRequisitionServiceImpl implements PurchaseRequisitionService {

    private static final Logger log =
            LoggerFactory.getLogger(PurchaseRequisitionServiceImpl.class);

    private final PurchaseRequisitionRepository purchaseRequisitionRepository;

    @Override
    @Transactional
    public PurchaseRequisitionResponse createPurchaseRequisition(PurchaseRequisitionRequest request) {

        PurchaseRequisition requisition = PurchaseRequisition.builder()
                .requestNumber(generateRequestNumber())
                .employeeId(request.getEmployeeId())
                .departmentId(request.getDepartmentId())
                .categoryId(request.getCategoryId())
                .description(request.getDescription())
                .quantity(request.getQuantity())
                .estimatedAmount(request.getEstimatedAmount())
                .priority(request.getPriority())
                .status(RequisitionStatus.DRAFT)
                .isDeleted(false)
                .build();

        PurchaseRequisition saved =
                purchaseRequisitionRepository.save(requisition);

        log.info("Purchase Requisition created with id {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseRequisitionResponse> getAllPurchaseRequisitions(Pageable pageable) {

        return purchaseRequisitionRepository
                .findAllByIsDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseRequisitionResponse getPurchaseRequisitionById(Long id) {

        return mapToResponse(findActiveRequisition(id));
    }

    @Override
    @Transactional
    public PurchaseRequisitionResponse updatePurchaseRequisition(
            Long id,
            PurchaseRequisitionUpdateRequest request) {

        PurchaseRequisition requisition = findActiveRequisition(id);

        if (requisition.getStatus() != RequisitionStatus.DRAFT) {
            throw new InvalidRequestException(
                    "Only draft requisitions can be updated.");
        }

        requisition.setDepartmentId(request.getDepartmentId());
        requisition.setCategoryId(request.getCategoryId());
        requisition.setDescription(request.getDescription());
        requisition.setQuantity(request.getQuantity());
        requisition.setEstimatedAmount(request.getEstimatedAmount());
        requisition.setPriority(request.getPriority());

        PurchaseRequisition updated =
                purchaseRequisitionRepository.save(requisition);

        log.info("Purchase Requisition updated with id {}", updated.getId());

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deletePurchaseRequisition(Long id) {

        PurchaseRequisition requisition = findActiveRequisition(id);

        if (requisition.getStatus() != RequisitionStatus.DRAFT) {
            throw new InvalidRequestException(
                    "Only draft requisitions can be deleted.");
        }

        requisition.setIsDeleted(true);

        purchaseRequisitionRepository.save(requisition);

        log.info("Purchase Requisition deleted with id {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseRequisitionResponse> searchPurchaseRequisitions(
            String keyword,
            Pageable pageable) {

        return purchaseRequisitionRepository
                .findByDescriptionContainingIgnoreCaseAndIsDeletedFalse(keyword, pageable)
                .map(this::mapToResponse);
    }

    private PurchaseRequisition findActiveRequisition(Long id) {

        return purchaseRequisitionRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Purchase Requisition",
                                id));
    }

    private PurchaseRequisitionResponse mapToResponse(PurchaseRequisition requisition) {

        return PurchaseRequisitionResponse.builder()
                .id(requisition.getId())
                .requestNumber(requisition.getRequestNumber())
                .employeeId(requisition.getEmployeeId())
                .departmentId(requisition.getDepartmentId())
                .categoryId(requisition.getCategoryId())
                .description(requisition.getDescription())
                .quantity(requisition.getQuantity())
                .estimatedAmount(requisition.getEstimatedAmount())
                .priority(requisition.getPriority())
                .status(requisition.getStatus())
                .currentApproverId(requisition.getCurrentApproverId())
                .createdAt(requisition.getCreatedAt())
                .updatedAt(requisition.getUpdatedAt())
                .build();
    }

    private String generateRequestNumber() {

        long count = purchaseRequisitionRepository.count() + 1;

        return String.format(
                "PR-%d-%04d",
                Year.now().getValue(),
                count);
    }
}