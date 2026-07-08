# Enterprise Procurement System Database

This repository contains the complete database implementation for the **Enterprise Procurement System**. The database is developed using **MySQL 8.0** and follows the **Third Normal Form (3NF)** to ensure data consistency, integrity, and minimal redundancy.

The database supports the complete procurement lifecycle, including purchase requests, approvals, purchase orders, vendor management, deliveries, invoicing, payments, notifications, and auditing.

---

# Requirements

- Database: MySQL 8.0+
- Storage Engine: InnoDB
- Character Set: utf8mb4
- Collation: utf8mb4_unicode_ci

---

# Project Structure

The SQL scripts should be executed in the following order to satisfy all foreign key dependencies.

| Order | File | Description |
|-------|------|-------------|
| 1 | schema.sql | Creates the database, tables, constraints, and relationships |
| 2 | indexes.sql | Creates indexes for query optimization |
| 3 | views.sql | Creates reporting and analytical views |
| 4 | functions.sql | Creates reusable SQL functions |
| 5 | procedures.sql | Creates stored procedures for business operations |
| 6 | triggers.sql | Creates triggers for automation and auditing |
| 7 | sample_data.sql | Inserts sample records into all tables |
| 8 | queries.sql | Contains sample SQL queries for testing |

---

# Database Setup

## Using MySQL Workbench

1. Open MySQL Workbench.
2. Connect to your MySQL Server.
3. Open each SQL script using **File → Open SQL Script**.
4. Execute the files in the following order:

```
schema.sql
indexes.sql
views.sql
functions.sql
procedures.sql
triggers.sql
sample_data.sql
queries.sql (optional)
```

---

## Using MySQL Command Line

Replace `<username>` with your MySQL username.

```bash
mysql -u <username> -p < schema.sql
mysql -u <username> -p enterprise_procurement < indexes.sql
mysql -u <username> -p enterprise_procurement < views.sql
mysql -u <username> -p enterprise_procurement < functions.sql
mysql -u <username> -p enterprise_procurement < procedures.sql
mysql -u <username> -p enterprise_procurement < triggers.sql
mysql -u <username> -p enterprise_procurement < sample_data.sql
mysql -u <username> -p enterprise_procurement < queries.sql
```

---

# Database Modules

The database is organized into the following functional modules.

## 1. Master Data Management

- roles
- departments
- users
- vendors
- categories
- products
- vendor_products

This module manages the organization's master information, including employees, vendors, products, and product categories.

---

## 2. Procurement Workflow

- purchase_requests
- purchase_request_items
- approvals

This module handles the creation and approval of purchase requests submitted by employees.

---

## 3. Purchase Order Management

- purchase_orders
- purchase_order_items
- vendor_estimates
- deliveries
- receipts
- invoices
- payments

This module manages the procurement lifecycle from approved requests to final payment.

---

## 4. Reporting & Analytics

Database views prefixed with:

```
vw_
```

These views provide summarized reports for:

- Pending Purchase Requests
- Purchase Order Summary
- Vendor Spending
- Department Spending
- Monthly Spending
- Product Purchase Summary
- Pending Payments

---

## 5. Security & Auditing

- notifications
- audit_logs

This module records important system notifications and maintains audit logs for tracking database activities.

---

# Database Relationships

The Enterprise Procurement System follows a relational design.

## Users

- One Role → Many Users
- One Department → Many Users
- One Department → One Manager (User)

---

## Products

- One Category → Many Products
- One Vendor → Many Products
- One Product → Many Vendors

The Vendor–Product relationship is maintained using the **vendor_products** junction table.

---

## Purchase Requests

- One User → Many Purchase Requests
- One Purchase Request → Many Purchase Request Items
- One Purchase Request → Many Approval Records

---

## Purchase Orders

- One Purchase Request → One Purchase Order
- One Purchase Order → Many Purchase Order Items
- One Purchase Order → Many Vendor Estimates
- One Purchase Order → Many Deliveries

---

## Deliveries

- One Delivery → Many Receipts

---

## Invoicing

- One Receipt → One Invoice
- One Invoice → Many Payments

This design supports **partial payments** for invoices.

---

# Database Features

The database includes:

- Primary and Foreign Key Constraints
- Check Constraints
- Soft Delete Support
- Stored Procedures
- User-defined Functions
- Database Triggers
- Analytical Views
- Performance Indexes
- Sample Test Data

---

# Hibernate / Spring Boot Compatibility

The schema is designed to integrate easily with Spring Boot and Hibernate.

Features include:

- `id` as AUTO_INCREMENT primary keys
- Standard foreign key naming convention
- `created_at` and `updated_at` timestamp columns
- Soft delete using the `is_deleted` flag
- ENUM fields compatible with `@Enumerated(EnumType.STRING)`

Example annotations:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

```java
@CreationTimestamp
private Timestamp createdAt;
```

```java
@UpdateTimestamp
private Timestamp updatedAt;
```

---

# Sample Queries

The `queries.sql` file contains sample queries demonstrating how to use the database.

Examples include:

- View pending purchase requests
- Calculate department spending
- View vendor spending
- View purchase order summary
- Calculate outstanding invoice balance
- View pending payments
- Generate monthly procurement reports
- Display recent audit logs

---

# Authors

Enterprise Procurement System

Developed as part of the Infosys Springboard Virtual Internship Project.

```
