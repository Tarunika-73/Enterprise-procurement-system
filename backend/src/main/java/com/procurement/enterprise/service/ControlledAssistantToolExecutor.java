package com.procurement.enterprise.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurement.enterprise.dto.request.AssistantHistoryMessage;
import com.procurement.enterprise.dto.response.AssistantResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.*;

/** The only model-to-data boundary: validates a tool, authorizes the caller, then invokes controlled logic. */
@Service
public class ControlledAssistantToolExecutor {
    private static final Set<String> PRODUCT_TOOLS = Set.of("get_product_prices", "get_vendor_product_prices");
    private static final Map<String, Set<String>> ROLES = Map.ofEntries(
            Map.entry("get_my_request_count", Set.of("ROLE_EMPLOYEE")), Map.entry("get_my_purchase_requests", Set.of("ROLE_EMPLOYEE")), Map.entry("get_my_latest_purchase_request", Set.of("ROLE_EMPLOYEE")), Map.entry("get_my_request_by_number", Set.of("ROLE_EMPLOYEE")), Map.entry("get_my_request_total", Set.of("ROLE_EMPLOYEE")), Map.entry("get_my_purchase_orders", Set.of("ROLE_EMPLOYEE", "ROLE_VENDOR")), Map.entry("get_my_latest_purchase_order", Set.of("ROLE_EMPLOYEE", "ROLE_VENDOR")), Map.entry("get_my_invoices", Set.of("ROLE_EMPLOYEE", "ROLE_VENDOR")), Map.entry("get_my_manager", Set.of("ROLE_EMPLOYEE")), Map.entry("get_my_report", Set.of("ROLE_EMPLOYEE")),
            Map.entry("get_pending_approvals", Set.of("ROLE_MANAGER")), Map.entry("get_department_requests", Set.of("ROLE_MANAGER")), Map.entry("get_approved_requests", Set.of("ROLE_MANAGER")), Map.entry("get_rejected_requests", Set.of("ROLE_MANAGER")),
            Map.entry("get_purchase_request_statistics", Set.of("ROLE_PROCUREMENT_OFFICER")), Map.entry("get_purchase_order_statistics", Set.of("ROLE_PROCUREMENT_OFFICER")), Map.entry("get_product_prices", Set.of("ROLE_PROCUREMENT_OFFICER")), Map.entry("get_vendor_product_prices", Set.of("ROLE_PROCUREMENT_OFFICER")), Map.entry("get_active_vendors", Set.of("ROLE_PROCUREMENT_OFFICER", "ROLE_ADMIN")), Map.entry("get_vendor_performance", Set.of("ROLE_PROCUREMENT_OFFICER")),
            Map.entry("get_pending_invoices", Set.of("ROLE_FINANCE")), Map.entry("get_paid_invoices", Set.of("ROLE_FINANCE")), Map.entry("get_payment_statistics", Set.of("ROLE_FINANCE")), Map.entry("get_invoice_status", Set.of("ROLE_FINANCE")),
            Map.entry("get_my_payments", Set.of("ROLE_VENDOR")), Map.entry("get_my_delivery_status", Set.of("ROLE_VENDOR")),
            Map.entry("get_user_statistics", Set.of("ROLE_ADMIN")), Map.entry("get_vendor_statistics", Set.of("ROLE_ADMIN")), Map.entry("get_procurement_statistics", Set.of("ROLE_ADMIN")), Map.entry("get_finance_statistics", Set.of("ROLE_ADMIN")), Map.entry("get_department_statistics", Set.of("ROLE_ADMIN")));
    private final ObjectMapper objectMapper;
    private final ProcurementAssistantServiceImpl fallback;
    private final AssistantToolService toolService;

    public ControlledAssistantToolExecutor(ObjectMapper objectMapper, ProcurementAssistantServiceImpl fallback, AssistantToolService toolService) {
        this.objectMapper = objectMapper;
        this.fallback = fallback;
        this.toolService = toolService;
    }

    public Optional<String> execute(String name, Object rawArguments) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !ROLES.containsKey(name)) return Optional.empty();
        String role = auth.getAuthorities().stream().map(a -> a.getAuthority()).findFirst().orElse("");
        if (!ROLES.get(name).contains(role)) return Optional.of("You do not have permission to access that information.");
        try {
            Map<String, Object> args = rawArguments == null ? Map.of() : objectMapper.convertValue(rawArguments, new TypeReference<>() {});
            if (!validArguments(name, args)) return Optional.empty();
            if ("get_my_latest_purchase_order".equals(name)) return toolService.latestPurchaseOrder();
            if ("get_my_manager".equals(name)) return toolService.myManager();
            if ("get_my_report".equals(name)) return toolService.myReport();
            AssistantResponse result = fallback.answer(promptFor(name, args), List.<AssistantHistoryMessage>of());
            return Optional.ofNullable(result.getMessage());
        } catch (Exception ignored) { return Optional.empty(); }
    }
    /** Fast path for clear, high-frequency requests. Ambiguous wording still goes to Ollama. */
    public Optional<String> fastAnswer(String question) {
        String q = question == null ? "" : question.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        String role = auth.getAuthorities().stream().map(a -> a.getAuthority()).findFirst().orElse("");
        boolean price = q.matches(".*\\b(price|rate|cost|cheapest)\\b.*");
        boolean po = q.contains("purchase order") || q.matches(".*\\bpo\\b.*");
        boolean pr = q.contains("purchase request") || q.contains("purchase requisition") || q.matches(".*\\bprs?\\b.*") || (!po && q.contains("request"));
        boolean latest = q.matches(".*\\b(latest|recent|most recent|newest|last)\\b.*") || q.contains("status");
        if ("ROLE_EMPLOYEE".equals(role)) return employeeFast(q, po, pr, latest, price);
        if ("ROLE_VENDOR".equals(role)) {
            if (po) return execute(latest ? "get_my_latest_purchase_order" : "get_my_purchase_orders", Map.of());
            if (q.contains("invoice")) return execute("get_my_invoices", Map.of());
            if (q.contains("payment")) return execute("get_my_payments", Map.of());
            if (q.contains("delivery")) return execute("get_my_delivery_status", Map.of());
        }
        if ("ROLE_MANAGER".equals(role) && pr) return execute(q.contains("approved") ? "get_approved_requests" : q.contains("rejected") ? "get_rejected_requests" : q.contains("pending") || q.contains("waiting") ? "get_pending_approvals" : "get_department_requests", Map.of());
        if ("ROLE_FINANCE".equals(role)) {
            if (q.contains("pending") && q.contains("invoice")) return execute("get_pending_invoices", Map.of());
            if (q.contains("paid") && q.contains("invoice")) return execute("get_paid_invoices", Map.of());
            if (q.contains("payment")) return execute("get_payment_statistics", Map.of());
            if (q.contains("invoice")) return execute("get_invoice_status", Map.of());
        }
        if ("ROLE_PROCUREMENT_OFFICER".equals(role)) {
            if (price) return execute(q.contains("vendor") || q.contains("cheapest") ? "get_vendor_product_prices" : "get_product_prices", Map.of("product_name", productName(q)));
            if (q.contains("active") && (q.contains("vendor") || q.contains("supplier"))) return execute("get_active_vendors", Map.of());
            if (po) return execute("get_purchase_order_statistics", Map.of());
            if (pr || q.contains("procurement") || q.contains("spend")) return execute("get_purchase_request_statistics", Map.of());
        }
        if ("ROLE_ADMIN".equals(role)) {
            if (q.contains("active") && (q.contains("vendor") || q.contains("supplier"))) return execute("get_active_vendors", Map.of());
            if (q.contains("user")) return execute("get_user_statistics", Map.of());
            if (q.contains("vendor") || q.contains("supplier")) return execute("get_vendor_statistics", Map.of());
            if (q.contains("invoice") || q.contains("payment")) return execute("get_finance_statistics", Map.of());
            if (po || pr || q.contains("procurement") || q.contains("spend")) return execute("get_procurement_statistics", Map.of());
        }
        return Optional.empty();
    }
    private Optional<String> employeeFast(String q, boolean po, boolean pr, boolean latest, boolean price) {
        if (q.matches(".*\\b(all )?(vendors?|suppliers?)\\b.*") || q.contains("another employee") || price)
            return Optional.of("You do not have permission to access that information.");
        boolean report = q.contains("report") || q.contains("procurement summary") || q.contains("procurement activity");
        if (report) {
            if (q.contains("everyone") || q.contains("all employees") || q.matches(".*\\b[a-z]+['’]s report\\b.*") || !q.matches(".*\\b(my|mine)\\b.*"))
                return Optional.of("You do not have permission to access that information.");
            return execute("get_my_report", Map.of());
        }
        boolean ownDepartment = q.contains("my department") || q.contains("my manager") || q.contains("manages me") || q.contains("reporting manager");
        boolean managerQuery = q.contains("manager") || q.contains("manages me") || q.contains("heads my department");
        if (managerQuery && ownDepartment) return execute("get_my_manager", Map.of());
        if (managerQuery && q.matches(".*\\b(manager|head) of\\s+(?!my\\b).*")) return Optional.of("You do not have permission to access that information.");
        if (latest && po) return execute("get_my_latest_purchase_order", Map.of());
        if (latest && pr) return execute("get_my_latest_purchase_request", Map.of());
        if (!po && pr && (q.contains("how many") || q.contains("count"))) return execute("get_my_request_count", Map.of());
        if (po) return execute("get_my_purchase_orders", Map.of());
        if (pr && (q.contains("total") || q.contains("how much") || q.contains("spent"))) return execute("get_my_request_total", Map.of());
        if (pr) return execute("get_my_purchase_requests", Map.of());
        return Optional.empty();
    }
    private String productName(String q) {
        String value = q.replaceAll(".*\\b(?:price|rate|cost|cheapest|vendor|supplier|is|of|for|a|the)\\b", "").replaceAll("[^a-z0-9 ._-]", " ").trim();
        return value.length() >= 2 && value.length() <= 80 ? value : "product";
    }
    public List<String> toolsForAuthenticatedRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return List.of();
        Set<String> authorities = new HashSet<>(); auth.getAuthorities().forEach(a -> authorities.add(a.getAuthority()));
        return ROLES.entrySet().stream().filter(entry -> entry.getValue().stream().anyMatch(authorities::contains)).map(Map.Entry::getKey).sorted().toList();
    }
    private boolean validArguments(String name, Map<String, Object> args) {
        if (PRODUCT_TOOLS.contains(name)) return args.size() == 1 && args.get("product_name") instanceof String s && s.trim().matches("[A-Za-z0-9 ._-]{2,80}");
        if ("get_my_request_by_number".equals(name)) return args.size() == 1 && args.get("request_number") instanceof String s && s.matches("(?i)PR[- ]?\\d{4}[- ]?\\d+");
        return args.isEmpty();
    }
    private String promptFor(String name, Map<String, Object> a) { return switch (name) {
        case "get_my_request_count" -> "how many requests did I make"; case "get_my_purchase_requests" -> "show my requests"; case "get_my_latest_purchase_request" -> "what is my latest request"; case "get_my_request_by_number" -> "status " + a.get("request_number"); case "get_my_request_total" -> "how much have I requested in total"; case "get_my_purchase_orders" -> "show my purchase orders"; case "get_my_invoices" -> "show my invoices";
        case "get_pending_approvals" -> "pending requests"; case "get_department_requests" -> "department requests"; case "get_approved_requests" -> "approved requests"; case "get_rejected_requests" -> "rejected requests";
        case "get_purchase_request_statistics" -> "purchase request statistics"; case "get_purchase_order_statistics" -> "purchase orders"; case "get_product_prices", "get_vendor_product_prices" -> "price of " + a.get("product_name"); case "get_active_vendors" -> "active vendors"; case "get_vendor_performance" -> "vendor performance";
        case "get_pending_invoices" -> "pending invoices"; case "get_paid_invoices", "get_payment_statistics" -> "how much have we paid"; case "get_invoice_status" -> "invoices"; case "get_my_payments" -> "my payments"; case "get_my_delivery_status" -> "my purchase orders";
        case "get_user_statistics" -> "user statistics"; case "get_vendor_statistics" -> "vendor statistics"; case "get_procurement_statistics" -> "procurement statistics"; case "get_finance_statistics" -> "finance statistics"; case "get_department_statistics" -> "department statistics"; default -> ""; }; }
}
