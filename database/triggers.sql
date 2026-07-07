USE enterprise_procurement;

DELIMITER //

-- 1. Before Insert Purchase Request
-- Ensure department is valid and requester belongs to department
CREATE TRIGGER trg_before_insert_pr
BEFORE INSERT ON purchase_requests
FOR EACH ROW
BEGIN
    DECLARE v_dept_count INT;
    
    SELECT COUNT(*) INTO v_dept_count 
    FROM users 
    WHERE id = NEW.requester_id AND department_id = NEW.department_id;
    
    IF v_dept_count = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Requester does not belong to the specified department';
    END IF;
    
    -- Set default status if null
    IF NEW.status IS NULL THEN
        SET NEW.status = 'Draft';
    END IF;
END //

-- 2. After Approval
-- When approval happens, update the PR total amount (optional, but a good check) or just log
CREATE TRIGGER trg_after_approval
AFTER UPDATE ON approvals
FOR EACH ROW
BEGIN
    -- If fully approved and it's the final level (simplified to just Approved), we might want to notify or change PR status
    IF NEW.status = 'Approved' AND OLD.status != 'Approved' THEN
        -- The PR status is typically updated in the procedure, but we can do it here too if needed
        -- We will just insert a notification here to demonstrate trigger usage
        INSERT INTO notifications (user_id, type, subject, message)
        SELECT requester_id, 'System', 'Request Approved', CONCAT('Your request ', request_number, ' has been approved.')
        FROM purchase_requests WHERE id = NEW.purchase_request_id;
    END IF;
END //

-- 3. After Purchase Order Creation
-- Send notification to vendor or requester
CREATE TRIGGER trg_after_po_creation
AFTER INSERT ON purchase_orders
FOR EACH ROW
BEGIN
    INSERT INTO notifications (user_id, type, subject, message)
    SELECT requester_id, 'Email', 'PO Created', CONCAT('Purchase Order ', NEW.purchase_order_number, ' has been created.')
    FROM purchase_requests WHERE id = NEW.purchase_request_id;
END //

-- 4. Before Invoice
-- Prevent invoice total from exceeding receipt's PO total
CREATE TRIGGER trg_before_invoice
BEFORE INSERT ON invoices
FOR EACH ROW
BEGIN
    DECLARE v_po_total DECIMAL(15,2);
    
    SELECT po.total_amount INTO v_po_total
    FROM purchase_orders po
    JOIN deliveries d ON d.purchase_order_id = po.id
    JOIN receipts r ON r.delivery_id = d.id
    WHERE r.id = NEW.receipt_id;
    
    -- In practice, invoices might have shipping, but let's assume strict matching or some threshold
    IF NEW.total_amount > v_po_total * 1.1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invoice amount exceeds PO amount by more than 10%';
    END IF;
END //

-- 5. After Payment
-- Close PO if fully paid
CREATE TRIGGER trg_after_payment
AFTER INSERT ON payments
FOR EACH ROW
BEGIN
    DECLARE v_invoice_status VARCHAR(50);
    DECLARE v_receipt_id BIGINT;
    DECLARE v_delivery_id BIGINT;
    DECLARE v_po_id BIGINT;
    
    SELECT status, receipt_id INTO v_invoice_status, v_receipt_id FROM invoices WHERE id = NEW.invoice_id;
    
    IF v_invoice_status = 'Paid' THEN
        SELECT delivery_id INTO v_delivery_id FROM receipts WHERE id = v_receipt_id;
        SELECT purchase_order_id INTO v_po_id FROM deliveries WHERE id = v_delivery_id;
        
        UPDATE purchase_orders SET status = 'Closed' WHERE id = v_po_id;
    END IF;
END //

-- 6. Audit Insert
CREATE TRIGGER trg_audit_insert_vendor
AFTER INSERT ON vendors
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (action, table_name, record_id, new_value)
    VALUES ('INSERT', 'vendors', NEW.id, CONCAT('{"vendor_name":"', NEW.vendor_name, '"}'));
END //

-- 7. Audit Update
CREATE TRIGGER trg_audit_update_vendor
AFTER UPDATE ON vendors
FOR EACH ROW
BEGIN
    IF NEW.vendor_name != OLD.vendor_name OR NEW.is_active != OLD.is_active THEN
        INSERT INTO audit_logs (action, table_name, record_id, old_value, new_value)
        VALUES (
            'UPDATE', 'vendors', NEW.id, 
            CONCAT('{"vendor_name":"', OLD.vendor_name, '", "is_active":', OLD.is_active, '}'),
            CONCAT('{"vendor_name":"', NEW.vendor_name, '", "is_active":', NEW.is_active, '}')
        );
    END IF;
END //

-- 8. Audit Delete
CREATE TRIGGER trg_audit_delete_vendor
AFTER DELETE ON vendors
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (action, table_name, record_id, old_value)
    VALUES ('DELETE', 'vendors', OLD.id, CONCAT('{"vendor_name":"', OLD.vendor_name, '"}'));
END //

-- 9. Update Timestamp
-- Even though we use ON UPDATE CURRENT_TIMESTAMP, fulfilling requirement explicitly for vendors
CREATE TRIGGER trg_update_timestamp_vendors
BEFORE UPDATE ON vendors
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END //

DELIMITER ;
