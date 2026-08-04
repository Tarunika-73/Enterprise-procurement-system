package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.ReportSummaryResponse;

public interface ReportService {

    /**
     * Builds a Reports page summary scoped to the currently authenticated
     * user: Employees see their own activity, Managers see their
     * department, and Procurement Officers / Finance / Admin see the whole
     * organization.
     */
    ReportSummaryResponse getSummary();

    /**
     * Renders the same role-scoped data as {@link #getSummary()} into a CSV
     * document, for the "Download Report" action on the Reports page.
     */
    String generateReportCsv();
}
