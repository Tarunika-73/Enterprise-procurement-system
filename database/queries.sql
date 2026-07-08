USE enterprise_procurement;

-- Example Queries

-- 1. Get all pending purchase requests with requester details
SELECT * FROM vw_pending_requests;

-- 2. Find the total spend per department
SELECT * FROM vw_department_spending;

-- 3. Get top 5 vendors by spend
CALL sp_top_vendors_report(5);

-- 4. Calculate total outstanding balance for an invoice
SELECT invoice_number,
       total_amount,
       fn_calculate_invoice_balance(id) AS balance
FROM invoices
WHERE id = 1;

-- 5. List all vendor products with prices and lead times
SELECT * FROM vw_vendor_products;

-- 6. View purchase order summary
SELECT * FROM vw_purchase_order_summary;

-- 7. Get monthly procurement report for the current year and month
CALL sp_monthly_procurement_report(YEAR(CURDATE()), MONTH(CURDATE()));

-- 8. List pending payments
SELECT * FROM vw_pending_payments;

-- 9. Check audit logs for recent updates
SELECT * FROM audit_logs
ORDER BY created_at DESC
LIMIT 10;