package com.procurement.enterprise.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: implement endpoints for PurchaseRequestController.
 * Placeholder created to match the agreed package structure.
 */
@RestController
@RequestMapping("/api/purchaserequests")
public class PurchaseRequestController {

    @GetMapping
    public String placeholder() {
        return "PurchaseRequestController is not yet implemented";
    }
}
