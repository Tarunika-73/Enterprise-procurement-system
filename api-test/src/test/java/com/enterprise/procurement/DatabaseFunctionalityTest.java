package com.enterprise.procurement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DatabaseFunctionalityTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void verifyTableData() {
        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        assertTrue(userCount > 0, "Users table is empty! Did you run sample_data.sql?");
        System.out.println("\n✅ Table Check: users table has " + userCount + " rows.");

        Integer vendorCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vendors", Integer.class);
        assertTrue(vendorCount > 0, "Vendors table is empty!");
        System.out.println("✅ Table Check: vendors table has " + vendorCount + " rows.");
        
        Integer poCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM purchase_orders", Integer.class);
        assertTrue(poCount > 0, "Purchase Orders table is empty!");
        System.out.println("✅ Table Check: purchase_orders table has " + poCount + " rows.\n");
    }

    @Test
    public void verifyViewFunctionality() {
        Integer requestSummaryCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vw_purchase_order_summary", Integer.class);
        assertNotNull(requestSummaryCount, "vw_purchase_order_summary should execute successfully");
        System.out.println("✅ View Check: vw_purchase_order_summary executed successfully.");
    }

    @Test
    public void verifyFunctionExecution() {
        Double total = jdbcTemplate.queryForObject("SELECT fn_calculate_request_total(1)", Double.class);
        assertNotNull(total, "Function fn_calculate_request_total should execute successfully");
        System.out.println("✅ Function Check: fn_calculate_request_total(1) executed successfully! Result: $" + total);
    }
}
