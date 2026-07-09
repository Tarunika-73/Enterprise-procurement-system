# Enterprise Procurement System

# Workflow Documentation

**Version:** 1.0

**Project:** Enterprise Procurement System

**Internship:** Infosys Springboard Virtual Internship 7.0

---

# Table of Contents

1. Project Overview
2. System Actors
3. Overall Workflow
4. Authentication Workflow
5. Purchase Request Workflow
6. Approval Workflow
7. Purchase Order Workflow
8. Vendor Workflow
9. Delivery Workflow
10. Receipt Workflow
11. Invoice Workflow
12. Payment Workflow
13. Report Workflow
14. Conclusion

# Project Overview

The Enterprise Procurement System is a web-based application developed to automate and streamline the procurement process within an organization. The system replaces traditional manual procurement activities with a centralized digital workflow, enabling efficient management of purchase requests, approvals, purchase orders, vendor management, deliveries, invoices, payments, and reporting.

The application allows employees to submit purchase requests, managers to review and approve requests, administrators to manage vendors and products, vendors to fulfill purchase orders, and finance teams to process invoices and payments. Every stage of the procurement lifecycle is recorded within the system to improve transparency, accountability, and operational efficiency.

The workflow is designed to ensure that procurement activities follow a structured approval process while maintaining accurate records for auditing and reporting purposes.

---

# System Actors

The Enterprise Procurement System consists of multiple users, each responsible for specific activities throughout the procurement lifecycle.

| Actor | Responsibilities |
|--------|------------------|
| Employee | Creates purchase requests, tracks request status, and receives notifications. |
| Manager | Reviews purchase requests and approves or rejects them based on organizational policies. |
| Administrator | Manages users, departments, vendors, products, categories, and generates purchase orders. |
| Vendor | Receives purchase orders, provides quotations or estimates, delivers products, and submits invoices. |
| Finance Officer | Verifies invoices, records payments, and manages financial transactions. |
| System | Maintains audit logs, sends notifications, validates business rules, and generates reports automatically. |

---

## Workflow Legend

- Employee – Creates purchase requests.
- Manager – Reviews and approves requests.
- Administrator – Generates purchase orders and manages vendors.
- Vendor – Supplies requested products.
- Finance Officer – Processes invoices and payments.
- System – Sends notifications, validates transactions, and generates reports.

# Overall Workflow

The Enterprise Procurement System follows a structured procurement lifecycle from purchase request creation to payment completion.

```
Employee Login
       │
       ▼
Employee Dashboard
       │
       ▼
Create Purchase Request
       │
       ▼
Manager Approval
       │
       ▼
Purchase Order Generation
       │
       ▼
Vendor Selection
       │
       ▼
Vendor Delivery
       │
       ▼
Goods Receipt
       │
       ▼
Invoice Generation
       │
       ▼
Payment Processing
       │
       ▼
Procurement Reports
```

### Workflow Description

1. The employee logs into the system and accesses the dashboard.
2. A purchase request is created by selecting the required products and providing the necessary justification.
3. The request is submitted to the manager for approval.
4. Once approved, the administrator generates a purchase order and assigns a suitable vendor.
5. The vendor receives the purchase order and delivers the requested products.
6. Upon delivery, the goods are inspected and a receipt is generated.
7. The vendor submits an invoice corresponding to the delivered goods.
8. The finance department verifies the invoice and records the payment.
9. After successful payment, the procurement cycle is completed, and reports are generated for analysis and auditing.

---

# Authentication Workflow

The authentication module is responsible for validating user credentials and providing secure access to the Enterprise Procurement System based on user roles.

```
User
   │
   ▼
Enter Email & Password
   │
   ▼
Login Request
   │
   ▼
Credential Validation
   │
   ▼
Authentication Successful
   │
   ▼
Generate Session / JWT Token
   │
   ▼
Load User Dashboard
```

### Workflow Description

1. The user enters a registered email address and password on the login page.
2. The system validates the submitted credentials against the user records stored in the database.
3. If the credentials are valid, the system authenticates the user and generates a secure session or JWT token.
4. Based on the user's assigned role, the appropriate dashboard and system permissions are loaded.
5. If the credentials are invalid, an authentication error message is displayed and access is denied.
6. Authenticated users can securely access only the modules and features permitted by their assigned roles.

---

# Purchase Request Workflow

The Purchase Request module enables employees to raise procurement requests for products or services required by their department.

```
Employee
    │
    ▼
Login to System
    │
    ▼
Open Purchase Request Module
    │
    ▼
Select Products
    │
    ▼
Enter Quantity & Justification
    │
    ▼
Submit Purchase Request
    │
    ▼
Request Status: Pending Approval
```

### Workflow Description

1. The employee logs into the Enterprise Procurement System.
2. The employee navigates to the Purchase Request module.
3. Required products are selected from the available product catalog.
4. The employee specifies the required quantity and provides a business justification.
5. The purchase request is submitted to the system.
6. The system stores the request in the database with the status **Pending Approval**.
7. The manager receives a notification for review and approval.

---

# Approval Workflow

The Approval module enables managers to review purchase requests and either approve or reject them based on business requirements and budget availability.

```
Manager
    │
    ▼
View Pending Requests
    │
    ▼
Review Request Details
    │
    ▼
Approve or Reject
    │
    ▼
Update Request Status
    │
    ▼
Notify Employee
```

### Workflow Description

1. The manager logs into the system.
2. The manager views all pending purchase requests assigned for approval.
3. Each request is reviewed along with product details, quantity, department, and business justification.
4. If the request satisfies organizational policies, it is approved.
5. If the request is not acceptable, it is rejected with appropriate comments.
6. The purchase request status is updated accordingly.
7. The employee receives a system notification regarding the approval decision.

---

# Purchase Order Workflow

The Purchase Order module converts approved purchase requests into official purchase orders that are sent to vendors.

```
Approved Request
      │
      ▼
Generate Purchase Order
      │
      ▼
Assign Vendor
      │
      ▼
Create Purchase Order Number
      │
      ▼
Purchase Order Generated
      │
      ▼
Vendor Notification
```

### Workflow Description

1. Only approved purchase requests are eligible for purchase order generation.
2. The administrator selects the appropriate vendor.
3. The system generates a unique purchase order number.
4. Purchase request items are copied into the purchase order.
5. The purchase order is stored in the database.
6. The vendor is notified that a new purchase order has been issued.

---

# Vendor Workflow

The Vendor module manages vendor participation in the procurement lifecycle after receiving a purchase order.

```
Vendor
    │
    ▼
Receive Purchase Order
    │
    ▼
Review Order Details
    │
    ▼
Submit Estimate (Optional)
    │
    ▼
Accept Order
    │
    ▼
Prepare Shipment
    │
    ▼
Dispatch Products
```

### Workflow Description

1. The vendor receives the purchase order from the organization.
2. The vendor reviews the requested products and quantities.
3. If required, the vendor submits a quotation or estimated cost.
4. After confirmation, the vendor accepts the purchase order.
5. The requested products are prepared for shipment.
6. Shipment details are recorded in the system.
7. The delivery process begins.

---

# Delivery Workflow

The Delivery module tracks the movement of products from the vendor to the organization until successful delivery.

```
Vendor Shipment
       │
       ▼
Delivery Created
       │
       ▼
Products In Transit
       │
       ▼
Goods Delivered
       │
       ▼
Goods Inspection
       │
       ▼
Generate Goods Receipt
```

### Workflow Description

1. The vendor dispatches the ordered products.
2. Delivery information such as carrier details and tracking number is recorded.
3. The shipment status is updated as products move toward the organization.
4. Upon arrival, the goods are inspected for quantity and quality.
5. If the products satisfy the purchase order, a goods receipt is generated.
6. The delivery status is updated to **Delivered**, allowing the invoicing process to begin.

---

# Receipt Workflow

The Receipt module confirms that the products delivered by the vendor have been received and verified by the organization.

```
Products Delivered
        │
        ▼
Inspect Goods
        │
        ▼
Verify Quantity & Quality
        │
        ▼
Generate Goods Receipt
        │
        ▼
Update Delivery Status
```

### Workflow Description

1. The organization receives the products delivered by the vendor.
2. The receiving officer verifies the quantity and quality of the delivered products.
3. If the delivered products match the purchase order, a goods receipt is generated.
4. The receipt is stored in the database for future reference.
5. The delivery status is updated to **Delivered**.
6. The receipt becomes the basis for invoice generation.

---

# Invoice Workflow

The Invoice module records invoices submitted by vendors after successful delivery of products.

```
Goods Receipt
       │
       ▼
Vendor Creates Invoice
       │
       ▼
Finance Verification
       │
       ▼
Invoice Approved
       │
       ▼
Ready for Payment
```

### Workflow Description

1. After the goods receipt is generated, the vendor submits an invoice.
2. The finance department verifies the invoice against the purchase order and receipt.
3. The system validates invoice details such as invoice number, amount, and due date.
4. If all validations are successful, the invoice is approved.
5. The invoice status is updated to **Pending Payment**.

---

# Payment Workflow

The Payment module records payments made to vendors and completes the procurement cycle.

```
Approved Invoice
        │
        ▼
Record Payment
        │
        ▼
Update Invoice Status
        │
        ▼
Close Purchase Order
        │
        ▼
Generate Audit Record
```

### Workflow Description

1. Finance records the payment made to the vendor.
2. The payment amount is validated against the outstanding invoice balance.
3. If the payment completes the invoice amount, the invoice status changes to **Paid**.
4. The associated purchase order is marked as **Closed**.
5. The payment transaction is stored for auditing and reporting purposes.

---

# Report Workflow

The Reporting module provides procurement analytics and business insights for management.

```
Procurement Database
         │
         ▼
Views & SQL Functions
         │
         ▼
Generate Reports
         │
         ▼
Management Dashboard
```

### Workflow Description

1. Procurement data is collected from purchase requests, purchase orders, invoices, payments, vendors, and departments.
2. Database views and SQL functions process the stored data.
3. The system generates procurement reports such as:
   - Department Spending Report
   - Vendor Spending Report
   - Monthly Procurement Report
   - Top Vendor Report
4. These reports help management monitor procurement performance, spending patterns, and vendor performance for better decision-making.

---

# Conclusion

The Enterprise Procurement System provides a structured and automated procurement workflow that improves efficiency, transparency, and accountability throughout the procurement lifecycle.

The workflow integrates employees, managers, administrators, vendors, and finance personnel into a single platform, ensuring that every procurement activity is properly tracked and validated. By automating purchase requests, approvals, purchase order generation, deliveries, receipts, invoices, payments, and reporting, the system minimizes manual effort, reduces processing time, and maintains complete auditability.

This workflow serves as a reference for developers, testers, project reviewers, and stakeholders by clearly describing how each module interacts to support the overall procurement process.