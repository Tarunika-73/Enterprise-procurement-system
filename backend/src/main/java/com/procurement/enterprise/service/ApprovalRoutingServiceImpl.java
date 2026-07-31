package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.policyrule.CreatePolicyRuleRequest;
import com.procurement.enterprise.dto.policyrule.PolicyRuleResponse;
import com.procurement.enterprise.dto.policyrule.UpdatePolicyRuleRequest;
import com.procurement.enterprise.entity.ApprovalPolicyRule;
import com.procurement.enterprise.entity.Category;
import com.procurement.enterprise.entity.PurchaseRequisition;
import com.procurement.enterprise.enums.CategoryStatus;
import com.procurement.enterprise.enums.PolicyRuleType;
import com.procurement.enterprise.enums.RequisitionStatus;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.ApprovalPolicyRuleRepository;
import com.procurement.enterprise.repository.CategoryRepository;
import com.procurement.enterprise.repository.DepartmentRepository;
import com.procurement.enterprise.repository.PurchaseRequisitionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @see ApprovalPolicyService
 */
@Service
@RequiredArgsConstructor
public class ApprovalPolicyServiceImpl implements ApprovalPolicyService {

    private static final Logger log = LoggerFactory.getLogger(ApprovalPolicyServiceImpl.class);

    private final ApprovalPolicyRuleRepository approvalPolicyRuleRepository;

    private final CategoryRepository categoryRepository;

    private final DepartmentRepository departmentRepository;

    private final PurchaseRequisitionRepository purchaseRequisitionRepository;

    // =====================================================================
    // CRUD
    // =====================================================================

    @Override
    @Transactional
    public PolicyRuleResponse create(CreatePolicyRuleRequest request) {

        if (request.getRuleType() == null) {
            throw new InvalidRequestException("Rule type is required");
        }

        if (request.getDepartmentId() != null) {
            departmentRepository.findByIdAndIsDeletedFalse(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", request.getDepartmentId()));
        }

        validateRuleFields(request.getRuleType(), request.getThresholdAmount(), request.getRestrictedCategoryId());

        ApprovalPolicyRule rule = ApprovalPolicyRule.builder()
                .ruleType(request.getRuleType())
                .departmentId(request.getDepartmentId())
                .thresholdAmount(request.getThresholdAmount())
                .restrictedCategoryId(request.getRestrictedCategoryId())
                .description(request.getDescription())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .isDeleted(false)
                .build();

        ApprovalPolicyRule saved = approvalPolicyRuleRepository.save(rule);

        log.info("Created approval policy rule {} ({})", saved.getId(), saved.getRuleType());

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public PolicyRuleResponse update(Long id, UpdatePolicyRuleRequest request) {

        ApprovalPolicyRule rule = findRule(id);

        if (request.getRuleType() != null) {
            rule.setRuleType(request.getRuleType());
        }

        if (request.getDepartmentId() != null) {
            departmentRepository.findByIdAndIsDeletedFalse(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", request.getDepartmentId()));
            rule.setDepartmentId(request.getDepartmentId());
        }

        if (request.getThresholdAmount() != null) {
            rule.setThresholdAmount(request.getThresholdAmount());
        }

        if (request.getRestrictedCategoryId() != null) {
            rule.setRestrictedCategoryId(request.getRestrictedCategoryId());
        }

        if (request.getDescription() != null) {
            rule.setDescription(request.getDescription());
        }

        if (request.getIsActive() != null) {
            rule.setIsActive(request.getIsActive());
        }

        validateRuleFields(rule.getRuleType(), rule.getThresholdAmount(), rule.getRestrictedCategoryId());

        ApprovalPolicyRule updated = approvalPolicyRuleRepository.save(rule);

        log.info("Updated approval policy rule {}", updated.getId());

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        ApprovalPolicyRule rule = findRule(id);

        rule.setIsDeleted(true);

        approvalPolicyRuleRepository.save(rule);

        log.info("Deleted approval policy rule {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PolicyRuleResponse getById(Long id) {

        return mapToResponse(findRule(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PolicyRuleResponse> getAll(Pageable pageable) {

        return approvalPolicyRuleRepository
                .findAllByIsDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    // =====================================================================
    // Evaluation
    // =====================================================================

    @Override
    @Transactional(readOnly = true)
    public Optional<String> evaluate(PurchaseRequisition requisition) {

        // 1. Category INACTIVE is always a hard block, regardless of
        //    any configured rule.
        Category category = categoryRepository
                .findByIdAndIsDeletedFalse(requisition.getCategoryId())
                .orElse(null);

        if (category != null && category.getStatus() == CategoryStatus.INACTIVE) {
            return Optional.of(
                    "Category '" + category.getName() + "' is inactive and cannot be used for new procurement requests."
            );
        }

        // 2. Evaluate every applicable configured rule (department-scoped
        //    + organisation-wide), in ascending ID order, and return the
        //    first violation found.
        List<ApprovalPolicyRule> applicableRules =
                approvalPolicyRuleRepository.findApplicableRules(requisition.getDepartmentId());

        for (ApprovalPolicyRule rule : applicableRules) {

            Optional<String> violation = evaluateRule(rule, requisition);

            if (violation.isPresent()) {
                return violation;
            }
        }

        return Optional.empty();
    }

    private Optional<String> evaluateRule(ApprovalPolicyRule rule, PurchaseRequisition requisition) {

        switch (rule.getRuleType()) {

            case MAX_REQUISITION_AMOUNT -> {

                if (rule.getThresholdAmount() != null
                        && requisition.getEstimatedAmount() != null
                        && requisition.getEstimatedAmount().compareTo(rule.getThresholdAmount()) > 0) {

                    return Optional.of(
                            "Estimated amount " + requisition.getEstimatedAmount()
                                    + " exceeds the maximum allowed requisition amount of "
                                    + rule.getThresholdAmount() + "."
                    );
                }
            }

            case DEPARTMENT_BUDGET_CAP -> {

                if (rule.getThresholdAmount() != null && requisition.getEstimatedAmount() != null) {

                    BigDecimal committed = purchaseRequisitionRepository.sumCommittedAmountByDepartment(
                            requisition.getDepartmentId(),
                            java.util.List.of(RequisitionStatus.PENDING, RequisitionStatus.APPROVED),
                            requisition.getId() != null ? requisition.getId() : -1L
                    );

                    BigDecimal projected = committed.add(requisition.getEstimatedAmount());

                    if (projected.compareTo(rule.getThresholdAmount()) > 0) {

                        return Optional.of(
                                "Approving this requisition would bring department "
                                        + requisition.getDepartmentId() + "'s committed spend to "
                                        + projected + ", exceeding its budget cap of "
                                        + rule.getThresholdAmount() + "."
                        );
                    }
                }
            }

            case RESTRICTED_CATEGORY -> {

                if (rule.getRestrictedCategoryId() != null
                        && rule.getRestrictedCategoryId().equals(requisition.getCategoryId())) {

                    return Optional.of(
                            "Category ID " + rule.getRestrictedCategoryId()
                                    + " is restricted by policy and cannot be used for new procurement requests."
                    );
                }
            }

            default -> {
                // No-op: unknown/unhandled rule type is treated as a pass.
            }
        }

        return Optional.empty();
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    private void validateRuleFields(PolicyRuleType ruleType, BigDecimal thresholdAmount, Long restrictedCategoryId) {

        if (ruleType == null) {
            throw new InvalidRequestException("Rule type is required");
        }

        switch (ruleType) {

            case MAX_REQUISITION_AMOUNT, DEPARTMENT_BUDGET_CAP -> {

                if (thresholdAmount == null || thresholdAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new InvalidRequestException(
                            ruleType + " rules require a positive thresholdAmount."
                    );
                }
            }

            case RESTRICTED_CATEGORY -> {

                if (restrictedCategoryId == null) {
                    throw new InvalidRequestException(
                            "RESTRICTED_CATEGORY rules require restrictedCategoryId."
                    );
                }

                categoryRepository.findByIdAndIsDeletedFalse(restrictedCategoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category", restrictedCategoryId));
            }
        }
    }

    private ApprovalPolicyRule findRule(Long id) {

        return approvalPolicyRuleRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("ApprovalPolicyRule", id));
    }

    private PolicyRuleResponse mapToResponse(ApprovalPolicyRule rule) {

        return PolicyRuleResponse.builder()
                .id(rule.getId())
                .ruleType(rule.getRuleType())
                .departmentId(rule.getDepartmentId())
                .thresholdAmount(rule.getThresholdAmount())
                .restrictedCategoryId(rule.getRestrictedCategoryId())
                .description(rule.getDescription())
                .isActive(rule.getIsActive())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }
}
