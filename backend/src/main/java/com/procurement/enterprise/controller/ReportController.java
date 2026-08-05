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
     * Downloads the current, role-scoped Reports page data as a CSV file.
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadReport() {
        log.info("Report download requested");

        String csv = reportService.generateReportCsv();
        byte[] body = csv.getBytes(StandardCharsets.UTF_8);

        String filename = "report_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".csv";
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(disposition);
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(body);
    }
}
