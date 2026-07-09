package com.procurement.enterprise.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: implement endpoints for ReceiptController.
 * Placeholder created to match the agreed package structure.
 */
@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    @GetMapping
    public String placeholder() {
        return "ReceiptController is not yet implemented";
    }
}
