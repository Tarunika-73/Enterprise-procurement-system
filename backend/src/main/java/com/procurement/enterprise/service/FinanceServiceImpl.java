package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.ApprovePaymentRequest;
import com.procurement.enterprise.dto.response.*;
import com.procurement.enterprise.entity.*;
import com.procurement.enterprise.enums.*;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.*;
import com.procurement.enterprise.util.Constants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {

    private static final Logger log = LoggerFactory.getLogger(FinanceServiceImpl.class);

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final DeliveryRepository deliveryRepository;
    private final ReceiptRepository receiptRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    /* ── helpers ─────────────────────────────────────────────────── */

    private PurchaseOrder resolvePO(Long purchaseOrderId) {
        return purchaseOrderRepository.findByIdAndIsDeletedFalse(purchaseOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", purchaseOrderId));
    }

    private Invoice resolveInvoiceForPO(Long purchaseOrderId) {
        // Chain: PO → Delivery → Receipt → Invoice
        Delivery delivery = deliveryRepository
                .findByPurchaseOrderIdAndIsDeletedFalse(purchaseOrderId, Pageable.unpaged())
                .getContent().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No delivery found for PurchaseOrder id: " + purchaseOrderId));

        Receipt receipt = receiptRepository
                .findByDeliveryIdAndIsDeletedFalse(delivery.getId(), Pageable.unpaged())
                .getContent().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No receipt found for delivery id: " + delivery.getId()));

        return invoiceRepository.findByReceiptIdAndIsDeletedFalse(receipt.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No invoice found for receipt id: " + receipt.getId()));
    }

    private Invoice requirePayableInvoice(PurchaseOrder po) {
        Delivery delivery = deliveryRepository.findByPurchaseOrderIdAndIsDeletedFalse(po.getId(), Pageable.unpaged())
                .getContent().stream().findFirst()
                .orElseThrow(() -> new InvalidRequestException("Payment cannot be approved because the delivery has not been created."));
        Receipt receipt = receiptRepository.findByDeliveryIdAndIsDeletedFalse(delivery.getId(), Pageable.unpaged())
                .getContent().stream().findFirst()
                .orElseThrow(() -> new InvalidRequestException("Payment cannot be approved because the Goods Receipt has not been created."));
        return invoiceRepository.findByReceiptIdAndIsDeletedFalse(receipt.getId())
                .orElseThrow(() -> new InvalidRequestException("Payment cannot be approved because the invoice has not been created."));
    }

    private String generatePaymentReference() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniquePart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return Constants.PAY_PREFIX + datePart + "-" + uniquePart;
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        Invoice invoice = payment.getInvoice();
        Receipt receipt = invoice.getReceipt();
        Delivery delivery = receipt.getDelivery();
        PurchaseOrder po = delivery.getPurchaseOrder();
        PurchaseRequest pr = po.getPurchaseRequest();
        Vendor vendor = invoice.getVendor();

        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .purchaseOrderId(po.getId())
                .purchaseOrderNumber(po.getPurchaseOrderNumber())
                .requestNumber(pr != null ? pr.getRequestNumber() : null)
                .vendorId(vendor.getId())
                .vendorName(vendor.getVendorName())
                .vendorEmail(vendor.getEmail())
                .departmentName(pr != null && pr.getDepartment() != null ? pr.getDepartment().getName() : null)
                .amountPaid(payment.getAmountPaid())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .remarks(payment.getRemarks())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    /* ── dashboard ───────────────────────────────────────────────── */

    @Override
    @Transactional(readOnly = true)
    public FinanceDashboardResponse getDashboardStats() {
        var eligibleInvoices = invoiceRepository.findEligibleForPayment(PurchaseOrderStatus.DELIVERED,
                InvoiceStatus.PAID, InvoiceStatus.CANCELLED, Pageable.unpaged()).getContent();
        long pending = eligibleInvoices.size();
        long completed = paymentRepository.countByStatusAndIsDeletedFalse(PaymentStatus.PAID);
        BigDecimal pendingAmount = eligibleInvoices.stream().map(Invoice::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPaid = paymentRepository.sumAmountByStatusAndIsDeletedFalse(PaymentStatus.PAID);

        return FinanceDashboardResponse.builder()
                .pendingPayments(pending)
                .completedPayments(completed)
                .pendingAmount(pendingAmount)
                .totalAmountPaid(totalPaid)
                .build();
    }

    /* ── pending payments ────────────────────────────────────────── */

    @Override
    @Transactional(readOnly = true)
    public Page<PendingPaymentResponse> getPendingPayments(Pageable pageable) {
        return invoiceRepository.findEligibleForPayment(PurchaseOrderStatus.DELIVERED,
                        InvoiceStatus.PAID, InvoiceStatus.CANCELLED, pageable)
                .map(inv -> {
                    Receipt receipt = inv.getReceipt();
                    PurchaseOrder po = receipt.getDelivery().getPurchaseOrder();
                    PurchaseRequest pr = po.getPurchaseRequest();
                    Vendor vendor = po.getVendor();
                    return PendingPaymentResponse.builder()
                            .purchaseOrderId(po.getId())
                            .purchaseOrderNumber(po.getPurchaseOrderNumber())
                            .requestNumber(pr != null ? pr.getRequestNumber() : null)
                            .vendorId(vendor != null ? vendor.getId() : null)
                            .vendorName(vendor != null ? vendor.getVendorName() : null)
                            .vendorEmail(vendor != null ? vendor.getEmail() : null)
                            .departmentName(pr != null && pr.getDepartment() != null ? pr.getDepartment().getName() : null)
                            .totalAmount(po.getTotalAmount())
                            .expectedDeliveryDate(po.getExpectedDeliveryDate())
                            .deliveryDate(receipt.getDelivery().getDeliveryDate())
                            .invoiceNumber(inv.getInvoiceNumber())
                            .createdAt(po.getCreatedAt())
                            .build();
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinanceInvoiceResponse> getInvoices(Pageable pageable) {
        return invoiceRepository.findAllByIsDeletedFalse(pageable).map(this::mapInvoice);
    }

    private FinanceInvoiceResponse mapInvoice(Invoice invoice) {
        Payment payment = paymentRepository.findByInvoiceIdAndIsDeletedFalse(invoice.getId()).orElse(null);
        BigDecimal paidAmount = payment == null ? BigDecimal.ZERO : payment.getAmountPaid();
        PurchaseOrder po = invoice.getReceipt() != null && invoice.getReceipt().getDelivery() != null
                ? invoice.getReceipt().getDelivery().getPurchaseOrder() : null;
        PurchaseRequest pr = po == null ? null : po.getPurchaseRequest();
        Vendor vendor = invoice.getVendor();
        return FinanceInvoiceResponse.builder().id(invoice.getId()).invoiceNumber(invoice.getInvoiceNumber())
                .purchaseOrderNumber(po == null ? null : po.getPurchaseOrderNumber())
                .purchaseRequestNumber(pr == null ? null : pr.getRequestNumber())
                .vendorName(vendor == null ? null : vendor.getVendorName())
                .invoiceDate(invoice.getInvoiceDate()).dueDate(invoice.getDueDate())
                .totalAmount(invoice.getTotalAmount()).paidAmount(paidAmount)
                .balanceAmount(invoice.getTotalAmount().subtract(paidAmount)).status(invoice.getStatus()).build();
    }

    /* ── payment history ─────────────────────────────────────────── */

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentHistoryResponse> getPaymentHistory(Pageable pageable) {
        return paymentRepository.findAllByIsDeletedFalse(pageable)
                .map(payment -> {
                    Invoice invoice = payment.getInvoice();
                    Receipt receipt = invoice.getReceipt();
                    Delivery delivery = receipt.getDelivery();
                    PurchaseOrder po = delivery.getPurchaseOrder();
                    PurchaseRequest pr = po.getPurchaseRequest();
                    Vendor vendor = invoice.getVendor();

                    return PaymentHistoryResponse.builder()
                            .id(payment.getId())
                            .paymentReference(payment.getPaymentReference())
                            .purchaseOrderId(po.getId())
                            .purchaseOrderNumber(po.getPurchaseOrderNumber())
                            .requestNumber(pr != null ? pr.getRequestNumber() : null)
                            .vendorId(vendor.getId())
                            .vendorName(vendor.getVendorName())
                            .invoiceNumber(invoice.getInvoiceNumber())
                            .amountPaid(payment.getAmountPaid())
                            .paymentDate(payment.getPaymentDate())
                            .paymentMethod(payment.getPaymentMethod())
                            .remarks(payment.getRemarks())
                            .status(payment.getStatus())
                            .createdAt(payment.getCreatedAt())
                            .build();
                });
    }

    /* ── get by id ───────────────────────────────────────────────── */

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        return mapToPaymentResponse(payment);
    }

    /* ── approve payment ─────────────────────────────────────────── */

    @Override
    @Transactional
    public PaymentResponse approvePayment(Long purchaseOrderId, ApprovePaymentRequest request) {
        PurchaseOrder po = resolvePO(purchaseOrderId);

        if (po.getStatus() == PurchaseOrderStatus.CLOSED) {
            throw new InvalidRequestException("Payment has already been processed for this Purchase Order.");
        }
        if (po.getStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new InvalidRequestException("Cannot approve payment for a cancelled Purchase Order.");
        }
        if (po.getStatus() != PurchaseOrderStatus.DELIVERED) {
            throw new InvalidRequestException(
                    "Payment can only be approved for DELIVERED orders. Current status: " + po.getStatus());
        }

        Invoice invoice = requirePayableInvoice(po);

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new InvalidRequestException("Invoice has already been paid.");
        }
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new InvalidRequestException("Cannot approve payment for a cancelled invoice.");
        }

        if (paymentRepository.existsByInvoiceIdAndIsDeletedFalse(invoice.getId())) {
            throw new InvalidRequestException("Payment already exists for this Purchase Order.");
        }

        String paymentReference = generatePaymentReference();
        while (paymentRepository.existsByPaymentReferenceAndIsDeletedFalse(paymentReference)) {
            paymentReference = generatePaymentReference();
        }

        Payment payment = Payment.builder()
                .invoice(invoice)
                .paymentReference(paymentReference)
                .amountPaid(invoice.getTotalAmount())
                .paymentDate(LocalDate.now())
                .paymentMethod(request.getPaymentMethod())
                .remarks(request.getRemarks())
                .status(PaymentStatus.PAID)
                .isDeleted(false)
                .build();

        Payment saved = paymentRepository.save(payment);

        // A completed payment settles its linked invoice in the same transaction.
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);

        // Update PO status → CLOSED
        po.setStatus(PurchaseOrderStatus.CLOSED);
        purchaseOrderRepository.save(po);

        // Update linked PR status → CLOSED
        PurchaseRequest pr = po.getPurchaseRequest();
        if (pr != null) {
            pr.setStatus(PurchaseRequestStatus.CLOSED);
            purchaseRequestRepository.save(pr);
        }

        log.info("Payment {} approved for PO {}", paymentReference, po.getPurchaseOrderNumber());
        auditLogService.record("PAYMENT_APPROVED", "payments", saved.getId(), null, "purchaseOrderId=" + po.getId());
        if (po.getPurchaseRequest() != null && po.getPurchaseRequest().getRequester() != null) notificationService.createNotification(po.getPurchaseRequest().getRequester(), NotificationType.SYSTEM,
                "Payment completed", "Payment for purchase order " + po.getPurchaseOrderNumber() + " has been completed.");
        for (User finance : userRepository.findActiveFinanceOfficers()) notificationService.createNotification(finance, NotificationType.SYSTEM,
                "Payment completed", "Payment " + paymentReference + " was completed.");
        return mapToPaymentResponse(saved);
    }

    /* ── cancel payment ──────────────────────────────────────────── */

    @Override
    @Transactional
    public PaymentResponse cancelPayment(Long purchaseOrderId) {
        PurchaseOrder po = resolvePO(purchaseOrderId);

        if (po.getStatus() == PurchaseOrderStatus.CLOSED) {
            throw new InvalidRequestException("Cannot cancel payment for a CLOSED Purchase Order.");
        }
        if (po.getStatus() != PurchaseOrderStatus.DELIVERED) {
            throw new InvalidRequestException(
                    "Only DELIVERED orders can have their payment cancelled. Current status: " + po.getStatus());
        }

        Invoice invoice = resolveInvoiceForPO(purchaseOrderId);

        Payment payment = paymentRepository.findByInvoiceIdAndIsDeletedFalse(invoice.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active payment found for PurchaseOrder id: " + purchaseOrderId));

        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            throw new InvalidRequestException("Payment is already cancelled.");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        Payment saved = paymentRepository.save(payment);

        log.info("Payment {} cancelled for PO {}", payment.getPaymentReference(), po.getPurchaseOrderNumber());
        return mapToPaymentResponse(saved);
    }
}
