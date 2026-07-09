package com.procurement.enterprise.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: implement endpoints for PaymentController.
 * Placeholder created to match the agreed package structure.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @GetMapping
    public String placeholder() {
        return "PaymentController is not yet implemented";
    }
}
