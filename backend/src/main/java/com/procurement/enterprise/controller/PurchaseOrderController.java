package com.procurement.enterprise.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: implement endpoints for PurchaseOrderController.
 * Placeholder created to match the agreed package structure.
 */
@RestController
@RequestMapping("/api/purchaseorders")
public class PurchaseOrderController {

    @GetMapping
    public String placeholder() {
        return "PurchaseOrderController is not yet implemented";
    }
}
