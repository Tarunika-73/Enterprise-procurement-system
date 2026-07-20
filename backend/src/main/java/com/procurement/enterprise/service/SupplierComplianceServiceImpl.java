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

@Service
@RequiredArgsConstructor
public class SupplierComplianceServiceImpl implements SupplierComplianceService {

    private static final Logger log =
            LoggerFactory.getLogger(SupplierComplianceServiceImpl.class);

    private final SupplierComplianceRepository supplierComplianceRepository;
    private final VendorRepository vendorRepository;

    @Override
    @Transactional
    public SupplierComplianceResponse createCompliance(
            CreateSupplierComplianceRequest request) {

        Vendor vendor = vendorRepository
                .findByIdAndIsDeletedFalse(request.getVendorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vendor", request.getVendorId()));

        SupplierCompliance compliance = SupplierCompliance.builder()
                .vendor(vendor)
                .documentType(request.getDocumentType().trim())
                .documentUrl(request.getDocumentUrl().trim())
                .expiryDate(request.getExpiryDate())
                .status(request.getStatus() == null
                        ? "Valid"
                        : request.getStatus())
                .isDeleted(false)
                .build();

        SupplierCompliance saved =
                supplierComplianceRepository.save(compliance);

        log.info("Created supplier compliance record with id: {}",
                saved.getId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierComplianceResponse getComplianceById(Long id) {

        SupplierCompliance compliance = findComplianceById(id);

        return mapToResponse(compliance);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierComplianceResponse> getAllCompliance(
            Pageable pageable) {

        return supplierComplianceRepository
                .findAllByIsDeletedFalse(pageable)
                .map(this::mapToResponse);
    }
        @Override
    @Transactional
    public SupplierComplianceResponse updateCompliance(
            Long id,
            UpdateSupplierComplianceRequest request) {

        SupplierCompliance compliance = findComplianceById(id);

        if (request.getDocumentType() != null) {
            compliance.setDocumentType(request.getDocumentType().trim());
        }

        if (request.getDocumentUrl() != null) {
            compliance.setDocumentUrl(request.getDocumentUrl().trim());
        }

        if (request.getExpiryDate() != null) {
            compliance.setExpiryDate(request.getExpiryDate());
        }

        if (request.getStatus() != null) {
            compliance.setStatus(request.getStatus());
        }

        SupplierCompliance updated =
                supplierComplianceRepository.save(compliance);

        log.info("Updated supplier compliance with id: {}",
                updated.getId());

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCompliance(Long id) {

        SupplierCompliance compliance = findComplianceById(id);

        compliance.setIsDeleted(true);

        supplierComplianceRepository.save(compliance);

        log.info("Deleted supplier compliance with id: {}", id);
    }
        private SupplierCompliance findComplianceById(Long id) {

        return supplierComplianceRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Supplier Compliance", id));
    }

    private SupplierComplianceResponse mapToResponse(
            SupplierCompliance compliance) {

        return SupplierComplianceResponse.builder()
                .id(compliance.getId())
                .vendorId(compliance.getVendor().getId())
                .vendorName(compliance.getVendor().getVendorName())
                .documentType(compliance.getDocumentType())
                .documentUrl(compliance.getDocumentUrl())
                .expiryDate(compliance.getExpiryDate())
                .status(compliance.getStatus())
                .createdAt(compliance.getCreatedAt())
                .updatedAt(compliance.getUpdatedAt())
                .build();
    }
}