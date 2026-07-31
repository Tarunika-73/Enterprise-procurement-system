package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.approvalhierarchy.ApprovalHierarchyLevelRequest;
import com.procurement.enterprise.dto.approvalhierarchy.ApprovalHierarchyLevelResponse;
import com.procurement.enterprise.dto.approvalhierarchy.ApprovalHierarchyRequest;
import com.procurement.enterprise.dto.approvalhierarchy.ApprovalHierarchyResponse;
import com.procurement.enterprise.entity.ApprovalHierarchy;
import com.procurement.enterprise.entity.ApprovalHierarchyLevel;
import com.procurement.enterprise.entity.Department;
import com.procurement.enterprise.entity.Role;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.ApprovalHierarchyRepository;
import com.procurement.enterprise.repository.DepartmentRepository;
import com.procurement.enterprise.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ApprovalHierarchyServiceImpl implements ApprovalHierarchyService {

    private static final Logger log =
            LoggerFactory.getLogger(ApprovalHierarchyServiceImpl.class);

    private final ApprovalHierarchyRepository approvalHierarchyRepository;

    private final DepartmentRepository departmentRepository;

    private final RoleRepository roleRepository;

    @Override
    public ApprovalHierarchyResponse createHierarchy(ApprovalHierarchyRequest request) {

        validateAmountRange(request.getMinAmount(), request.getMaxAmount());
        validateLevels(request.getLevels());

        Department department = resolveDepartment(request.getDepartmentId());

        ApprovalHierarchy hierarchy = ApprovalHierarchy.builder()
                .name(request.getName())
                .department(department)
                .minAmount(request.getMinAmount())
                .maxAmount(request.getMaxAmount())
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .isDeleted(false)
                .build();

        attachLevels(hierarchy, request.getLevels());

        ApprovalHierarchy saved = approvalHierarchyRepository.save(hierarchy);

        log.info("Approval hierarchy '{}' created with ID {}", saved.getName(), saved.getId());

        return mapToResponse(saved);
    }

    @Override
    public ApprovalHierarchyResponse updateHierarchy(Long id, ApprovalHierarchyRequest request) {

        validateAmountRange(request.getMinAmount(), request.getMaxAmount());
        validateLevels(request.getLevels());

        ApprovalHierarchy hierarchy = getActiveHierarchy(id);

        Department department = resolveDepartment(request.getDepartmentId());

        hierarchy.setName(request.getName());
        hierarchy.setDepartment(department);
        hierarchy.setMinAmount(request.getMinAmount());
        hierarchy.setMaxAmount(request.getMaxAmount());
        hierarchy.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        hierarchy.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        // Replace the level chain entirely (orphanRemoval clears the old rows).
        hierarchy.getLevels().clear();
        attachLevels(hierarchy, request.getLevels());

        ApprovalHierarchy saved = approvalHierarchyRepository.save(hierarchy);

        log.info("Approval hierarchy {} updated", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ApprovalHierarchyResponse getHierarchyById(Long id) {
        return mapToResponse(getActiveHierarchy(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalHierarchyResponse> getAllHierarchies(Pageable pageable) {
        return approvalHierarchyRepository
                .findAllByIsDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public void deleteHierarchy(Long id) {
        ApprovalHierarchy hierarchy = getActiveHierarchy(id);
        hierarchy.setIsDeleted(true);
        hierarchy.setIsActive(false);
        approvalHierarchyRepository.save(hierarchy);
        log.info("Approval hierarchy {} soft-deleted", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ApprovalHierarchyResponse resolveApplicableHierarchy(Long departmentId, BigDecimal amount) {

        if (amount == null) {
            throw new InvalidRequestException("Amount is required to resolve an approval hierarchy");
        }

        return approvalHierarchyRepository
                .findApplicableHierarchies(departmentId, amount)
                .stream()
                .findFirst()
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active approval hierarchy configured for department ID "
                                + departmentId + " and amount " + amount
                ));
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    private ApprovalHierarchy getActiveHierarchy(Long id) {
        return approvalHierarchyRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("ApprovalHierarchy", id));
    }

    private Department resolveDepartment(Long departmentId) {

        if (departmentId == null) {
            return null;
        }

        return departmentRepository
                .findByIdAndIsDeletedFalse(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));
    }

    private void validateAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {

        if (minAmount == null) {
            throw new InvalidRequestException("Minimum amount is required");
        }

        if (minAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRequestException("Minimum amount cannot be negative");
        }

        if (maxAmount != null && maxAmount.compareTo(minAmount) < 0) {
            throw new InvalidRequestException("Maximum amount cannot be less than minimum amount");
        }
    }

    private void validateLevels(List<ApprovalHierarchyLevelRequest> levels) {

        if (levels == null || levels.isEmpty()) {
            throw new InvalidRequestException("At least one approval level is required");
        }

        Set<Integer> seenLevels = new HashSet<>();

        List<Integer> sorted = levels.stream()
                .map(ApprovalHierarchyLevelRequest::getLevelNumber)
                .sorted()
                .toList();

        for (int i = 0; i < sorted.size(); i++) {

            if (!seenLevels.add(sorted.get(i))) {
                throw new InvalidRequestException("Duplicate level number: " + sorted.get(i));
            }

            if (sorted.get(i) != i + 1) {
                throw new InvalidRequestException(
                        "Level numbers must be sequential starting at 1 (e.g. 1, 2, 3)"
                );
            }
        }
    }

    private void attachLevels(ApprovalHierarchy hierarchy, List<ApprovalHierarchyLevelRequest> levelRequests) {

        List<ApprovalHierarchyLevelRequest> ordered = levelRequests.stream()
                .sorted(Comparator.comparing(ApprovalHierarchyLevelRequest::getLevelNumber))
                .toList();

        List<ApprovalHierarchyLevel> levels = new ArrayList<>();

        for (ApprovalHierarchyLevelRequest levelRequest : ordered) {

            Role role = roleRepository
                    .findByIdAndIsDeletedFalse(levelRequest.getApproverRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role", levelRequest.getApproverRoleId()));

            ApprovalHierarchyLevel level = ApprovalHierarchyLevel.builder()
                    .approvalHierarchy(hierarchy)
                    .levelNumber(levelRequest.getLevelNumber())
                    .approverRole(role)
                    .isDeleted(false)
                    .build();

            levels.add(level);
        }

        hierarchy.getLevels().addAll(levels);
    }

    private ApprovalHierarchyResponse mapToResponse(ApprovalHierarchy hierarchy) {

        List<ApprovalHierarchyLevelResponse> levelResponses = hierarchy.getLevels().stream()
                .filter(level -> !Boolean.TRUE.equals(level.getIsDeleted()))
                .sorted(Comparator.comparing(ApprovalHierarchyLevel::getLevelNumber))
                .map(level -> ApprovalHierarchyLevelResponse.builder()
                        .id(level.getId())
                        .levelNumber(level.getLevelNumber())
                        .approverRoleId(level.getApproverRole().getId())
                        .approverRoleName(level.getApproverRole().getName())
                        .build())
                .toList();

        return ApprovalHierarchyResponse.builder()
                .id(hierarchy.getId())
                .name(hierarchy.getName())
                .departmentId(hierarchy.getDepartment() != null ? hierarchy.getDepartment().getId() : null)
                .departmentName(hierarchy.getDepartment() != null ? hierarchy.getDepartment().getName() : "All Departments")
                .minAmount(hierarchy.getMinAmount())
                .maxAmount(hierarchy.getMaxAmount())
                .priority(hierarchy.getPriority())
                .isActive(hierarchy.getIsActive())
                .levels(levelResponses)
                .createdAt(hierarchy.getCreatedAt())
                .updatedAt(hierarchy.getUpdatedAt())
                .build();
    }
}
