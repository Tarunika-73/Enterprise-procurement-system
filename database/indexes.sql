USE enterprise_procurement;

-- Indexes for users
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_employee_id ON users(employee_id);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Indexes for vendors
CREATE INDEX idx_vendors_vendor_name ON vendors(vendor_name);
CREATE INDEX idx_vendors_gst_number ON vendors(gst_number);
CREATE INDEX idx_vendors_created_at ON vendors(created_at);

-- Indexes for purchase_requests
CREATE INDEX idx_pr_request_number ON purchase_requests(request_number);
CREATE INDEX idx_pr_status ON purchase_requests(status);
CREATE INDEX idx_pr_created_at ON purchase_requests(created_at);

-- Indexes for purchase_orders
CREATE INDEX idx_po_po_number ON purchase_orders(purchase_order_number);
CREATE INDEX idx_po_status ON purchase_orders(status);
CREATE INDEX idx_po_created_at ON purchase_orders(created_at);

-- Indexes for invoices
CREATE INDEX idx_inv_invoice_number ON invoices(invoice_number);
CREATE INDEX idx_inv_status ON invoices(status);
CREATE INDEX idx_inv_created_at ON invoices(created_at);

-- Indexes for payments
CREATE INDEX idx_pay_payment_ref ON payments(payment_reference);
CREATE INDEX idx_pay_status ON payments(status);
CREATE INDEX idx_pay_created_at ON payments(created_at);

-- Additional indexes
CREATE INDEX idx_approvals_status ON approvals(status);
CREATE INDEX idx_deliveries_status ON deliveries(status);

CREATE INDEX idx_roles_created_at ON roles(created_at);
CREATE INDEX idx_departments_created_at ON departments(created_at);
CREATE INDEX idx_categories_created_at ON categories(created_at);
CREATE INDEX idx_products_created_at ON products(created_at);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);