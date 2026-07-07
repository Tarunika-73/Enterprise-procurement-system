USE enterprise_procurement;

-- Example Queries

-- 1. Get all pending purchase requests with requester details
SELECT * FROM vw_pending_requests;

-- 2. Find the total spend per department
SELECT * FROM vw_department_spending;

-- 3. Get top 5 vendors by spend
CALL sp_top_vendors_report(5);

-- 4. Calculate total outstanding balance for an invoice
-- Example using function for invoice ID 1
SELECT invoice_number, total_amount, fn_calculate_invoice_balance(id) AS balance
FROM invoices
WHERE id = 1;

-- 5. List all vendor products with prices and lead times
SELECT * FROM vw_vendor_products;

-- 6. Get overall rating for a specific vendor (Example: Vendor ID 1)
SELECT vendor_name, fn_vendor_rating_average(1) AS average_rating
FROM vendors 
WHERE id = 1;

-- 7. View purchase order summary
SELECT * FROM vw_purchase_order_summary;

-- 8. Get monthly procurement report for the current year and month
CALL sp_monthly_procurement_report(YEAR(CURDATE()), MONTH(CURDATE()));

-- 9. List pending payments
SELECT * FROM vw_pending_payments;

-- 10. Check audit logs for recent updates
SELECT * FROM audit_logs ORDER BY created_at DESC LIMIT 10;
