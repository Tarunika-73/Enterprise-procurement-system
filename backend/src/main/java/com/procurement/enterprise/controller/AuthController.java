package com.procurement.enterprise.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: implement endpoints for AuthController.
 * Placeholder created to match the agreed package structure.
 */
@RestController
@RequestMapping("/api/auths")
public class AuthController {

    @GetMapping
    public String placeholder() {
        return "AuthController is not yet implemented";
    }
}
