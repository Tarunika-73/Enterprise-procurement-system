USE enterprise_procurement;

DELIMITER //

-- 1. Calculate Purchase Request Total
CREATE FUNCTION fn_calculate_request_total(p_request_id BIGINT)
RETURNS DECIMAL(15,2)
DETERMINISTIC
BEGIN
    DECLARE v_total DECIMAL(15,2);

    SELECT COALESCE(SUM(quantity * estimated_price),0.00)
    INTO v_total
    FROM purchase_request_items
    WHERE purchase_request_id = p_request_id
      AND is_deleted = FALSE;

    RETURN v_total;
END //

-- 2. Calculate Purchase Order Total
CREATE FUNCTION fn_calculate_po_total(p_po_id BIGINT)
RETURNS DECIMAL(15,2)
DETERMINISTIC
BEGIN
    DECLARE v_total DECIMAL(15,2);

    SELECT COALESCE(SUM(total_price),0.00)
    INTO v_total
    FROM purchase_order_items
    WHERE purchase_order_id = p_po_id
      AND is_deleted = FALSE;

    RETURN v_total;
END //

-- 3. Calculate Invoice Total
CREATE FUNCTION fn_calculate_invoice_total(p_invoice_id BIGINT)
RETURNS DECIMAL(15,2)
DETERMINISTIC
BEGIN
    DECLARE v_total DECIMAL(15,2);

    SELECT COALESCE(total_amount,0.00)
    INTO v_total
    FROM invoices
    WHERE id = p_invoice_id;

    RETURN v_total;
END //

-- 4. Calculate Outstanding Invoice Balance
CREATE FUNCTION fn_calculate_invoice_balance(p_invoice_id BIGINT)
RETURNS DECIMAL(15,2)
DETERMINISTIC
BEGIN
    DECLARE v_total DECIMAL(15,2);
    DECLARE v_paid DECIMAL(15,2);

    SELECT COALESCE(total_amount,0.00)
    INTO v_total
    FROM invoices
    WHERE id = p_invoice_id;

    SELECT COALESCE(SUM(amount_paid),0.00)
    INTO v_paid
    FROM payments
    WHERE invoice_id = p_invoice_id
      AND status <> 'Cancelled'
      AND is_deleted = FALSE;

    RETURN (v_total - v_paid);
END //

-- 5. Calculate Department Total Spend
CREATE FUNCTION fn_department_total_spend(p_department_id BIGINT)
RETURNS DECIMAL(15,2)
DETERMINISTIC
BEGIN
    DECLARE v_total DECIMAL(15,2);

    SELECT COALESCE(SUM(total_amount),0.00)
    INTO v_total
    FROM purchase_requests
    WHERE department_id = p_department_id
      AND status IN ('Approved','Closed')
      AND is_deleted = FALSE;

    RETURN v_total;
END //

DELIMITER ;