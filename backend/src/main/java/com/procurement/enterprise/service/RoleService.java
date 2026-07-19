package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateRoleRequest;
import com.procurement.enterprise.dto.request.UpdateRoleRequest;
import com.procurement.enterprise.dto.response.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Service interface for Role management. */
public interface RoleService {

    RoleResponse create(CreateRoleRequest request);

    RoleResponse update(Long id, UpdateRoleRequest request);

    void delete(Long id);

    RoleResponse getById(Long id);

    Page<RoleResponse> getAll(Pageable pageable);

    RoleResponse getByName(String name);
}
