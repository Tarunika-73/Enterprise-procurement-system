-- Safe, re-runnable recommendation-engine data for the live enterprise_procurement database.
-- This script never drops, truncates, or bulk-deletes application data. It uses existing
-- products and users, preserves ABC TECH, and resolves all new relationships by natural keys.

USE enterprise_procurement;
START TRANSACTION;

-- The current vendor is retained. The remaining suppliers are Chennai-area demonstration data.
CREATE TEMPORARY TABLE recommendation_vendor_seed (
    vendor_name VARCHAR(150), contact_name VARCHAR(100), email VARCHAR(100),
    phone VARCHAR(20), address TEXT, gst_number VARCHAR(50)
);
INSERT INTO recommendation_vendor_seed VALUES
('Chennai IT Solutions', 'Arun Kumar', 'sales@chennaicitsolutions.in', '044-4218-2101', 'Anna Nagar, Chennai, Tamil Nadu', '33AAEFC2101A1Z1'),
('Coromandel Office Mart', 'Meera Iyer', 'orders@coromandeloffice.in', '044-2815-1022', 'T. Nagar, Chennai, Tamil Nadu', '33AAEFC1022B1Z2'),
('South India Network Systems', 'Vijay Raman', 'sales@sinsnetworks.in', '044-2461-3303', 'Guindy, Chennai, Tamil Nadu', '33AAEFS3303C1Z3'),
('Marina Furniture Works', 'Kavitha Raj', 'hello@marinafurniture.in', '044-2498-4404', 'Mylapore, Chennai, Tamil Nadu', '33AAEFM4404D1Z4'),
('Kaveri CleanCare Supplies', 'S. Prakash', 'sales@kavericleancare.in', '044-2650-5505', 'Ambattur, Chennai, Tamil Nadu', '33AAEFK5505E1Z5'),
('Chola Software Licensing', 'Nandhini S', 'licenses@cholasoftware.in', '044-2836-6606', 'Nungambakkam, Chennai, Tamil Nadu', '33AAEFC6606F1Z6'),
('Mylapore Security Systems', 'R. Suresh', 'sales@mylaporesecurity.in', '044-2499-7707', 'Mylapore, Chennai, Tamil Nadu', '33AAEFM7707G1Z7'),
('Bayleaf Pantry Suppliers', 'Divya Menon', 'orders@bayleafpantry.in', '044-2371-8808', 'Velachery, Chennai, Tamil Nadu', '33AAEFB8808H1Z8'),
('Adyar Print and Promotions', 'Karthik Bala', 'sales@adyarprint.in', '044-2442-9909', 'Adyar, Chennai, Tamil Nadu', '33AAEFA9909J1Z9');

UPDATE vendors v
JOIN recommendation_vendor_seed s ON s.email = v.email
SET v.vendor_name = s.vendor_name, v.contact_name = s.contact_name, v.phone = s.phone,
    v.address = s.address, v.gst_number = s.gst_number, v.is_active = TRUE, v.is_deleted = FALSE;

INSERT INTO vendors (vendor_name, contact_name, email, phone, address, gst_number, password_hash, is_active, is_deleted)
SELECT s.vendor_name, s.contact_name, s.email, s.phone, s.address, s.gst_number,
       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE, FALSE
FROM recommendation_vendor_seed s
LEFT JOIN vendors v ON v.email = s.email
WHERE v.id IS NULL;

-- Populate inventory for the existing ABC TECH offers without changing their valid prices.
UPDATE vendor_products
SET available_quantity = 80 + MOD(product_id * 47, 321), is_active = TRUE, is_deleted = FALSE
WHERE vendor_id = (SELECT id FROM vendors WHERE vendor_name = 'ABC TECH' AND is_deleted = FALSE LIMIT 1)
  AND available_quantity IS NULL;

-- Each offer is resolved by vendor email and existing product name. Products are deliberately
-- assigned only to relevant suppliers; every listed product receives ABC TECH plus two peers.
CREATE TEMPORARY TABLE recommendation_offer_seed (
    vendor_email VARCHAR(100), product_name VARCHAR(150), price_multiplier DECIMAL(5,3),
    lead_time_days INT, available_quantity INT
);
INSERT INTO recommendation_offer_seed VALUES
('sales@chennaicitsolutions.in','ProBook 15',0.940,4,115), ('sales@sinsnetworks.in','ProBook 15',0.980,6,62),
('sales@chennaicitsolutions.in','AirBook 13',0.930,4,96), ('sales@sinsnetworks.in','AirBook 13',1.010,6,45),
('sales@chennaicitsolutions.in','DevBook Pro',0.950,5,71), ('sales@sinsnetworks.in','DevBook Pro',1.030,7,38),
('sales@chennaicitsolutions.in','WorkStation X',0.920,4,83), ('sales@sinsnetworks.in','WorkStation X',1.020,7,41),
('sales@chennaicitsolutions.in','PowerStation Y',0.940,5,57), ('sales@sinsnetworks.in','PowerStation Y',1.010,7,29),
('sales@chennaicitsolutions.in','Mini PC',0.910,4,102), ('sales@sinsnetworks.in','Mini PC',1.040,6,48),
('orders@coromandeloffice.in','Ergo Chair',0.930,5,76), ('hello@marinafurniture.in','Ergo Chair',1.020,8,44),
('orders@coromandeloffice.in','Standing Desk',0.920,5,54), ('hello@marinafurniture.in','Standing Desk',1.030,8,32),
('orders@coromandeloffice.in','Meeting Table',0.940,6,35), ('hello@marinafurniture.in','Meeting Table',1.010,9,18),
('orders@coromandeloffice.in','A4 Paper Ream',0.900,2,760), ('sales@kavericleancare.in','A4 Paper Ream',0.960,3,420),
('orders@coromandeloffice.in','Blue Pens (Box)',0.910,2,690), ('sales@adyarprint.in','Blue Pens (Box)',0.980,3,365),
('orders@coromandeloffice.in','Stapler',0.920,2,310), ('sales@adyarprint.in','Stapler',1.040,3,175),
('licenses@cholasoftware.in','OS Pro License',0.900,1,140), ('sales@chennaicitsolutions.in','OS Pro License',0.970,2,88),
('licenses@cholasoftware.in','Office Suite',0.910,1,125), ('sales@chennaicitsolutions.in','Office Suite',0.980,2,74),
('licenses@cholasoftware.in','Antivirus',0.890,1,240), ('sales@chennaicitsolutions.in','Antivirus',0.960,2,155),
('sales@sinsnetworks.in','WiFi Router',0.910,3,105), ('sales@chennaicitsolutions.in','WiFi Router',0.980,5,61),
('sales@sinsnetworks.in','24-port Switch',0.930,4,47), ('sales@chennaicitsolutions.in','24-port Switch',1.010,6,26),
('sales@sinsnetworks.in','Cat6 Cable 100m',0.900,3,280), ('sales@chennaicitsolutions.in','Cat6 Cable 100m',0.970,4,170),
('sales@kavericleancare.in','Glass Cleaner',0.920,2,215), ('orders@coromandeloffice.in','Glass Cleaner',0.980,3,124),
('sales@kavericleancare.in','Floor Polish',0.910,2,178), ('orders@coromandeloffice.in','Floor Polish',0.970,3,96),
('sales@kavericleancare.in','Paper Towels',0.900,2,530), ('orders@coromandeloffice.in','Paper Towels',0.960,3,340),
('orders@bayleafpantry.in','Coffee Beans',0.930,2,92), ('orders@coromandeloffice.in','Coffee Beans',1.010,3,54),
('orders@bayleafpantry.in','Tea Bags',0.910,2,210), ('orders@coromandeloffice.in','Tea Bags',0.980,3,126),
('orders@bayleafpantry.in','Mixed Nuts',0.940,2,118), ('orders@coromandeloffice.in','Mixed Nuts',1.020,3,66),
('sales@mylaporesecurity.in','Dome Camera',0.920,4,73), ('sales@sinsnetworks.in','Dome Camera',0.990,6,42),
('sales@mylaporesecurity.in','Motion Sensor',0.910,4,135), ('sales@sinsnetworks.in','Motion Sensor',0.980,6,82),
('sales@mylaporesecurity.in','Access Card',0.900,2,640), ('sales@sinsnetworks.in','Access Card',0.970,4,380),
('sales@adyarprint.in','Rollup Banner',0.920,4,94), ('orders@coromandeloffice.in','Rollup Banner',0.990,5,58),
('sales@adyarprint.in','Flyer A5',0.900,3,1800), ('orders@coromandeloffice.in','Flyer A5',0.970,4,1120),
('sales@adyarprint.in','Branded Pens',0.930,3,920), ('orders@coromandeloffice.in','Branded Pens',1.010,4,540);

INSERT INTO vendor_products (vendor_id, product_id, price, lead_time_days, available_quantity, is_active, is_deleted)
SELECT v.id, p.id, ROUND(base.price * o.price_multiplier, 2), o.lead_time_days,
       o.available_quantity, TRUE, FALSE
FROM recommendation_offer_seed o
JOIN vendors v ON v.email = o.vendor_email
JOIN products p ON p.name = o.product_name AND p.is_deleted = FALSE
JOIN vendor_products base ON base.vendor_id = (SELECT id FROM vendors WHERE vendor_name = 'ABC TECH' AND is_deleted = FALSE LIMIT 1)
    AND base.product_id = p.id AND base.is_deleted = FALSE
ON DUPLICATE KEY UPDATE price = VALUES(price), lead_time_days = VALUES(lead_time_days),
    available_quantity = VALUES(available_quantity), is_active = TRUE, is_deleted = FALSE;

-- One historic, valid PO per added supplier. Requests use existing user/department keys;
-- orders, items, and performance are all looked up by their stable seed numbers on reruns.
CREATE TEMPORARY TABLE recommendation_history_seed (
    vendor_email VARCHAR(100), request_number VARCHAR(50), po_number VARCHAR(50), product_name VARCHAR(150),
    quantity INT, unit_price DECIMAL(10,2), po_status VARCHAR(20), quality_rating INT,
    delivery_rating INT, pricing_rating INT, comments TEXT
);
INSERT INTO recommendation_history_seed VALUES
('orders@coromandeloffice.in','REC-HIST-PR-001','REC-HIST-PO-001','Ergo Chair',12,11160.00,'CLOSED',4,4,5,'Good value and dependable office furniture supply.'),
('sales@sinsnetworks.in','REC-HIST-PR-002','REC-HIST-PO-002','WiFi Router',18,3185.00,'DELIVERED',4,5,4,'Network equipment delivered ahead of schedule.'),
('sales@adyarprint.in','REC-HIST-PR-003','REC-HIST-PO-003','Rollup Banner',25,2300.00,'CLOSED',4,4,4,'Campaign material quality met requirements.'),
('hello@marinafurniture.in','REC-HIST-PR-004','REC-HIST-PO-004','Standing Desk',8,18540.00,'DELIVERED',5,3,3,'Excellent build quality; delivery was slightly delayed.'),
('sales@kavericleancare.in','REC-HIST-PR-005','REC-HIST-PO-005','Glass Cleaner',90,414.00,'CLOSED',4,5,5,'Cleaning consumables were competitively priced.'),
('licenses@cholasoftware.in','REC-HIST-PR-006','REC-HIST-PO-006','Office Suite',20,11375.00,'DELIVERED',5,5,4,'License activation and support were excellent.'),
('sales@mylaporesecurity.in','REC-HIST-PR-007','REC-HIST-PO-007','Dome Camera',10,3864.00,'CLOSED',5,4,3,'High quality security hardware at premium pricing.'),
('orders@bayleafpantry.in','REC-HIST-PR-008','REC-HIST-PO-008','Coffee Beans',30,790.50,'DELIVERED',3,4,4,'Pantry supplies were fresh and delivered reliably.'),
('sales@chennaicitsolutions.in','REC-HIST-PR-009','REC-HIST-PO-009','ProBook 15',3,61100.00,'ACCEPTED',4,3,4,'Order accepted; delivery is pending for comparison testing.');

INSERT INTO purchase_requests (request_number, requester_id, department_id, justification, title, priority,
    status, total_amount, expected_delivery_date, is_deleted)
SELECT h.request_number, (SELECT MIN(id) FROM users WHERE is_deleted = FALSE),
       (SELECT MIN(id) FROM departments WHERE is_deleted = FALSE),
       'Historical recommendation-engine procurement data.', 'Recommendation history seed', 'NORMAL',
       'CLOSED', h.quantity * h.unit_price, DATE_SUB(CURDATE(), INTERVAL 30 DAY), FALSE
FROM recommendation_history_seed h
ON DUPLICATE KEY UPDATE status = VALUES(status), total_amount = VALUES(total_amount), is_deleted = FALSE;

INSERT INTO purchase_orders (purchase_order_number, purchase_request_id, vendor_id, status, total_amount,
    expected_delivery_date, is_deleted)
SELECT h.po_number, pr.id, v.id, h.po_status, h.quantity * h.unit_price,
       DATE_SUB(CURDATE(), INTERVAL 15 DAY), FALSE
FROM recommendation_history_seed h
JOIN purchase_requests pr ON pr.request_number = h.request_number
JOIN vendors v ON v.email = h.vendor_email
ON DUPLICATE KEY UPDATE vendor_id = VALUES(vendor_id), status = VALUES(status),
    total_amount = VALUES(total_amount), is_deleted = FALSE;

UPDATE purchase_order_items poi
JOIN purchase_orders po ON po.id = poi.purchase_order_id
JOIN recommendation_history_seed h ON h.po_number = po.purchase_order_number
JOIN products p ON p.name = h.product_name
SET poi.product_id = p.id, poi.quantity = h.quantity, poi.unit_price = h.unit_price,
    poi.total_price = h.quantity * h.unit_price, poi.is_deleted = FALSE;

INSERT INTO purchase_order_items (purchase_order_id, product_id, quantity, unit_price, total_price, is_deleted)
SELECT po.id, p.id, h.quantity, h.unit_price, h.quantity * h.unit_price, FALSE
FROM recommendation_history_seed h
JOIN purchase_orders po ON po.purchase_order_number = h.po_number
JOIN products p ON p.name = h.product_name
LEFT JOIN purchase_order_items poi ON poi.purchase_order_id = po.id AND poi.product_id = p.id
WHERE poi.id IS NULL;

UPDATE supplier_performance sp
JOIN purchase_orders po ON po.id = sp.purchase_order_id
JOIN recommendation_history_seed h ON h.po_number = po.purchase_order_number
JOIN vendors v ON v.email = h.vendor_email
SET sp.vendor_id = v.id, sp.quality_rating = h.quality_rating, sp.delivery_rating = h.delivery_rating,
    sp.pricing_rating = h.pricing_rating, sp.comments = h.comments, sp.is_deleted = FALSE
WHERE sp.vendor_id = v.id;

INSERT INTO supplier_performance (vendor_id, purchase_order_id, quality_rating, delivery_rating, pricing_rating, comments, is_deleted)
SELECT v.id, po.id, h.quality_rating, h.delivery_rating, h.pricing_rating, h.comments, FALSE
FROM recommendation_history_seed h
JOIN vendors v ON v.email = h.vendor_email
JOIN purchase_orders po ON po.purchase_order_number = h.po_number
LEFT JOIN supplier_performance sp ON sp.vendor_id = v.id AND sp.purchase_order_id = po.id
WHERE sp.id IS NULL;

DROP TEMPORARY TABLE recommendation_history_seed;
DROP TEMPORARY TABLE recommendation_offer_seed;
DROP TEMPORARY TABLE recommendation_vendor_seed;
COMMIT;
