package com.procurement.enterprise.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: implement endpoints for UserController.
 * Placeholder created to match the agreed package structure.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public String placeholder() {
        return "UserController is not yet implemented";
    }
}
