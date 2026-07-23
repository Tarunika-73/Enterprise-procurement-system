package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateApprovalHierarchyRequest;
import com.procurement.enterprise.dto.request.UpdateApprovalHierarchyRequest;
import com.procurement.enterprise.dto.response.ApprovalHierarchyResponse;
import com.procurement.enterprise.entity.ApprovalHierarchy;
import com.procurement.enterprise.entity.Department;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.exception.DuplicateResourceException;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.ApprovalHierarchyRepository;
import com.procurement.enterprise.repository.DepartmentRepository;
import com.procurement.enterprise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ApprovalHierarchyServiceImpl implements ApprovalHierarchyService {

    private static final Logger log =
            LoggerFactory.getLogger(ApprovalHierarchyServiceImpl.class);

    private final ApprovalHierarchyRepository approvalHierarchyRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ApprovalHierarchyResponse create(CreateApprovalHierarchyRequest request) {

        if (request.getDepartmentId() == null) {
            throw new InvalidRequestException("Department ID is required");
        }

        if (request.getLevel() == null || request.getLevel() < 1) {
            throw new InvalidRequestException("Level must be a positive integer");
        }

        if (request.getApproverId() == null) {
            throw new InvalidRequestException("Approver ID is required");
        }

        Department department = departmentRepository
                .findByIdAndIsDeletedFalse(request.getDepartmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department",
                                request.getDepartmentId()));

        User approver = userRepository
                .findByIdAndIsDeletedFalse(request.getApproverId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                request.getApproverId()));

        if (Boolean.FALSE.equals(approver.getIsActive())) {
            throw new InvalidRequestException(
                    "Approver account is inactive and cannot be assigned to a hierarchy");
        }

        if (approvalHierarchyRepository.existsByDepartmentIdAndLevelAndIsDeletedFalse(
                request.getDepartmentId(), request.getLevel())) {
            throw new DuplicateResourceException(
                    "ApprovalHierarchy",
                    "level",
                    request.getLevel() + " for department " + department.getName());
        }

        ApprovalHierarchy hierarchy = ApprovalHierarchy.builder()
                .department(department)
                .level(request.getLevel())
                .approver(approver)
                .isDeleted(false)
                .build();

        ApprovalHierarchy saved = approvalHierarchyRepository.save(hierarchy);

        log.info("Created approval hierarchy level {} for department {}",
                saved.getLevel(), department.getId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ApprovalHierarchyResponse update(Long id, UpdateApprovalHierarchyRequest request) {

        ApprovalHierarchy hierarchy = findHierarchy(id);

        if (request.getLevel() != null) {

            if (!Objects.equals(hierarchy.getLevel(), request.getLevel())
                    && approvalHierarchyRepository.existsByDepartmentIdAndLevelAndIsDeletedFalse(
                            hierarchy.getDepartment().getId(), request.getLevel())) {

                throw new DuplicateResourceException(
                        "ApprovalHierarchy",
                        "level",
                        request.getLevel() + " for department " + hierarchy.getDepartment().getName());
            }

            hierarchy.setLevel(request.getLevel());
        }

        if (request.getApproverId() != null) {

            User approver = userRepository
                    .findByIdAndIsDeletedFalse(request.getApproverId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User",
                                    request.getApproverId()));

            if (Boolean.FALSE.equals(approver.getIsActive())) {
                throw new InvalidRequestException(
                        "Approver account is inactive and cannot be assigned to a hierarchy");
            }

            hierarchy.setApprover(approver);
        }

        ApprovalHierarchy updated = approvalHierarchyRepository.save(hierarchy);

        log.info("Updated approval hierarchy {}", updated.getId());

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        ApprovalHierarchy hierarchy = findHierarchy(id);

        hierarchy.setIsDeleted(true);

        approvalHierarchyRepository.save(hierarchy);

        log.info("Deleted approval hierarchy {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ApprovalHierarchyResponse getById(Long id) {

        return mapToResponse(findHierarchy(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalHierarchyResponse> getAll(Pageable pageable) {

        return approvalHierarchyRepository
                .findAllByIsDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalHierarchyResponse> getByDepartment(Long departmentId) {

        departmentRepository
                .findByIdAndIsDeletedFalse(departmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department", departmentId));

        return approvalHierarchyRepository
                .findByDepartmentIdAndIsDeletedFalseOrderByLevelAsc(departmentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ApprovalHierarchy findHierarchy(Long id) {

        return approvalHierarchyRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("ApprovalHierarchy", id));
    }

    private ApprovalHierarchyResponse mapToResponse(ApprovalHierarchy hierarchy) {

        Department department = hierarchy.getDepartment();
        User approver = hierarchy.getApprover();

        return ApprovalHierarchyResponse.builder()
                .id(hierarchy.getId())
                .departmentId(department != null ? department.getId() : null)
                .departmentName(department != null ? department.getName() : null)
                .level(hierarchy.getLevel())
                .approverId(approver != null ? approver.getId() : null)
                .approverName(approver != null
                        ? approver.getFirstName() + " " + approver.getLastName()
                        : null)
                .createdAt(hierarchy.getCreatedAt())
                .updatedAt(hierarchy.getUpdatedAt())
                .build();
    }
}
