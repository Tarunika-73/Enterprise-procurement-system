USE enterprise_procurement;

DELIMITER //

-- 1. Create Purchase Request
CREATE PROCEDURE sp_create_purchase_request(
    IN p_requester_id BIGINT,
    IN p_department_id BIGINT,
    IN p_justification TEXT,
    OUT p_new_request_id BIGINT
)
BEGIN
    DECLARE v_request_number VARCHAR(50);
    
    -- Generate simple request number
    SET v_request_number = CONCAT('PR-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', FLOOR(RAND() * 10000));
    
    INSERT INTO purchase_requests (request_number, requester_id, department_id, justification, status)
    VALUES (v_request_number, p_requester_id, p_department_id, p_justification, 'Draft');
    
    SET p_new_request_id = LAST_INSERT_ID();
END //

-- 2. Approve Request
CREATE PROCEDURE sp_approve_request(
    IN p_request_id BIGINT,
    IN p_approver_id BIGINT,
    IN p_comments TEXT
)
BEGIN
    -- Update approval record
    UPDATE approvals 
    SET status = 'Approved', approver_id = p_approver_id, comments = p_comments
    WHERE purchase_request_id = p_request_id AND status = 'Pending';
    
    -- Update request status
    UPDATE purchase_requests 
    SET status = 'Approved' 
    WHERE id = p_request_id;
    
    -- Insert history
    INSERT INTO approval_history (approval_id, action_by_id, action_taken, comments)
    SELECT id, p_approver_id, 'Approved', p_comments
    FROM approvals 
    WHERE purchase_request_id = p_request_id AND approver_id = p_approver_id;
END //

-- 3. Reject Request
CREATE PROCEDURE sp_reject_request(
    IN p_request_id BIGINT,
    IN p_approver_id BIGINT,
    IN p_comments TEXT
)
BEGIN
    -- Update approval record
    UPDATE approvals 
    SET status = 'Rejected', approver_id = p_approver_id, comments = p_comments
    WHERE purchase_request_id = p_request_id AND status = 'Pending';
    
    -- Update request status
    UPDATE purchase_requests 
    SET status = 'Rejected' 
    WHERE id = p_request_id;
    
    -- Insert history
    INSERT INTO approval_history (approval_id, action_by_id, action_taken, comments)
    SELECT id, p_approver_id, 'Rejected', p_comments
    FROM approvals 
    WHERE purchase_request_id = p_request_id AND approver_id = p_approver_id;
END //

-- 4. Generate Purchase Order
CREATE PROCEDURE sp_generate_purchase_order(
    IN p_request_id BIGINT,
    IN p_vendor_id BIGINT,
    IN p_expected_date DATE,
    OUT p_new_po_id BIGINT
)
BEGIN
    DECLARE v_po_number VARCHAR(50);
    DECLARE v_total DECIMAL(15, 2);
    
    -- Calculate total from request items
    SELECT fn_calculate_request_total(p_request_id) INTO v_total;
    
    -- Generate PO number
    SET v_po_number = CONCAT('PO-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', FLOOR(RAND() * 10000));
    
    INSERT INTO purchase_orders (purchase_order_number, purchase_request_id, vendor_id, total_amount, expected_delivery_date, status)
    VALUES (v_po_number, p_request_id, p_vendor_id, v_total, p_expected_date, 'Created');
    
    SET p_new_po_id = LAST_INSERT_ID();
    
    -- Insert PO items from Request items
    INSERT INTO purchase_order_items (purchase_order_id, product_id, quantity, unit_price, total_price)
    SELECT p_new_po_id, product_id, quantity, estimated_price, (quantity * estimated_price)
    FROM purchase_request_items
    WHERE purchase_request_id = p_request_id AND is_deleted = FALSE;
END //

-- 5. Create Invoice
CREATE PROCEDURE sp_create_invoice(
    IN p_receipt_id BIGINT,
    IN p_vendor_id BIGINT,
    IN p_invoice_number VARCHAR(100),
    IN p_invoice_date DATE,
    IN p_due_date DATE,
    IN p_total_amount DECIMAL(15,2),
    OUT p_new_invoice_id BIGINT
)
BEGIN
    INSERT INTO invoices (invoice_number, receipt_id, vendor_id, invoice_date, due_date, total_amount, status)
    VALUES (p_invoice_number, p_receipt_id, p_vendor_id, p_invoice_date, p_due_date, p_total_amount, 'Pending');
    
    SET p_new_invoice_id = LAST_INSERT_ID();
END //

-- 6. Record Payment
CREATE PROCEDURE sp_record_payment(
    IN p_invoice_id BIGINT,
    IN p_payment_ref VARCHAR(100),
    IN p_amount DECIMAL(15,2),
    IN p_payment_date DATE,
    IN p_payment_method VARCHAR(50)
)
BEGIN
    DECLARE v_balance DECIMAL(15,2);
    
    -- Prevent payment greater than invoice is checked via triggers or constraint but we handle logic here
    SELECT fn_calculate_invoice_balance(p_invoice_id) INTO v_balance;
    
    IF p_amount > v_balance THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Payment amount exceeds invoice balance';
    ELSE
        INSERT INTO payments (invoice_id, payment_reference, amount_paid, payment_date, payment_method, status)
        VALUES (p_invoice_id, p_payment_ref, p_amount, p_payment_date, p_payment_method, 'Paid');
        
        -- Update invoice status
        IF p_amount = v_balance THEN
            UPDATE invoices SET status = 'Paid' WHERE id = p_invoice_id;
        ELSE
            UPDATE invoices SET status = 'Partially Paid' WHERE id = p_invoice_id;
        END IF;
    END IF;
END //

-- 7. Create Delivery
CREATE PROCEDURE sp_create_delivery(
    IN p_po_id BIGINT,
    IN p_delivery_note VARCHAR(100),
    IN p_delivery_date DATE,
    IN p_carrier VARCHAR(100),
    IN p_tracking VARCHAR(100),
    OUT p_new_delivery_id BIGINT
)
BEGIN
    INSERT INTO deliveries (purchase_order_id, delivery_note_number, delivery_date, carrier, tracking_number, status)
    VALUES (p_po_id, p_delivery_note, p_delivery_date, p_carrier, p_tracking, 'In Transit');
    
    SET p_new_delivery_id = LAST_INSERT_ID();
END //

-- 8. Generate Receipt
CREATE PROCEDURE sp_generate_receipt(
    IN p_delivery_id BIGINT,
    IN p_receiver_id BIGINT,
    IN p_receipt_date DATE,
    IN p_notes TEXT,
    OUT p_new_receipt_id BIGINT
)
BEGIN
    INSERT INTO receipts (delivery_id, receiver_id, receipt_date, condition_notes)
    VALUES (p_delivery_id, p_receiver_id, p_receipt_date, p_notes);
    
    SET p_new_receipt_id = LAST_INSERT_ID();
    
    -- Update delivery status
    UPDATE deliveries SET status = 'Delivered' WHERE id = p_delivery_id;
END //

-- 9. Add Vendor
CREATE PROCEDURE sp_add_vendor(
    IN p_name VARCHAR(150),
    IN p_contact VARCHAR(100),
    IN p_email VARCHAR(100),
    IN p_phone VARCHAR(20),
    IN p_address TEXT,
    IN p_gst VARCHAR(50),
    OUT p_vendor_id BIGINT
)
BEGIN
    INSERT INTO vendors (vendor_name, contact_name, email, phone, address, gst_number)
    VALUES (p_name, p_contact, p_email, p_phone, p_address, p_gst);
    
    SET p_vendor_id = LAST_INSERT_ID();
END //

-- 10. Update Vendor
CREATE PROCEDURE sp_update_vendor(
    IN p_vendor_id BIGINT,
    IN p_name VARCHAR(150),
    IN p_contact VARCHAR(100),
    IN p_email VARCHAR(100),
    IN p_phone VARCHAR(20),
    IN p_address TEXT
)
BEGIN
    UPDATE vendors
    SET vendor_name = p_name, contact_name = p_contact, email = p_email, phone = p_phone, address = p_address
    WHERE id = p_vendor_id;
END //

-- 11. Deactivate Vendor
CREATE PROCEDURE sp_deactivate_vendor(
    IN p_vendor_id BIGINT
)
BEGIN
    UPDATE vendors
    SET is_active = FALSE
    WHERE id = p_vendor_id;
END //

-- 12. Get Department Spend
CREATE PROCEDURE sp_get_department_spend(
    IN p_department_id BIGINT
)
BEGIN
    SELECT * FROM vw_department_spending WHERE department_id = p_department_id;
END //

-- 13. Get Vendor Spend
CREATE PROCEDURE sp_get_vendor_spend(
    IN p_vendor_id BIGINT
)
BEGIN
    SELECT * FROM vw_vendor_spending WHERE vendor_id = p_vendor_id;
END //

-- 14. Monthly Procurement Report
CREATE PROCEDURE sp_monthly_procurement_report(
    IN p_year INT,
    IN p_month INT
)
BEGIN
    SELECT * FROM vw_monthly_spending WHERE spend_year = p_year AND spend_month = p_month;
END //

-- 15. Top Vendors Report
CREATE PROCEDURE sp_top_vendors_report(
    IN p_limit INT
)
BEGIN
    SELECT * FROM vw_top_vendors LIMIT p_limit;
END //

DELIMITER ;
