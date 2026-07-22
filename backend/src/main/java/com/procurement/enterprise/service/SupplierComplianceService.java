package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateSupplierComplianceRequest;
import com.procurement.enterprise.dto.request.UpdateSupplierComplianceRequest;
import com.procurement.enterprise.dto.response.SupplierComplianceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierComplianceService {

    SupplierComplianceResponse create(CreateSupplierComplianceRequest request);

    SupplierComplianceResponse update(
            Long id,
            UpdateSupplierComplianceRequest request
    );

    void delete(Long id);

    SupplierComplianceResponse getById(Long id);

    Page<SupplierComplianceResponse> getAll(Pageable pageable);

    Page<SupplierComplianceResponse> getByVendor(
            Long vendorId,
            Pageable pageable
    );

    Page<SupplierComplianceResponse> getByStatus(
            String status,
            Pageable pageable
    );

    Page<SupplierComplianceResponse> getExpired(Pageable pageable);
}