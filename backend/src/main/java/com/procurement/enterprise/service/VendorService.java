package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateVendorRequest;
import com.procurement.enterprise.dto.request.UpdateVendorRequest;
import com.procurement.enterprise.dto.response.VendorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for vendor management operations.
 */
public interface VendorService {

    VendorResponse createVendor(CreateVendorRequest request);

    VendorResponse updateVendor(Long id, UpdateVendorRequest request);

    void deleteVendor(Long id);

    VendorResponse getVendorById(Long id);

    Page<VendorResponse> getAllVendors(Pageable pageable);
}
