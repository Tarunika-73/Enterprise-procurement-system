package com.procurement.enterprise.controller;
import com.procurement.enterprise.dto.request.CreateReceiptRequest;
import com.procurement.enterprise.dto.response.ReceiptResponse;
import com.procurement.enterprise.service.GoodsReceiptService;
import com.procurement.enterprise.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
@RestController @RequestMapping("/v1/receipts") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PROCUREMENT_OFFICER', 'ADMIN')")
public class ReceiptController { private final GoodsReceiptService service;
 @GetMapping("/workflow") public ResponseEntity<ApiResponse<List<com.procurement.enterprise.dto.response.GoodsReceiptWorkflowResponse>>> workflows() { return ResponseEntity.ok(ApiResponse.success("Goods receipt workflows fetched.", service.getDeliveredReceiptWorkflows())); }
 @GetMapping("/workflow/{deliveryId}") public ResponseEntity<ApiResponse<com.procurement.enterprise.dto.response.GoodsReceiptWorkflowResponse>> workflow(@PathVariable Long deliveryId) { return ResponseEntity.ok(ApiResponse.success("Goods receipt workflow fetched.", service.getReceiptWorkflow(deliveryId))); }
 @PostMapping public ResponseEntity<ApiResponse<ReceiptResponse>> create(@Valid @RequestBody CreateReceiptRequest request) { return ResponseEntity.status(201).body(ApiResponse.success("Goods receipt created.", service.create(request))); }
 @GetMapping("/{receiptId}/pdf") public ResponseEntity<byte[]> downloadPdf(@PathVariable Long receiptId) {
     byte[] pdf = service.generatePdf(receiptId);
     String filename = String.format("Goods-Receipt-GRN-%06d.pdf", receiptId);
     return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
             .body(pdf);
 }
}
