# Enterprise Procurement System Database

This directory contains the database schema, sample data, functions, views, procedures, triggers, and sample queries for the Enterprise Procurement System. The database is designed for MySQL 8.0 and follows the Third Normal Form (3NF).

## Requirements

- **Database:** MySQL 8.0+
- **Character Set:** `utf8mb4`
- **Collation:** `utf8mb4_unicode_ci`
- **Engine:** `InnoDB`

## Folder Structure

The generated SQL files must be executed in a specific order to avoid foreign key dependency errors:

1. `schema.sql`: Contains the database creation, table definitions, and relationships.
2. `indexes.sql`: Contains performance indexes for optimizing common queries.
3. `views.sql`: Contains analytical views for reporting.
4. `functions.sql`: Contains custom functions for calculations.
5. `procedures.sql`: Contains stored procedures handling business workflows.
6. `triggers.sql`: Contains triggers for auditing and automated background updates.
7. `sample_data.sql`: Contains comprehensive sample data to populate the tables.
8. `queries.sql`: Contains example queries demonstrating usage.

## Database Setup & Execution Order

To set up the database, you can use the MySQL command-line tool or MySQL Workbench. 

### Using MySQL Workbench

1. Open MySQL Workbench and connect to your MySQL 8.0 server.
2. Go to **File -> Open SQL Script**.
3. Open and execute the scripts in the following exact order:
   - `schema.sql` (This creates the `enterprise_procurement` database)
   - `indexes.sql`
   - `views.sql`
   - `functions.sql`
   - `procedures.sql`
   - `triggers.sql`
   - `sample_data.sql`
4. You can then run scripts from `queries.sql` to test the environment.

### Using Command Line

Run the following command, replacing `<user>` with your MySQL username. You will be prompted for your password.

```bash
mysql -u <user> -p < schema.sql
mysql -u <user> -p enterprise_procurement < indexes.sql
mysql -u <user> -p enterprise_procurement < views.sql
mysql -u <user> -p enterprise_procurement < functions.sql
mysql -u <user> -p enterprise_procurement < procedures.sql
mysql -u <user> -p enterprise_procurement < triggers.sql
mysql -u <user> -p enterprise_procurement < sample_data.sql
```

## ER Diagram & Relationship Explanation

The database consists of 5 core modules:
1. **Master Data Management:** `roles`, `departments`, `users`, `vendors`, `categories`, `products`, `vendor_products`, `supplier_performance`, `supplier_compliance`.
2. **Workflow Engine:** `purchase_requests`, `purchase_request_items`, `approvals`, `approval_history`.
3. **Purchase Orders:** `purchase_orders`, `purchase_order_items`, `vendor_estimates`, `deliveries`, `receipts`, `invoices`, `payments`.
4. **Analytics:** Analytical views prefix with `vw_`.
5. **Security:** `audit_logs`, `notifications`, `login_history`, `user_sessions`.

### Key Relationships

- **Users & Departments/Roles:** A User belongs to 1 Department and 1 Role. Departments have 1 Manager (User).
- **Products & Vendors:** A Category has many Products. A Vendor has many Products (managed via the `vendor_products` junction table).
- **Requests & Workflows:** A User creates many Purchase Requests. A Purchase Request has many Items and many Approvals (to support multi-level hierarchies).
- **Purchase Orders to Payments:**
  - 1 Purchase Request -> 1 Purchase Order
  - 1 Purchase Order -> Many Deliveries
  - 1 Delivery -> Many Receipts
  - 1 Receipt -> 1 Invoice
  - 1 Invoice -> Many Payments (supporting partial payments)
- **Vendors to Performance:** A Vendor has many Performance and Compliance records.
- **Auditing:** Notifications, Audit Logs, and Sessions are tied to Users.

## Hibernate / JPA Integration

All tables are optimized for seamless mapping with Spring Boot and Hibernate/JPA:
- Primary keys are named `id` (`@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)`).
- Foreign keys use standard `table_id` formatting.
- `created_at` and `updated_at` can be mapped using `@CreationTimestamp` and `@UpdateTimestamp`.
- Soft deletes (`is_deleted`) can be managed using `@SQLDelete(sql = "UPDATE table SET is_deleted = true WHERE id=?")` and `@Where(clause = "is_deleted=false")`.
- `ENUM` types can be mapped using `@Enumerated(EnumType.STRING)`.

## Example Queries

Check `queries.sql` for pre-written commands such as:
- Viewing department spending.
- Retrieving top vendors.
- Calculating outstanding invoice balances.
- Fetching the monthly procurement report.
