package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateApprovalRequest;
import com.procurement.enterprise.dto.request.UpdateApprovalRequest;
import com.procurement.enterprise.dto.response.ApprovalResponse;
import com.procurement.enterprise.entity.Approval;
import com.procurement.enterprise.entity.PurchaseRequisition;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.enums.ApprovalStatus;
import com.procurement.enterprise.exception.DuplicateResourceException;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.ApprovalRepository;
import com.procurement.enterprise.repository.PurchaseRequisitionRepository;
import com.procurement.enterprise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {


    private static final Logger log =
            LoggerFactory.getLogger(ApprovalServiceImpl.class);


    private final ApprovalRepository approvalRepository;

    private final PurchaseRequisitionRepository purchaseRequisitionRepository;

    private final UserRepository userRepository;



    @Override
    @Transactional
    public ApprovalResponse create(CreateApprovalRequest request) {


        validateCreateRequest(request);



        PurchaseRequisition requisition =
                purchaseRequisitionRepository
                        .findByIdAndIsDeletedFalse(
                                request.getPurchaseRequestId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Purchase Requisition",
                                        request.getPurchaseRequestId()
                                )
                        );



        User approver = null;


        if(request.getApproverId()!=null){

            approver =
                    userRepository
                            .findByIdAndIsDeletedFalse(
                                    request.getApproverId()
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "User",
                                            request.getApproverId()
                                    )
                            );
        }



        approvalRepository
                .findByPurchaseRequestIdAndLevelAndIsDeletedFalse(
                        request.getPurchaseRequestId(),
                        request.getLevel()
                )
                .ifPresent(a -> {

                    throw new DuplicateResourceException(
                            "Approval",
                            "level",
                            request.getLevel().toString()
                    );

                });



        Approval approval =
                Approval.builder()

                        .purchaseRequisition(requisition)

                        .level(request.getLevel())

                        .approver(approver)

                        .status(
                                request.getStatus()!=null
                                ?
                                request.getStatus()
                                :
                                ApprovalStatus.PENDING
                        )

                        .comments(request.getComments())

                        .isDeleted(false)

                        .build();



        Approval saved =
                approvalRepository.save(approval);



        log.info(
                "Approval created : {}",
                saved.getId()
        );



        return mapToResponse(saved);
    }





    @Override
    @Transactional
    public ApprovalResponse update(
            Long id,
            UpdateApprovalRequest request){


        Approval approval =
                findApproval(id);



        if(request.getLevel()!=null){
            approval.setLevel(request.getLevel());
        }



        if(request.getApproverId()!=null){


            User approver =
                    userRepository
                            .findByIdAndIsDeletedFalse(
                                    request.getApproverId()
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "User",
                                            request.getApproverId()
                                    )
                            );


            approval.setApprover(approver);
        }



        if(request.getStatus()!=null){
            approval.setStatus(request.getStatus());
        }



        if(request.getComments()!=null){
            approval.setComments(request.getComments());
        }



        return mapToResponse(
                approvalRepository.save(approval)
        );
    }





    @Override
    @Transactional
    public void delete(Long id){

        Approval approval =
                findApproval(id);


        approval.setIsDeleted(true);


        approvalRepository.save(approval);

    }





    @Override
    public ApprovalResponse getById(Long id){

        return mapToResponse(
                findApproval(id)
        );
    }





    @Override
    public Page<ApprovalResponse> getAll(Pageable pageable){


        return approvalRepository
                .findAllByIsDeletedFalse(pageable)
                .map(this::mapToResponse);

    }





    @Override
    public Page<ApprovalResponse> getByPurchaseRequest(
            Long purchaseRequestId,
            Pageable pageable){


        return approvalRepository
                .findByPurchaseRequestIdAndIsDeletedFalse(
                        purchaseRequestId,
                        pageable
                )
                .map(this::mapToResponse);

    }





    @Override
    public Page<ApprovalResponse> getByApprover(
            Long approverId,
            Pageable pageable){


        return approvalRepository
                .findByApproverIdAndIsDeletedFalse(
                        approverId,
                        pageable
                )
                .map(this::mapToResponse);

    }





    @Override
    public Page<ApprovalResponse> getByStatus(
            ApprovalStatus status,
            Pageable pageable){


        return approvalRepository
                .findByStatusAndIsDeletedFalse(
                        status,
                        pageable
                )
                .map(this::mapToResponse);

    }





    @Override
    public Page<ApprovalResponse> getByApproverAndStatus(
            Long approverId,
            ApprovalStatus status,
            Pageable pageable){


        return approvalRepository
                .findByApproverIdAndStatusAndIsDeletedFalse(
                        approverId,
                        status,
                        pageable
                )
                .map(this::mapToResponse);

    }





    private Approval findApproval(Long id){


        return approvalRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Approval",
                                id
                        )
                );

    }





    private void validateCreateRequest(
            CreateApprovalRequest request){


        if(request.getPurchaseRequestId()==null){

            throw new InvalidRequestException(
                    "Purchase requisition ID required"
            );

        }


        if(request.getLevel()==null){

            throw new InvalidRequestException(
                    "Approval level required"
            );

        }

    }





    private ApprovalResponse mapToResponse(
            Approval approval){



        User approver =
                approval.getApprover();



        return ApprovalResponse.builder()

                .id(approval.getId())

                .purchaseRequestId(
                        approval.getPurchaseRequisition()!=null
                        ?
                        approval.getPurchaseRequisition().getId()
                        :
                        null
                )

                .level(
                        approval.getLevel()
                )

                .approverId(
                        approver!=null
                        ?
                        approver.getId()
                        :
                        null
                )

                .approverName(
                        approver!=null
                        ?
                        approver.getFirstName()+" "+approver.getLastName()
                        :
                        null
                )

                .status(
                        approval.getStatus()
                )

                .comments(
                        approval.getComments()
                )

                .createdAt(
                        approval.getCreatedAt()
                )

                .updatedAt(
                        approval.getUpdatedAt()
                )

                .build();

    }

}