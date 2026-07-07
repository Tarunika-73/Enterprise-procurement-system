USE enterprise_procurement;

-- 1. vw_pending_requests
CREATE OR REPLACE VIEW vw_pending_requests AS
SELECT 
    pr.id,
    pr.request_number,
    pr.justification,
    pr.status,
    pr.total_amount,
    pr.created_at,
    u.first_name AS requester_first_name,
    u.last_name AS requester_last_name,
    d.name AS department_name
FROM purchase_requests pr
JOIN users u ON pr.requester_id = u.id
JOIN departments d ON pr.department_id = d.id
WHERE pr.status = 'Pending' AND pr.is_deleted = FALSE;

-- 2. vw_purchase_order_summary
CREATE OR REPLACE VIEW vw_purchase_order_summary AS
SELECT 
    po.id,
    po.purchase_order_number,
    po.status,
    po.total_amount,
    po.expected_delivery_date,
    po.created_at,
    v.vendor_name,
    pr.request_number
FROM purchase_orders po
JOIN vendors v ON po.vendor_id = v.id
JOIN purchase_requests pr ON po.purchase_request_id = pr.id
WHERE po.is_deleted = FALSE;

-- 3. vw_vendor_products
CREATE OR REPLACE VIEW vw_vendor_products AS
SELECT 
    vp.id AS vendor_product_id,
    v.vendor_name,
    p.sku,
    p.name AS product_name,
    c.name AS category_name,
    vp.price,
    vp.lead_time_days,
    vp.is_active
FROM vendor_products vp
JOIN vendors v ON vp.vendor_id = v.id
JOIN products p ON vp.product_id = p.id
JOIN categories c ON p.category_id = c.id
WHERE vp.is_deleted = FALSE AND v.is_deleted = FALSE AND p.is_deleted = FALSE;

-- 4. vw_department_spending
CREATE OR REPLACE VIEW vw_department_spending AS
SELECT 
    d.id AS department_id,
    d.name AS department_name,
    COUNT(pr.id) AS total_requests,
    SUM(pr.total_amount) AS total_spend
FROM departments d
LEFT JOIN purchase_requests pr ON d.id = pr.department_id AND pr.status IN ('Approved', 'Closed') AND pr.is_deleted = FALSE
WHERE d.is_deleted = FALSE
GROUP BY d.id, d.name;

-- 5. vw_vendor_spending
CREATE OR REPLACE VIEW vw_vendor_spending AS
SELECT 
    v.id AS vendor_id,
    v.vendor_name,
    COUNT(po.id) AS total_purchase_orders,
    SUM(po.total_amount) AS total_spend
FROM vendors v
LEFT JOIN purchase_orders po ON v.id = po.vendor_id AND po.status NOT IN ('Cancelled', 'Rejected') AND po.is_deleted = FALSE
WHERE v.is_deleted = FALSE
GROUP BY v.id, v.vendor_name;

-- 6. vw_monthly_spending
CREATE OR REPLACE VIEW vw_monthly_spending AS
SELECT 
    YEAR(payment_date) AS spend_year,
    MONTH(payment_date) AS spend_month,
    SUM(amount_paid) AS total_spent
FROM payments
WHERE status = 'Paid' AND is_deleted = FALSE
GROUP BY YEAR(payment_date), MONTH(payment_date)
ORDER BY spend_year DESC, spend_month DESC;

-- 7. vw_top_vendors
CREATE OR REPLACE VIEW vw_top_vendors AS
SELECT 
    v.id AS vendor_id,
    v.vendor_name,
    SUM(po.total_amount) AS total_order_value,
    COUNT(po.id) AS order_count
FROM vendors v
JOIN purchase_orders po ON v.id = po.vendor_id
WHERE po.status NOT IN ('Cancelled', 'Rejected') AND po.is_deleted = FALSE AND v.is_deleted = FALSE
GROUP BY v.id, v.vendor_name
ORDER BY total_order_value DESC;

-- 8. vw_product_purchase_summary
CREATE OR REPLACE VIEW vw_product_purchase_summary AS
SELECT 
    p.id AS product_id,
    p.sku,
    p.name AS product_name,
    SUM(poi.quantity) AS total_quantity_purchased,
    SUM(poi.total_price) AS total_spend_on_product
FROM products p
JOIN purchase_order_items poi ON p.id = poi.product_id
JOIN purchase_orders po ON poi.purchase_order_id = po.id
WHERE po.status NOT IN ('Cancelled', 'Rejected') AND po.is_deleted = FALSE AND poi.is_deleted = FALSE
GROUP BY p.id, p.sku, p.name;

-- 9. vw_pending_payments
CREATE OR REPLACE VIEW vw_pending_payments AS
SELECT 
    i.id AS invoice_id,
    i.invoice_number,
    i.invoice_date,
    i.due_date,
    i.total_amount,
    i.status AS invoice_status,
    v.vendor_name
FROM invoices i
JOIN vendors v ON i.vendor_id = v.id
WHERE i.status IN ('Pending', 'Partially Paid') AND i.is_deleted = FALSE;

-- 10. vw_supplier_performance
CREATE OR REPLACE VIEW vw_supplier_performance AS
SELECT 
    v.id AS vendor_id,
    v.vendor_name,
    AVG(sp.quality_rating) AS avg_quality,
    AVG(sp.delivery_rating) AS avg_delivery,
    AVG(sp.pricing_rating) AS avg_pricing,
    (AVG(sp.quality_rating) + AVG(sp.delivery_rating) + AVG(sp.pricing_rating)) / 3 AS overall_rating,
    COUNT(sp.id) AS total_reviews
FROM vendors v
LEFT JOIN supplier_performance sp ON v.id = sp.vendor_id AND sp.is_deleted = FALSE
WHERE v.is_deleted = FALSE
GROUP BY v.id, v.vendor_name;
