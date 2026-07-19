package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateUserRequest;
import com.procurement.enterprise.dto.request.UpdateUserRequest;
import com.procurement.enterprise.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Service interface for User management. */
public interface UserService {

    UserResponse create(CreateUserRequest request);

    UserResponse update(Long id, UpdateUserRequest request);

    void delete(Long id);

    UserResponse getById(Long id);

    UserResponse getByEmail(String email);

    Page<UserResponse> getAll(Pageable pageable);

    Page<UserResponse> getByDepartment(Long departmentId, Pageable pageable);

    Page<UserResponse> getByRole(Long roleId, Pageable pageable);

    Page<UserResponse> search(String name, Pageable pageable);

    UserResponse activate(Long id);

    UserResponse deactivate(Long id);

    void changePassword(Long id, String currentPassword, String newPassword);
}
