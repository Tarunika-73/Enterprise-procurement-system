package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateVendorRequest;
import com.procurement.enterprise.dto.request.UpdateVendorRequest;
import com.procurement.enterprise.dto.response.VendorResponse;
import com.procurement.enterprise.entity.Vendor;
import com.procurement.enterprise.exception.DuplicateResourceException;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Business logic implementation for vendor registration and profile management.
 */
@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private static final Logger log = LoggerFactory.getLogger(VendorServiceImpl.class);

    private final VendorRepository vendorRepository;

    @Override
    @Transactional
    public VendorResponse createVendor(CreateVendorRequest request) {
        validateVendorName(request.getVendorName());
        validateUniqueEmail(request.getEmail());
        validateUniqueGst(request.getGstNumber());

        Vendor vendor = Vendor.builder()
                .vendorName(request.getVendorName().trim())
                .contactName(request.getContactName())
                .email(request.getEmail().trim().toLowerCase())
                .phone(request.getPhone())
                .address(request.getAddress())
                .gstNumber(request.getGstNumber() != null ? request.getGstNumber().trim().toUpperCase() : null)
                .isActive(true)
                .isDeleted(false)
                .build();

        Vendor savedVendor = vendorRepository.save(vendor);
        log.info("Created vendor with id: {}", savedVendor.getId());
        return mapToResponse(savedVendor);
    }

    @Override
    @Transactional
    public VendorResponse updateVendor(Long id, UpdateVendorRequest request) {
        Vendor vendor = findActiveVendorById(id);

        if (request.getVendorName() != null) {
            validateVendorName(request.getVendorName());
            vendor.setVendorName(request.getVendorName().trim());
        }

        if (request.getEmail() != null) {
            String normalizedEmail = request.getEmail().trim().toLowerCase();
            if (!Objects.equals(vendor.getEmail(), normalizedEmail) && vendorRepository.existsByEmailAndIsDeletedFalse(normalizedEmail)) {
                throw new DuplicateResourceException("Vendor", "email", normalizedEmail);
            }
            vendor.setEmail(normalizedEmail);
        }

        if (request.getGstNumber() != null) {
            String normalizedGst = request.getGstNumber().trim().toUpperCase();
            if (!Objects.equals(vendor.getGstNumber(), normalizedGst) && vendorRepository.existsByGstNumberAndIsDeletedFalse(normalizedGst)) {
                throw new DuplicateResourceException("Vendor", "gst number", normalizedGst);
            }
            vendor.setGstNumber(normalizedGst);
        }

        if (request.getContactName() != null) {
            vendor.setContactName(request.getContactName());
        }
        if (request.getPhone() != null) {
            vendor.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            vendor.setAddress(request.getAddress());
        }
        if (request.getIsActive() != null) {
            vendor.setIsActive(request.getIsActive());
        }

        Vendor updatedVendor = vendorRepository.save(vendor);
        log.info("Updated vendor with id: {}", updatedVendor.getId());
        return mapToResponse(updatedVendor);
    }

    @Override
    @Transactional
    public void deleteVendor(Long id) {
        Vendor vendor = findActiveVendorById(id);
        if (Boolean.TRUE.equals(vendor.getIsActive())) {
            throw new InvalidRequestException("Cannot delete an active vendor. Deactivate the vendor first.");
        }

        vendor.setIsDeleted(true);
        vendorRepository.save(vendor);
        log.info("Soft deleted vendor with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponse getVendorById(Long id) {
        Vendor vendor = findActiveVendorById(id);
        return mapToResponse(vendor);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VendorResponse> getAllVendors(Pageable pageable) {
        return vendorRepository.findAllByIsDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    private Vendor findActiveVendorById(Long id) {
        return vendorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", id));
    }

    private void validateVendorName(String vendorName) {
        if (vendorName == null || vendorName.trim().isEmpty()) {
            throw new InvalidRequestException("Vendor name cannot be blank");
        }
    }

    private void validateUniqueEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        String normalizedEmail = email.trim().toLowerCase();
        if (vendorRepository.existsByEmailAndIsDeletedFalse(normalizedEmail)) {
            throw new DuplicateResourceException("Vendor", "email", normalizedEmail);
        }
    }

    private void validateUniqueGst(String gstNumber) {
        if (gstNumber == null || gstNumber.trim().isEmpty()) {
            return;
        }
        String normalizedGst = gstNumber.trim().toUpperCase();
        if (vendorRepository.existsByGstNumberAndIsDeletedFalse(normalizedGst)) {
            throw new DuplicateResourceException("Vendor", "gst number", normalizedGst);
        }
    }

    private VendorResponse mapToResponse(Vendor vendor) {
        return VendorResponse.builder()
                .id(vendor.getId())
                .vendorName(vendor.getVendorName())
                .contactName(vendor.getContactName())
                .email(vendor.getEmail())
                .phone(vendor.getPhone())
                .address(vendor.getAddress())
                .gstNumber(vendor.getGstNumber())
                .isActive(vendor.getIsActive())
                .createdAt(vendor.getCreatedAt())
                .updatedAt(vendor.getUpdatedAt())
                .build();
    }
}
