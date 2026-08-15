package com.procurement.enterprise.service;
import com.procurement.enterprise.dto.request.CreateReceiptRequest;
import com.procurement.enterprise.dto.response.ReceiptResponse;
import com.procurement.enterprise.dto.response.GoodsReceiptWorkflowItemResponse;
import com.procurement.enterprise.dto.response.GoodsReceiptWorkflowResponse;
import com.procurement.enterprise.entity.*;
import com.procurement.enterprise.enums.DeliveryStatus;
import com.procurement.enterprise.enums.InvoiceStatus;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.exception.UnauthorizedException;
import com.procurement.enterprise.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;
import org.springframework.data.domain.Pageable;

@Service @RequiredArgsConstructor
public class GoodsReceiptServiceImpl implements GoodsReceiptService {
    private final DeliveryRepository deliveries; private final ReceiptRepository receipts; private final InvoiceRepository invoices;
    private final UserRepository users; private final NotificationService notifications; private final AuditLogService audit;
    private final GoodsReceiptPdfService goodsReceiptPdfService;
    @Override @Transactional public ReceiptResponse create(CreateReceiptRequest request) {
        User receiver = users.findByEmailAndIsDeletedFalse(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UnauthorizedException("Authenticated internal user not found."));
        Delivery delivery = deliveries.findByIdAndIsDeletedFalse(request.getDeliveryId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery", request.getDeliveryId()));
        if (delivery.getStatus() != DeliveryStatus.DELIVERED || delivery.getPurchaseOrder() == null)
            throw new InvalidRequestException("A receipt can only be created for a delivered purchase order.");
        if (receipts.existsByDeliveryIdAndIsDeletedFalse(delivery.getId()))
            throw new InvalidRequestException("A receipt already exists for this delivery.");
        Receipt receipt = receipts.save(Receipt.builder().delivery(delivery).receiver(receiver)
                .receiptDate(request.getReceiptDate() == null ? LocalDate.now() : request.getReceiptDate())
                .conditionNotes(request.getConditionNotes()).isDeleted(false).build());
        PurchaseOrder po = delivery.getPurchaseOrder();
        if (!invoices.existsByReceiptIdAndIsDeletedFalse(receipt.getId())) {
            invoices.save(Invoice.builder().invoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                    .receipt(receipt).vendor(po.getVendor()).invoiceDate(LocalDate.now()).dueDate(LocalDate.now().plusDays(30))
                    .totalAmount(po.getTotalAmount()).status(InvoiceStatus.PENDING).isDeleted(false).build());
        }
        audit.record("CREATE", "receipts", receipt.getId(), null, "deliveryId=" + delivery.getId());
        for (User officer : users.findActiveProcurementOfficers()) notifications.createNotification(officer, com.procurement.enterprise.enums.NotificationType.SYSTEM, "Goods receipt created", "Goods receipt created for " + po.getPurchaseOrderNumber());
        for (User finance : users.findActiveFinanceOfficers()) notifications.createNotification(finance, com.procurement.enterprise.enums.NotificationType.SYSTEM, "Invoice ready for payment", "Goods receipt and invoice are ready for " + po.getPurchaseOrderNumber());
        return ReceiptResponse.builder().id(receipt.getId()).deliveryId(delivery.getId()).receiverId(receiver.getId())
                .receiverName(receiver.getFirstName() + " " + receiver.getLastName()).receiptDate(receipt.getReceiptDate())
                .conditionNotes(receipt.getConditionNotes()).createdAt(receipt.getCreatedAt()).updatedAt(receipt.getUpdatedAt()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoodsReceiptWorkflowResponse> getDeliveredReceiptWorkflows() {
        return deliveries.findByStatusAndIsDeletedFalse(DeliveryStatus.DELIVERED, Pageable.unpaged())
                .getContent().stream().map(this::mapWorkflow).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GoodsReceiptWorkflowResponse getReceiptWorkflow(Long deliveryId) {
        return mapWorkflow(deliveries.findByIdAndIsDeletedFalse(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery", deliveryId)));
    }

    @Override
    public byte[] generatePdf(Long receiptId) { return goodsReceiptPdfService.generate(receiptId); }

    private GoodsReceiptWorkflowResponse mapWorkflow(Delivery delivery) {
        PurchaseOrder po = delivery.getPurchaseOrder();
        Receipt receipt = receipts.findByDeliveryIdAndIsDeletedFalse(delivery.getId(), Pageable.unpaged())
                .getContent().stream().findFirst().orElse(null);
        return GoodsReceiptWorkflowResponse.builder()
                .deliveryId(delivery.getId()).purchaseOrderId(po.getId())
                .purchaseOrderNumber(po.getPurchaseOrderNumber())
                .vendorName(po.getVendor() == null ? null : po.getVendor().getVendorName())
                .deliveryDate(delivery.getDeliveryDate()).deliveryStatus(delivery.getStatus())
                .receiptCreated(receipt != null).receiptId(receipt == null ? null : receipt.getId())
                .receiverName(receipt == null || receipt.getReceiver() == null ? null
                        : receipt.getReceiver().getFirstName() + " " + receipt.getReceiver().getLastName())
                .receiptDate(receipt == null ? null : receipt.getReceiptDate())
                .conditionNotes(receipt == null ? null : receipt.getConditionNotes())
                .items(po.getItems().stream().filter(item -> !Boolean.TRUE.equals(item.getIsDeleted()))
                        .map(item -> GoodsReceiptWorkflowItemResponse.builder()
                                .productId(item.getProduct() == null ? null : item.getProduct().getId())
                                .productName(item.getProduct() == null ? "—" : item.getProduct().getName())
                                .productSku(item.getProduct() == null ? null : item.getProduct().getSku())
                                .orderedQuantity(item.getQuantity()).deliveredQuantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice()).build()).toList())
                .build();
    }
}
