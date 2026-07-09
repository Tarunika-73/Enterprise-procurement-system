package com.procurement.enterprise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Quick sanity-check endpoint for backend setup.
 * GET /api/health -> confirms the app is running and the DB connection works.
 * Delete or move this once real endpoints exist, if you'd rather not keep it.
 */
@RestController
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");

        try (Connection connection = dataSource.getConnection()) {
            result.put("database", "CONNECTED");
            result.put("databaseProduct", connection.getMetaData().getDatabaseProductName());
        } catch (Exception e) {
            result.put("database", "DISCONNECTED");
            result.put("error", e.getMessage());
        }

        return result;
    }
}
