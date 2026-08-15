package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.response.ReportSummaryResponse;
import com.procurement.enterprise.service.ReportService;
import com.procurement.enterprise.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Reports page data, scoped to the currently authenticated user's role:
 * Employees see their own activity, Managers see their department, and
 * Procurement Officers / Finance / Admin see the whole organization.
 */
@RestController
@RequestMapping({
        "/v1/reports",
        "/reports"
})
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'PROCUREMENT_OFFICER', 'FINANCE', 'ADMIN')")
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ReportSummaryResponse>> getSummary() {
        log.info("Report summary requested");
        return ResponseEntity.ok(ApiResponse.success("Report summary fetched", reportService.getSummary()));
    }

    /**
     * Downloads the current, role-scoped Reports page data as an Excel-compatible report.
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadReport() {
        log.info("Report download requested");

        String report = reportService.generateReportCsv();
        byte[] body = report.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        String filename = "EPS_Procurement_Report_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".xls";
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(disposition);
        headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel; charset=UTF-8"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(body);
    }
}
