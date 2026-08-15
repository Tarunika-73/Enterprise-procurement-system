package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.AssistantResponse;
import com.procurement.enterprise.entity.*;
import com.procurement.enterprise.enums.InvoiceStatus;
import com.procurement.enterprise.enums.PurchaseOrderStatus;
import com.procurement.enterprise.enums.PurchaseRequestStatus;
import com.procurement.enterprise.enums.PaymentStatus;
import com.procurement.enterprise.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Controlled natural-language interpreter. Every route below has a fixed repository query and role scope. */
@Service("ruleBasedProcurementAssistantService")
@RequiredArgsConstructor
public class ProcurementAssistantServiceImpl implements ProcurementAssistantService {
    private static final String DENIED = "You do not have permission to access that information.";
    private static final int LIMIT = 5;
    private static final Pattern REQUEST_NO = Pattern.compile("\\bPR[- ]?\\d{4}[- ]?\\d+\\b", Pattern.CASE_INSENSITIVE);
    private static final Set<String> SENSITIVE = Set.of("password", "hash", "jwt", "token", "secret", "credential", "database");
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository;
    private final VendorProductRepository vendorProductRepository;
    private final PurchaseRequestRepository requestRepository;
    private final PurchaseRequestItemRepository requestItemRepository;
    private final PurchaseOrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    @Override @Transactional(readOnly = true)
    public AssistantResponse answer(String question, List<com.procurement.enterprise.dto.request.AssistantHistoryMessage> ignoredHistory) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return reply("UNAUTHORIZED", DENIED);
        String q = normalize(question);
        String role = auth.getAuthorities().stream().map(a -> a.getAuthority()).findFirst().orElse("");
        if (SENSITIVE.stream().anyMatch(q::contains)) return reply("UNAUTHORIZED", DENIED);
        if (isGreeting(q)) return reply("CONVERSATION", "Hello! I can help you with procurement information available to your role.");
        if (q.contains("who are you") || q.contains("what can you do")) return reply("HELP", "I can answer role-authorized questions about requests, orders, products, invoices, payments, and procurement activity.");
        return switch (role) {
            case "ROLE_EMPLOYEE" -> employee(q, auth.getName());
            case "ROLE_MANAGER" -> manager(q, auth.getName());
            case "ROLE_PROCUREMENT_OFFICER" -> procurement(q);
            case "ROLE_FINANCE" -> finance(q);
            case "ROLE_VENDOR" -> vendor(q, auth.getName());
            case "ROLE_ADMIN" -> admin(q);
            default -> reply("UNAUTHORIZED", DENIED);
        };
    }

    private AssistantResponse employee(String q, String email) {
        if (asksOutsideEmployeeScope(q)) return reply("UNAUTHORIZED", DENIED);
        User user = user(email);
        PurchaseRequestStatus status = requestStatus(q);
        Matcher number = REQUEST_NO.matcher(q);
        if (number.find() || q.contains("status")) {
            if (number.find(0)) return requestByNumber(number.group().replace(' ', '-').toUpperCase(Locale.ROOT), user);
            if (q.contains("latest") || q.contains("last")) return latestRequest(user, true);
        }
        if (q.contains("how many") || q.startsWith("count ")) {
            if (q.contains("request") || q.contains("requisition")) return reply("MY_REQUEST_COUNT", "You have created " + requestRepository.countByRequesterIdAndIsDeletedFalse(user.getId()) + " purchase request(s) so far.");
        }
        if (q.contains("total value") || q.contains("total amount") || q.contains("how much") || q.contains("spent"))
            return reply("MY_REQUEST_VALUE", "The total value of your purchase requests is " + money(requestRepository.sumTotalAmountByRequesterId(user.getId())) + ".");
        if (q.contains("product") || q.contains("item") || q.contains("quantity")) {
            List<String> products = requestItemRepository.findDistinctProductNamesByRequesterId(user.getId(), PageRequest.of(0, LIMIT)).getContent();
            return reply("MY_REQUESTED_PRODUCTS", products.isEmpty() ? "You have not requested any products yet." : "Products you requested: " + numbered(products) + ".");
        }
        if (q.contains("purchase order") || q.matches(".*\\borders?\\b.*") || q.contains("vendor associated")) {
            List<PurchaseOrder> orders = orderRepository.findByPurchaseRequest_Requester_IdAndIsDeletedFalseOrderByCreatedAtDesc(user.getId(), PageRequest.of(0, LIMIT)).getContent();
            return orders(orders, "Purchase orders generated from your requests");
        }
        if (q.contains("latest") || q.contains("last")) return latestRequest(user, false);
        if (q.contains("request") || q.contains("requisition")) return requestList(user, status);
        return clarification("your request", "status, latest request, total value, requested products, or purchase orders");
    }

    private AssistantResponse manager(String q, String email) {
        if (q.contains("vendor") || q.contains("supplier") || q.contains("invoice") || q.contains("payment") || q.contains("other department")) return reply("UNAUTHORIZED", DENIED);
        User user = user(email); PurchaseRequestStatus status = requestStatus(q);
        if (q.contains("total value") || q.contains("total amount")) {
            PurchaseRequestStatus scopeStatus = status == null ? PurchaseRequestStatus.PENDING : status;
            return reply("DEPARTMENT_VALUE", "The total value of " + human(scopeStatus.name()) + " requests in your approval scope is " + money(requestRepository.sumTotalAmountByManagerIdAndStatus(user.getId(), scopeStatus)) + ".");
        }
        if (q.contains("how many") || q.contains("waiting") || q.contains("pending")) {
            PurchaseRequestStatus scopeStatus = status == null ? PurchaseRequestStatus.PENDING : status;
            return reply("DEPARTMENT_REQUEST_COUNT", "There are " + requestRepository.countByManagerIdAndStatusAndIsDeletedFalse(user.getId(), scopeStatus) + " " + human(scopeStatus.name()) + " requests in your approval scope.");
        }
        List<PurchaseRequest> items = status == null ? requestRepository.findByManagerIdAndIsDeletedFalseOrderByCreatedAtDesc(user.getId(), PageRequest.of(0, LIMIT)).getContent() : requestRepository.findByManagerIdAndStatusAndIsDeletedFalse(user.getId(), status, PageRequest.of(0, LIMIT)).getContent();
        return requestLines(items, "Requests in your approval scope");
    }

    private AssistantResponse procurement(String q) {
        if (q.contains("invoice") || q.contains("payment")) return reply("UNAUTHORIZED", DENIED);
        if (isProductPriceQuestion(q) || q.contains("vendors supply") || q.contains("lowest") && q.contains("vendor")) return productPricing(q);
        if (q.contains("active vendor") || q.contains("active supplier")) return reply("ACTIVE_VENDORS", "There are " + vendorRepository.countByIsActiveTrueAndIsDeletedFalse() + " active vendors.");
        if (q.contains("product") && (q.contains("available") || q.contains("show"))) return reply("PRODUCTS", "Use the product catalog to view the current authorized product list.");
        if (q.contains("purchase order") || q.matches(".*\\borders?\\b.*")) {
            PurchaseOrderStatus orderStatus = q.contains("pending") ? PurchaseOrderStatus.CREATED : null;
            long count = orderStatus == null ? orderRepository.countByIsDeletedFalse() : orderRepository.countByStatusAndIsDeletedFalse(orderStatus);
            return reply("PURCHASE_ORDER_COUNT", "There are " + count + (orderStatus == null ? " purchase orders in procurement." : " pending purchase orders in procurement."));
        }
        if (q.contains("total") && (q.contains("amount") || q.contains("spend"))) return reply("PROCUREMENT_VALUE", "The total procurement request value is " + money(requestRepository.sumTotalAmountByIsDeletedFalse()) + ".");
        if (q.contains("request") || q.contains("requisition")) { PurchaseRequestStatus s = requestStatus(q); long count = s == null ? requestRepository.countByIsDeletedFalse() : requestRepository.countByStatusAndIsDeletedFalse(s); return reply("PROCUREMENT_REQUEST_COUNT", "There are " + count + (s == null ? " purchase requests." : " " + human(s.name()) + " purchase requests.")); }
        return clarification("procurement", "request counts, purchase orders, product prices, vendors, or total procurement value");
    }

    private AssistantResponse finance(String q) {
        if (q.contains("employee") || q.contains("vendor detail") || q.contains("department")) return reply("UNAUTHORIZED", DENIED);
        if (q.contains("paid") || q.contains("completed")) return reply("PAID_TOTAL", "Payments completed total " + money(paymentRepository.sumPaidAmount()) + ".");
        if (q.contains("pending") && (q.contains("invoice") || q.contains("payment"))) return reply("PENDING_INVOICES", "There are " + invoiceRepository.countByStatusAndIsDeletedFalse(InvoiceStatus.PENDING) + " pending invoices.");
        if (q.contains("recent payment")) { List<Payment> payments = paymentRepository.findAllByIsDeletedFalse(PageRequest.of(0, LIMIT)).getContent(); return reply("RECENT_PAYMENTS", payments.isEmpty() ? "No payments found." : "Recent payments: " + numbered(payments.stream().map(p -> p.getPaymentReference() + " — " + money(p.getAmountPaid()) + " — " + human(p.getStatus().name())).toList()) + "."); }
        if (q.contains("invoice")) return reply("INVOICE_COUNT", "There are " + invoiceRepository.countByIsDeletedFalse() + " invoices in your authorized finance scope.");
        return clarification("finance", "pending invoices, completed payments, or recent payments");
    }

    private AssistantResponse vendor(String q, String email) {
        if (q.contains("other vendor") || q.contains("all vendor") || q.contains("procurement spending")) return reply("UNAUTHORIZED", DENIED);
        Vendor vendor = vendorRepository.findByEmailAndIsDeletedFalse(email).orElse(null); if (vendor == null) return reply("UNAUTHORIZED", DENIED);
        if (q.contains("invoice")) return reply("MY_INVOICES", "You have " + invoiceRepository.countByVendorIdAndIsDeletedFalse(vendor.getId()) + " invoice(s).");
        if (q.contains("payment")) return reply("MY_PAYMENTS", "Payment details for your invoices are available through your vendor portal.");
        List<PurchaseOrder> orders = orderRepository.findByVendorIdAndIsDeletedFalse(vendor.getId(), PageRequest.of(0, LIMIT)).getContent();
        return orders(orders, "Your purchase orders");
    }

    private AssistantResponse admin(String q) {
        if (SENSITIVE.stream().anyMatch(q::contains)) return reply("UNAUTHORIZED", DENIED);
        if (q.contains("user")) return reply("USER_COUNT", "There are " + userRepository.countByIsDeletedFalse() + " users.");
        if (q.contains("active vendor") || q.contains("active supplier")) return reply("ACTIVE_VENDOR_COUNT", "There are " + vendorRepository.countByIsActiveTrueAndIsDeletedFalse() + " active vendors.");
        if (q.contains("vendor") || q.contains("supplier")) return reply("VENDOR_COUNT", "There are " + vendorRepository.countByIsDeletedFalse() + " vendors.");
        if (q.contains("paid") || q.contains("payment")) return reply("PAID_TOTAL", "Payments completed total " + money(paymentRepository.sumPaidAmount()) + ".");
        if (q.contains("invoice")) return reply("INVOICE_COUNT", "There are " + invoiceRepository.countByIsDeletedFalse() + " invoices.");
        if (q.contains("total") && (q.contains("amount") || q.contains("procurement"))) return reply("PROCUREMENT_VALUE", "The total procurement request value is " + money(requestRepository.sumTotalAmountByIsDeletedFalse()) + ".");
        if (q.contains("purchase order") || q.matches(".*\\borders?\\b.*")) return reply("ORDER_COUNT", "There are " + orderRepository.countByIsDeletedFalse() + " purchase orders.");
        return reply("REQUEST_COUNT", "There are " + requestRepository.countByIsDeletedFalse() + " purchase requests.");
    }

    private AssistantResponse productPricing(String q) {
        Product product = findProduct(q); if (product == null) return reply("PRODUCT_CLARIFICATION", "Which product would you like pricing for? Please provide its name.");
        List<VendorProduct> offers = vendorProductRepository.findTop5ByProductIdAndIsActiveTrueAndIsDeletedFalseOrderByPriceAsc(product.getId());
        if (offers.isEmpty()) return reply("PRODUCT_PRICE", "No active vendor pricing is available for " + product.getName() + ".");
        List<String> lines = offers.stream().map(o -> o.getVendor().getVendorName() + " — " + money(o.getPrice())).toList();
        return reply("PRODUCT_PRICE", product.getName() + " pricing: " + numbered(lines) + ". Lowest available price: " + money(offers.get(0).getPrice()) + ".");
    }

    private Product findProduct(String q) { String[] words = q.replaceAll("[^a-z0-9 ]", " ").split("\\s+"); for (int i = words.length - 1; i >= 0; i--) if (words[i].length() > 2 && !Set.of("price","rate","cost","vendor","supply","lowest","current","what","which","does","laptop").contains(words[i]) || "laptop".equals(words[i])) { List<Product> found = productRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(words[i], PageRequest.of(0, 1)).getContent(); if (!found.isEmpty()) return found.get(0); } return null; }
    private AssistantResponse requestByNumber(String no, User user) { PurchaseRequest r = requestRepository.findByRequestNumberAndIsDeletedFalse(no).orElse(null); if (r == null || r.getRequester() == null || !user.getId().equals(r.getRequester().getId())) return reply("UNAUTHORIZED", DENIED); return reply("MY_REQUEST_STATUS", "Your purchase request " + r.getRequestNumber() + " is currently " + human(r.getStatus().name()) + "."); }
    private AssistantResponse latestRequest(User user, boolean statusOnly) { List<PurchaseRequest> rs = requestRepository.findByRequesterIdAndIsDeletedFalseOrderByCreatedAtDesc(user.getId(), PageRequest.of(0, 1)).getContent(); if (rs.isEmpty()) return reply("MY_LATEST_REQUEST", "You have not created a purchase request yet."); PurchaseRequest r = rs.get(0); return reply("MY_LATEST_REQUEST", "Your latest request, " + r.getRequestNumber() + ", is " + human(r.getStatus().name()) + (statusOnly ? "." : " and was created on " + r.getCreatedAt().toLocalDate() + ".")); }
    private AssistantResponse requestList(User u, PurchaseRequestStatus s) { List<PurchaseRequest> rs = s == null ? requestRepository.findByRequesterIdAndIsDeletedFalseOrderByCreatedAtDesc(u.getId(), PageRequest.of(0, LIMIT)).getContent() : requestRepository.findByRequesterIdAndStatusAndIsDeletedFalse(u.getId(), s, PageRequest.of(0, LIMIT)).getContent(); return requestLines(rs, s == null ? "Your purchase requests" : "Your " + human(s.name()) + " purchase requests"); }
    private AssistantResponse requestLines(List<PurchaseRequest> rs, String label) { return reply("REQUEST_LIST", rs.isEmpty() ? label + ": none found." : label + ": " + numbered(rs.stream().map(r -> r.getRequestNumber() + " — " + human(r.getStatus().name())).toList()) + "."); }
    private AssistantResponse orders(List<PurchaseOrder> os, String label) { return reply("PURCHASE_ORDERS", os.isEmpty() ? label + ": none found." : label + ": " + numbered(os.stream().map(o -> o.getPurchaseOrderNumber() + " — " + human(o.getStatus().name()) + (o.getVendor() == null ? "" : " — " + o.getVendor().getVendorName())).toList()) + "."); }
    private User user(String email) { return userRepository.findByEmailAndIsDeletedFalse(email).orElseThrow(); }
    private boolean asksOutsideEmployeeScope(String q) { return (q.contains("vendor") || q.contains("supplier") || isProductPriceQuestion(q) || q.contains("manager") || q.contains("department") || q.contains("another employee") || q.matches(".*\\bemp\\d+.*")) && !(q.contains("purchase order") || q.matches(".*\\borders?\\b.*")); }
    private boolean isProductPriceQuestion(String q) { return q.contains("price") || q.contains("rate") || q.contains("cost") || q.contains("how much does"); }
    private PurchaseRequestStatus requestStatus(String q) { if (q.contains("closed")) return PurchaseRequestStatus.CLOSED; if (q.contains("rejected")) return PurchaseRequestStatus.REJECTED; if (q.contains("approved")) return PurchaseRequestStatus.APPROVED; if (q.contains("pending") || q.contains("waiting")) return PurchaseRequestStatus.PENDING; return null; }
    private boolean isGreeting(String q) { return q.matches("^(hi|hello|hey|good morning|good afternoon|good evening|thanks|thank you|okay|ok)[!. ]*$"); }
    private String normalize(String q) { return q == null ? "" : q.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim(); }
    private String human(String s) { return s.toLowerCase(Locale.ROOT).replace('_', ' '); }
    private String money(BigDecimal value) { return NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(value == null ? BigDecimal.ZERO : value); }
    private String numbered(List<String> values) { List<String> lines = new ArrayList<>(); for (int i=0;i<values.size();i++) lines.add((i+1) + ". " + values.get(i)); return String.join("; ", lines); }
    private AssistantResponse clarification(String subject, String options) { return reply("CLARIFICATION", "I can help with " + subject + ". Do you want " + options + "?"); }
    private AssistantResponse reply(String intent, String message) { return AssistantResponse.builder().intent(intent).message(message).suggestions(List.of()).build(); }
}
