package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.ApprovePaymentRequest;
import com.procurement.enterprise.dto.response.FinanceDashboardResponse;
import com.procurement.enterprise.dto.response.PaymentHistoryResponse;
import com.procurement.enterprise.dto.response.PaymentResponse;
import com.procurement.enterprise.dto.response.PendingPaymentResponse;
import com.procurement.enterprise.dto.response.FinanceInvoiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FinanceService {

    FinanceDashboardResponse getDashboardStats();

    Page<PendingPaymentResponse> getPendingPayments(Pageable pageable);

    Page<PaymentHistoryResponse> getPaymentHistory(Pageable pageable);

    Page<FinanceInvoiceResponse> getInvoices(Pageable pageable);

    PaymentResponse getPaymentById(Long id);

    PaymentResponse approvePayment(Long purchaseOrderId, ApprovePaymentRequest request);

    PaymentResponse cancelPayment(Long purchaseOrderId);
}
