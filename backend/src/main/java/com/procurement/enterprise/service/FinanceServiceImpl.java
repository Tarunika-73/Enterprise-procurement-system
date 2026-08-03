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
        long pending = paymentRepository.countByStatusAndIsDeletedFalse(PaymentStatus.PENDING);
        long completed = paymentRepository.countByStatusAndIsDeletedFalse(PaymentStatus.PAID);
        BigDecimal pendingAmount = paymentRepository.sumAmountByStatusAndIsDeletedFalse(PaymentStatus.PENDING);
        BigDecimal totalPaid = paymentRepository.sumAmountByStatusAndIsDeletedFalse(PaymentStatus.PAID);

        // Also count DELIVERED POs that have no payment yet
        long deliveredWithoutPayment = purchaseOrderRepository
                .findByStatusAndIsDeletedFalse(PurchaseOrderStatus.DELIVERED, Pageable.unpaged())
                .getTotalElements();

        return FinanceDashboardResponse.builder()
                .pendingPayments(pending + deliveredWithoutPayment)
                .completedPayments(completed)
                .pendingAmount(pendingAmount)
                .totalAmountPaid(totalPaid)
                .build();
    }

    /* ── pending payments ────────────────────────────────────────── */

    @Override
    @Transactional(readOnly = true)
    public Page<PendingPaymentResponse> getPendingPayments(Pageable pageable) {
        return purchaseOrderRepository
                .findByStatusAndIsDeletedFalse(PurchaseOrderStatus.DELIVERED, pageable)
                .map(po -> {
                    PurchaseRequest pr = po.getPurchaseRequest();
                    Vendor vendor = po.getVendor();

                    // Try to find invoice number via chain
                    String invoiceNumber = null;
                    LocalDate deliveryDate = null;
                    try {
                        Invoice inv = resolveInvoiceForPO(po.getId());
                        invoiceNumber = inv.getInvoiceNumber();
                        Receipt receipt = inv.getReceipt();
                        deliveryDate = receipt.getDelivery().getDeliveryDate();
                    } catch (ResourceNotFoundException ignored) {
                        // Invoice may not exist yet
                    }

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
                            .deliveryDate(deliveryDate)
                            .invoiceNumber(invoiceNumber)
                            .createdAt(po.getCreatedAt())
                            .build();
                });
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

        Invoice invoice = resolveInvoiceForPO(purchaseOrderId);

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
