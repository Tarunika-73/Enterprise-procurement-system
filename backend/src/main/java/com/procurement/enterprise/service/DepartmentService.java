package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateDepartmentRequest;
import com.procurement.enterprise.dto.request.UpdateDepartmentRequest;
import com.procurement.enterprise.dto.response.DepartmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Service interface for Department management. */
public interface DepartmentService {

    DepartmentResponse create(CreateDepartmentRequest request);

    DepartmentResponse update(Long id, UpdateDepartmentRequest request);

    void delete(Long id);

    DepartmentResponse getById(Long id);

    Page<DepartmentResponse> getAll(Pageable pageable);

    DepartmentResponse getByCode(String code);
}
