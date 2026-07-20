package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateSupplierComplianceRequest;
import com.procurement.enterprise.dto.request.UpdateSupplierComplianceRequest;
import com.procurement.enterprise.dto.response.SupplierComplianceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierComplianceService {

    SupplierComplianceResponse createCompliance(CreateSupplierComplianceRequest request);

    SupplierComplianceResponse updateCompliance(Long id,
                                                UpdateSupplierComplianceRequest request);

    void deleteCompliance(Long id);

    SupplierComplianceResponse getComplianceById(Long id);

    Page<SupplierComplianceResponse> getAllCompliance(Pageable pageable);
}