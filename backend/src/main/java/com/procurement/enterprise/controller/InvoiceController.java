package com.procurement.enterprise.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: implement endpoints for InvoiceController.
 * Placeholder created to match the agreed package structure.
 */
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @GetMapping
    public String placeholder() {
        return "InvoiceController is not yet implemented";
    }
}
