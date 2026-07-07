USE enterprise_procurement;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Roles (5)
INSERT INTO roles (id, name, description) VALUES 
(1, 'Admin', 'System Administrator'),
(2, 'Department Manager', 'Manager of a department'),
(3, 'Employee', 'Regular employee'),
(4, 'Procurement Officer', 'Procurement handler'),
(5, 'Finance Officer', 'Financial controller');

-- 2. Departments (5)
INSERT INTO departments (id, name, code, manager_id) VALUES 
(1, 'Information Technology', 'IT', 1),
(2, 'Human Resources', 'HR', 2),
(3, 'Finance', 'FIN', 3),
(4, 'Marketing', 'MKT', 4),
(5, 'Operations', 'OPS', 5);

-- 3. Users (20)
INSERT INTO users (id, employee_id, first_name, last_name, email, password_hash, role_id, department_id) VALUES 
(1, 'EMP001', 'Alice', 'Smith', 'alice@example.com', 'hash', 1, 1),
(2, 'EMP002', 'Bob', 'Jones', 'bob@example.com', 'hash', 2, 2),
(3, 'EMP003', 'Charlie', 'Brown', 'charlie@example.com', 'hash', 2, 3),
(4, 'EMP004', 'David', 'Miller', 'david@example.com', 'hash', 2, 4),
(5, 'EMP005', 'Eva', 'Davis', 'eva@example.com', 'hash', 2, 5),
(6, 'EMP006', 'Frank', 'Wilson', 'frank@example.com', 'hash', 4, 1),
(7, 'EMP007', 'Grace', 'Taylor', 'grace@example.com', 'hash', 5, 3),
(8, 'EMP008', 'Harry', 'Anderson', 'harry@example.com', 'hash', 3, 1),
(9, 'EMP009', 'Ivy', 'Thomas', 'ivy@example.com', 'hash', 3, 2),
(10, 'EMP010', 'Jack', 'Jackson', 'jack@example.com', 'hash', 3, 3),
(11, 'EMP011', 'Karen', 'White', 'karen@example.com', 'hash', 3, 4),
(12, 'EMP012', 'Leo', 'Harris', 'leo@example.com', 'hash', 3, 5),
(13, 'EMP013', 'Mia', 'Martin', 'mia@example.com', 'hash', 3, 1),
(14, 'EMP014', 'Nick', 'Thompson', 'nick@example.com', 'hash', 3, 2),
(15, 'EMP015', 'Olivia', 'Garcia', 'olivia@example.com', 'hash', 3, 3),
(16, 'EMP016', 'Paul', 'Martinez', 'paul@example.com', 'hash', 3, 4),
(17, 'EMP017', 'Quinn', 'Robinson', 'quinn@example.com', 'hash', 3, 5),
(18, 'EMP018', 'Rose', 'Clark', 'rose@example.com', 'hash', 4, 1),
(19, 'EMP019', 'Sam', 'Rodriguez', 'sam@example.com', 'hash', 5, 3),
(20, 'EMP020', 'Tina', 'Lewis', 'tina@example.com', 'hash', 3, 1);

-- 4. Vendors (10)
INSERT INTO vendors (id, vendor_name, contact_name, email, phone, address, gst_number) VALUES 
(1, 'Tech Supplies Inc.', 'Tom Hardy', 'tom@techsupplies.com', '555-0101', '123 Tech Blvd', 'GST1001'),
(2, 'Office Wonders', 'Wanda Maximoff', 'wanda@officewonders.com', '555-0102', '456 Office Pkwy', 'GST1002'),
(3, 'Global Logistics', 'Luke Cage', 'luke@globallogistics.com', '555-0103', '789 Shipping Way', 'GST1003'),
(4, 'Marketing Pro', 'Peter Parker', 'peter@marketingpro.com', '555-0104', '101 Market St', 'GST1004'),
(5, 'HR Solutions', 'Bruce Banner', 'bruce@hrsolutions.com', '555-0105', '202 People Rd', 'GST1005'),
(6, 'Clean Masters', 'Steve Rogers', 'steve@cleanmasters.com', '555-0106', '303 Shine Ave', 'GST1006'),
(7, 'Security First', 'Natasha Romanoff', 'natasha@securityfirst.com', '555-0107', '404 Guard Ln', 'GST1007'),
(8, 'Catering Co.', 'Tony Stark', 'tony@cateringco.com', '555-0108', '505 Food Ct', 'GST1008'),
(9, 'Hardware Hub', 'Thor Odinson', 'thor@hardwarehub.com', '555-0109', '606 Tool Dr', 'GST1009'),
(10, 'Software Sync', 'Clint Barton', 'clint@softwaresync.com', '555-0110', '707 Code Blvd', 'GST1010');

-- 5. Categories (10)
INSERT INTO categories (id, name, description) VALUES 
(1, 'Laptops', 'Portable computers'),
(2, 'Desktops', 'Stationary computers'),
(3, 'Office Furniture', 'Desks, chairs, etc.'),
(4, 'Stationery', 'Pens, paper, etc.'),
(5, 'Software Licenses', 'OS, Office, etc.'),
(6, 'Networking', 'Routers, switches'),
(7, 'Cleaning Supplies', 'Janitorial goods'),
(8, 'Snacks', 'Pantry items'),
(9, 'Security Systems', 'Cameras, alarms'),
(10, 'Marketing Materials', 'Banners, flyers');

-- 6. Products (30)
INSERT INTO products (id, sku, name, description, category_id) VALUES 
(1, 'LAP-001', 'ProBook 15', '15 inch laptop', 1),
(2, 'LAP-002', 'AirBook 13', '13 inch light laptop', 1),
(3, 'LAP-003', 'DevBook Pro', 'High perf laptop', 1),
(4, 'DSK-001', 'WorkStation X', 'Standard desktop', 2),
(5, 'DSK-002', 'PowerStation Y', 'High perf desktop', 2),
(6, 'DSK-003', 'Mini PC', 'Compact desktop', 2),
(7, 'FURN-001', 'Ergo Chair', 'Ergonomic chair', 3),
(8, 'FURN-002', 'Standing Desk', 'Adjustable desk', 3),
(9, 'FURN-003', 'Meeting Table', 'Large table', 3),
(10, 'STAT-001', 'A4 Paper Ream', '500 sheets', 4),
(11, 'STAT-002', 'Blue Pens (Box)', '50 pens', 4),
(12, 'STAT-003', 'Stapler', 'Standard stapler', 4),
(13, 'SOFT-001', 'OS Pro License', 'Operating System', 5),
(14, 'SOFT-002', 'Office Suite', 'Productivity suite', 5),
(15, 'SOFT-003', 'Antivirus', '1 year license', 5),
(16, 'NET-001', 'WiFi Router', 'Dual band', 6),
(17, 'NET-002', '24-port Switch', 'Gigabit switch', 6),
(18, 'NET-003', 'Cat6 Cable 100m', 'Network cable', 6),
(19, 'CLN-001', 'Glass Cleaner', '5L bottle', 7),
(20, 'CLN-002', 'Floor Polish', '5L bottle', 7),
(21, 'CLN-003', 'Paper Towels', '10 rolls', 7),
(22, 'SNK-001', 'Coffee Beans', '1kg bag', 8),
(23, 'SNK-002', 'Tea Bags', '100 pack', 8),
(24, 'SNK-003', 'Mixed Nuts', '1kg pack', 8),
(25, 'SEC-001', 'Dome Camera', '1080p security cam', 9),
(26, 'SEC-002', 'Motion Sensor', 'PIR sensor', 9),
(27, 'SEC-003', 'Access Card', 'RFID card', 9),
(28, 'MKT-001', 'Rollup Banner', 'Standard size', 10),
(29, 'MKT-002', 'Flyer A5', '1000 pack', 10),
(30, 'MKT-003', 'Branded Pens', '500 pack', 10);

-- 7. Vendor Products (50)
-- Just linking sequentially to ensure we have 50 records
INSERT INTO vendor_products (vendor_id, product_id, price, lead_time_days)
SELECT 
    (MOD(id, 10) + 1) as vendor_id, 
    id as product_id, 
    id * 10.50 as price, 
    (MOD(id, 5) + 1) as lead_time_days
FROM products;

INSERT INTO vendor_products (vendor_id, product_id, price, lead_time_days)
SELECT 
    (MOD(id + 3, 10) + 1) as vendor_id, 
    id as product_id, 
    id * 11.00 as price, 
    (MOD(id, 5) + 2) as lead_time_days
FROM products WHERE id <= 20;

-- 8. Purchase Requests (20)
-- Generate 20 requests with 'Approved' status to allow PO creation downstream
INSERT INTO purchase_requests (id, request_number, requester_id, department_id, justification, status, total_amount)
SELECT 
    n, CONCAT('PR-2023-', LPAD(n, 4, '0')), (MOD(n, 20) + 1), (MOD(n, 5) + 1), CONCAT('Need supplies for Q', MOD(n, 4) + 1), 'Approved', n * 100
FROM (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
      UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20) AS nums;

-- 9. Purchase Request Items (50)
-- Adding items to requests
INSERT INTO purchase_request_items (purchase_request_id, product_id, quantity, estimated_price)
SELECT 
    (MOD(n, 20) + 1), (MOD(n, 30) + 1), (MOD(n, 10) + 1), (n * 5.00)
FROM (SELECT @row := @row + 1 as n FROM (select 0 union all select 1 union all select 2 union all select 3 union all select 4) t1, (select 0 union all select 1 union all select 2 union all select 3 union all select 4) t2, (select 0 union all select 1) t3, (SELECT @row:=0) t0) b;

-- 10. Approvals (20)
INSERT INTO approvals (purchase_request_id, level, approver_id, status, comments)
SELECT 
    pr.id, 1, manager_id, 'Approved', 'Looks good to me'
FROM purchase_requests pr
JOIN departments d ON pr.department_id = d.id;

-- 11. Purchase Orders (20)
INSERT INTO purchase_orders (id, purchase_order_number, purchase_request_id, vendor_id, status, total_amount, expected_delivery_date)
SELECT 
    id, CONCAT('PO-', request_number), id, (MOD(id, 10) + 1), 'Delivered', total_amount, DATE_ADD(NOW(), INTERVAL 14 DAY)
FROM purchase_requests;

-- 12. Purchase Order Items (50)
INSERT INTO purchase_order_items (purchase_order_id, product_id, quantity, unit_price, total_price)
SELECT 
    purchase_request_id, product_id, quantity, estimated_price, (quantity * estimated_price)
FROM purchase_request_items;

-- 13. Deliveries (20)
INSERT INTO deliveries (id, purchase_order_id, delivery_note_number, delivery_date, status, carrier, tracking_number)
SELECT 
    id, id, CONCAT('DN-', id), DATE_ADD(NOW(), INTERVAL 5 DAY), 'Delivered', 'FastShip', CONCAT('TRK', id, '999')
FROM purchase_orders;

-- 14. Receipts (20)
INSERT INTO receipts (id, delivery_id, receiver_id, receipt_date, condition_notes)
SELECT 
    id, id, 1, DATE_ADD(NOW(), INTERVAL 6 DAY), 'Received in good condition'
FROM deliveries;

-- 15. Invoices (20)
INSERT INTO invoices (id, invoice_number, receipt_id, vendor_id, invoice_date, due_date, total_amount, status)
SELECT 
    r.id, CONCAT('INV-', r.id), r.id, po.vendor_id, DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 37 DAY), po.total_amount, 'Paid'
FROM receipts r
JOIN deliveries d ON r.delivery_id = d.id
JOIN purchase_orders po ON d.purchase_order_id = po.id;

-- 16. Payments (20)
INSERT INTO payments (id, invoice_id, payment_reference, amount_paid, payment_date, payment_method, status)
SELECT 
    id, id, CONCAT('PAY-', id), total_amount, DATE_ADD(NOW(), INTERVAL 15 DAY), 'Bank Transfer', 'Paid'
FROM invoices;

-- 17. Audit Logs (100)
INSERT INTO audit_logs (user_id, action, table_name, record_id, new_value)
SELECT 
    (MOD(n, 20) + 1), 'INSERT', 'purchase_requests', n, '{"status": "Draft"}'
FROM (SELECT @row2 := @row2 + 1 as n FROM (select 0 union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) t1, (select 0 union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) t2, (SELECT @row2:=0) t0) b;

-- 18. Notifications (50)
INSERT INTO notifications (user_id, type, subject, message)
SELECT 
    (MOD(n, 20) + 1), 'System', 'System Update', CONCAT('This is notification message number ', n)
FROM (SELECT @row3 := @row3 + 1 as n FROM (select 0 union all select 1 union all select 2 union all select 3 union all select 4) t1, (select 0 union all select 1 union all select 2 union all select 3 union all select 4) t2, (select 0 union all select 1) t3, (SELECT @row3:=0) t0) b;

-- 19. Supplier Performance (20)
INSERT INTO supplier_performance (vendor_id, purchase_order_id, quality_rating, delivery_rating, pricing_rating, comments)
SELECT 
    po.vendor_id, po.id, (MOD(po.id, 5) + 1), (MOD(po.id + 1, 5) + 1), (MOD(po.id + 2, 5) + 1), 'Good supplier'
FROM purchase_orders po;

-- 20. Supplier Compliance (20)
INSERT INTO supplier_compliance (vendor_id, document_type, document_url, expiry_date, status)
SELECT 
    (MOD(n, 10) + 1), 'ISO 9001', CONCAT('http://docs.example.com/comp/', n), DATE_ADD(NOW(), INTERVAL 365 DAY), 'Valid'
FROM (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
      UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20) AS nums;

SET FOREIGN_KEY_CHECKS = 1;
