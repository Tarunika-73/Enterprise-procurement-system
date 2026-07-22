package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateSupplierComplianceRequest;
import com.procurement.enterprise.dto.request.UpdateSupplierComplianceRequest;
import com.procurement.enterprise.dto.response.SupplierComplianceResponse;
import com.procurement.enterprise.entity.SupplierCompliance;
import com.procurement.enterprise.entity.Vendor;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.SupplierComplianceRepository;
import com.procurement.enterprise.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SupplierComplianceServiceImpl implements SupplierComplianceService {

    private static final Logger log =
            LoggerFactory.getLogger(SupplierComplianceServiceImpl.class);

    private final SupplierComplianceRepository supplierComplianceRepository;
    private final VendorRepository vendorRepository;

    @Override
    @Transactional
    public SupplierComplianceResponse create(
            CreateSupplierComplianceRequest request) {

        Vendor vendor = vendorRepository
                .findByIdAndIsDeletedFalse(request.getVendorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vendor",
                                request.getVendorId()
                        ));

        SupplierCompliance compliance = SupplierCompliance.builder()
                .vendor(vendor)
                .documentType(request.getDocumentType().trim())
                .documentUrl(request.getDocumentUrl().trim())
                .expiryDate(request.getExpiryDate())
                .status(
                        request.getStatus() != null
                                && !request.getStatus().trim().isEmpty()
                                ? request.getStatus().trim()
                                : "Valid"
                )
                .isDeleted(false)
                .build();

        SupplierCompliance saved =
                supplierComplianceRepository.save(compliance);

        log.info(
                "Created supplier compliance record with id: {}",
                saved.getId()
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public SupplierComplianceResponse update(
            Long id,
            UpdateSupplierComplianceRequest request) {

        SupplierCompliance compliance = findById(id);

        if (request.getDocumentType() != null) {
            compliance.setDocumentType(
                    request.getDocumentType().trim()
            );
        }

        if (request.getDocumentUrl() != null) {
            compliance.setDocumentUrl(
                    request.getDocumentUrl().trim()
            );
        }

        if (request.getExpiryDate() != null) {
            compliance.setExpiryDate(
                    request.getExpiryDate()
            );
        }

        if (request.getStatus() != null) {
            compliance.setStatus(
                    request.getStatus().trim()
            );
        }

        SupplierCompliance updated =
                supplierComplianceRepository.save(compliance);

        log.info(
                "Updated supplier compliance record with id: {}",
                updated.getId()
        );

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        SupplierCompliance compliance = findById(id);

        compliance.setIsDeleted(true);

        supplierComplianceRepository.save(compliance);

        log.info(
                "Soft deleted supplier compliance record with id: {}",
                id
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierComplianceResponse getById(Long id) {

        return mapToResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierComplianceResponse> getAll(
            Pageable pageable) {

        return supplierComplianceRepository
                .findAllByIsDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierComplianceResponse> getByVendor(
            Long vendorId,
            Pageable pageable) {

        // Make sure the vendor exists before filtering.
        vendorRepository
                .findByIdAndIsDeletedFalse(vendorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vendor",
                                vendorId
                        ));

        return supplierComplianceRepository
                .findByVendorIdAndIsDeletedFalse(
                        vendorId,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierComplianceResponse> getByStatus(
            String status,
            Pageable pageable) {

        return supplierComplianceRepository
                .findByStatusAndIsDeletedFalse(
                        status,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierComplianceResponse> getExpired(
            Pageable pageable) {

        return supplierComplianceRepository
                .findByExpiryDateBeforeAndIsDeletedFalse(
                        LocalDate.now(),
                        pageable
                )
                .map(this::mapToResponse);
    }

    private SupplierCompliance findById(Long id) {

        return supplierComplianceRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Supplier Compliance",
                                id
                        ));
    }

    private SupplierComplianceResponse mapToResponse(
            SupplierCompliance compliance) {

        Vendor vendor = compliance.getVendor();

        return SupplierComplianceResponse.builder()
                .id(compliance.getId())
                .vendorId(
                        vendor != null
                                ? vendor.getId()
                                : null
                )
                .vendorName(
                        vendor != null
                                ? vendor.getVendorName()
                                : null
                )
                .documentType(
                        compliance.getDocumentType()
                )
                .documentUrl(
                        compliance.getDocumentUrl()
                )
                .expiryDate(
                        compliance.getExpiryDate()
                )
                .status(
                        compliance.getStatus()
                )
                .createdAt(
                        compliance.getCreatedAt()
                )
                .updatedAt(
                        compliance.getUpdatedAt()
                )
                .build();
    }
}