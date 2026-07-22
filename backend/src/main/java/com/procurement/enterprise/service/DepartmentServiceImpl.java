package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateDepartmentRequest;
import com.procurement.enterprise.dto.request.UpdateDepartmentRequest;
import com.procurement.enterprise.dto.response.DepartmentResponse;
import com.procurement.enterprise.entity.Department;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.exception.DuplicateResourceException;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.DepartmentRepository;
import com.procurement.enterprise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public DepartmentResponse create(CreateDepartmentRequest request) {

        validate(request.getName(), request.getCode());

        String name = request.getName().trim();
        String code = request.getCode().trim();

        if (departmentRepository.existsByNameAndIsDeletedFalse(name)) {
            throw new DuplicateResourceException(
                    "Department",
                    "name",
                    name
            );
        }

        if (departmentRepository.existsByCodeAndIsDeletedFalse(code)) {
            throw new DuplicateResourceException(
                    "Department",
                    "code",
                    code
            );
        }

        Department department = Department.builder()
                .name(name)
                .code(code)
                .isDeleted(false)
                .build();

        if (request.getManagerId() != null) {

            User manager = userRepository
                    .findByIdAndIsDeletedFalse(request.getManagerId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User",
                                    request.getManagerId()));

            department.setManager(manager);
        }

        Department saved = departmentRepository.save(department);

        log.info("Created Department {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long id, UpdateDepartmentRequest request) {

        Department department = findDepartment(id);

        if (request.getName() != null) {

            String name = request.getName().trim();

            if (name.isBlank()) {
                throw new InvalidRequestException(
                        "Department name cannot be blank");
            }

            if (!Objects.equals(department.getName(), name)
                    && departmentRepository.existsByNameAndIsDeletedFalse(name)) {

                throw new DuplicateResourceException(
                        "Department",
                        "name",
                        name);
            }

            department.setName(name);
        }

        if (request.getCode() != null) {

            String code = request.getCode().trim();

            if (code.isBlank()) {
                throw new InvalidRequestException(
                        "Department code cannot be blank");
            }

            if (!Objects.equals(department.getCode(), code)
                    && departmentRepository.existsByCodeAndIsDeletedFalse(code)) {

                throw new DuplicateResourceException(
                        "Department",
                        "code",
                        code);
            }

            department.setCode(code);
        }

        if (request.getManagerId() != null) {

            User manager = userRepository
                    .findByIdAndIsDeletedFalse(request.getManagerId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User",
                                    request.getManagerId()));

            department.setManager(manager);
        }

        Department updated = departmentRepository.save(department);

        log.info("Updated Department {}", updated.getId());

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Department department = findDepartment(id);

        department.setIsDeleted(true);

        departmentRepository.save(department);

        log.info("Deleted Department {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getById(Long id) {

        return mapToResponse(findDepartment(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAll(Pageable pageable) {

        return departmentRepository
                .findAllByIsDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getByCode(String code) {

        Department department = departmentRepository
                .findByCodeAndIsDeletedFalse(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department",
                                "code",
                                code));

        return mapToResponse(department);
    }

    private Department findDepartment(Long id) {

        return departmentRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department",
                                id));
    }

    private void validate(String name, String code) {

        if (name == null || name.trim().isEmpty()) {
            throw new InvalidRequestException(
                    "Department name cannot be blank");
        }

        if (code == null || code.trim().isEmpty()) {
            throw new InvalidRequestException(
                    "Department code cannot be blank");
        }
    }

    private DepartmentResponse mapToResponse(Department department) {

        Long managerId = null;
        String managerName = null;

        if (department.getManager() != null) {

            managerId = department.getManager().getId();
            managerName = department.getManager().getFirstName()
                    + " "
                    + department.getManager().getLastName();
        }

        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .managerId(managerId)
                .managerName(managerName)
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }
}