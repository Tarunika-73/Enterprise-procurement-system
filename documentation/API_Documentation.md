# Enterprise Procurement System

# API Documentation

**Version:** 1.0

**Project:** Enterprise Procurement System

**Internship:** Infosys Springboard Virtual Internship 7.0

---

# Technology Stack

| Layer | Technology |
|--------|------------|
| Frontend | React.js |
| Backend | Spring Boot |
| Database | MySQL 8.0 |
| API Architecture | REST API |
| Data Format | JSON |

---

# Project Overview

The Enterprise Procurement System is a web-based application designed to automate and streamline the procurement process within an organization.

The system enables employees to create purchase requests, managers to review and approve requests, administrators to manage vendors, products, and categories, vendors to provide quotations and deliver products, and finance teams to process invoices and payments.

The application improves procurement efficiency by reducing manual paperwork, increasing transparency, maintaining complete procurement records, and providing analytical reports to support business decision-making.

This document presents the proposed REST API specification for all major modules of the Enterprise Procurement System.

---

# Purpose

This document serves as the official API specification for the Enterprise Procurement System. It provides a detailed description of the proposed REST API endpoints, including request formats, response structures, and expected behavior for each module.

The API documentation acts as a reference for backend developers, frontend developers, testers, and project stakeholders during development and integration.

---

# Note

This API documentation has been prepared based on the approved database schema, ER diagram, and procurement workflow.

Since backend development is currently in progress, the API endpoints, request payloads, response formats, and authentication mechanisms described in this document are proposed specifications.

The final implementation may include additional validations, headers, response attributes, security configurations, and business rules based on project requirements.

---

# Base URL

```
http://localhost:8080/api
```

All REST API endpoints described in this document use the above Base URL.

---
# Table of Contents

1. Authentication APIs
2. Vendor APIs
3. Product APIs
4. Category APIs
5. Purchase Request APIs
6. Approval APIs
7. Purchase Order APIs
8. Vendor Estimate APIs
9. Delivery APIs
10. Receipt APIs
11. Invoice APIs
12. Payment APIs
13. Notification APIs
14. Report APIs
15. Overall API Summary

# Authentication

The Authentication module is responsible for validating users and providing secure access to the Enterprise Procurement System. It ensures that only authorized users can access protected resources based on their assigned roles and permissions.

---

## 1. Login

### Endpoint

| Method | URL |
|--------|-----|
| POST | `/api/auth/login` |

### Description

Authenticates a registered user using their email address and password.

Upon successful authentication, the system generates and returns a JSON Web Token (JWT) along with the authenticated user's information and role.

### Request Body

```json
{
    "email": "employee@company.com",
    "password": "password123"
}
```

### Success Response (200 OK)

```json
{
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "role": "EMPLOYEE",
    "userId": 101,
    "employeeName": "John Doe",
    "message": "Login successful."
}
```

### Error Response (401 Unauthorized)

```json
{
    "message": "Invalid email or password."
}
```

### HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 | Login successful |
| 400 | Invalid request |
| 401 | Invalid credentials |
| 500 | Internal server error |

---

## 2. Logout

### Endpoint

| Method | URL |
|--------|-----|
| POST | `/api/auth/logout` |

### Description

Logs out the currently authenticated user by invalidating the active session or authentication token.

### Request Header

```text
Authorization: Bearer <JWT_TOKEN>
```

### Success Response (200 OK)

```json
{
    "message": "Logout successful."
}
```

### HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 | Logout successful |
| 401 | Unauthorized |
| 500 | Internal server error |

---

## Authentication Workflow

```text
User
  │
  ▼
Enter Email & Password
  │
  ▼
POST /api/auth/login
  │
  ▼
Validate Credentials
  │
  ▼
Generate JWT Token
  │
  ▼
Return JWT Token and User Details
  │
  ▼
Access Protected APIs
```

---

## Authentication Summary

| API | Method | Endpoint |
|-----|--------|----------|
| Login | POST | `/api/auth/login` |
| Logout | POST | `/api/auth/logout` |

---

**Next Module:** Vendor APIs

# Vendor APIs

The Vendor module allows administrators to manage vendor information within the Enterprise Procurement System. It provides APIs to create, retrieve, update, and deactivate vendor records used throughout the procurement lifecycle.

---

## 1. Add Vendor

### Endpoint

| Method | URL |
|--------|-----|
| POST | `/api/vendors` |

### Description

Creates a new vendor in the system.

### Request Body

```json
{
    "vendorName": "Tech Supplies Inc.",
    "contactName": "Tom Hardy",
    "email": "tom@techsupplies.com",
    "username": "techsupplies",
    "password": "password123",
    "phone": "9876543210",
    "address": "123 Tech Boulevard, Chennai",
    "gstNumber": "GST1001",
    "createdBy": 1
}
```

### Success Response (201 Created)

```json
{
    "message": "Vendor created successfully.",
    "vendorId": 101
}
```

### Error Responses

**400 Bad Request**

```json
{
    "message": "Invalid vendor details."
}
```

**409 Conflict**

```json
{
    "message": "Vendor email or username already exists."
}
```

---

## 2. Get All Vendors

### Endpoint

| Method | URL |
|--------|-----|
| GET | `/api/vendors` |

### Description

Returns a list of all active vendors.

### Success Response (200 OK)

```json
[
    {
        "id": 1,
        "vendorName": "Tech Supplies Inc.",
        "contactName": "Tom Hardy",
        "email": "tom@techsupplies.com",
        "phone": "9876543210",
        "gstNumber": "GST1001",
        "status": "Active"
    },
    {
        "id": 2,
        "vendorName": "Office Wonders",
        "contactName": "Wanda Maximoff",
        "email": "wanda@officewonders.com",
        "phone": "9876543211",
        "gstNumber": "GST1002",
        "status": "Active"
    }
]
```

---

## 3. Get Vendor By ID

### Endpoint

| Method | URL |
|--------|-----|
| GET | `/api/vendors/{id}` |

### Description

Returns the details of a specific vendor.

### Path Parameter

| Parameter | Type | Description |
|-----------|------|-------------|
| id | Long | Vendor ID |

### Example Request

```http
GET /api/vendors/1
```

### Success Response (200 OK)

```json
{
    "id": 1,
    "vendorName": "Tech Supplies Inc.",
    "contactName": "Tom Hardy",
    "email": "tom@techsupplies.com",
    "username": "techsupplies",
    "phone": "9876543210",
    "address": "123 Tech Boulevard, Chennai",
    "gstNumber": "GST1001",
    "status": "Active"
}
```

### Error Response (404 Not Found)

```json
{
    "message": "Vendor not found."
}
```

---

## 4. Update Vendor

### Endpoint

| Method | URL |
|--------|-----|
| PUT | `/api/vendors/{id}` |

### Description

Updates the information of an existing vendor.

### Path Parameter

| Parameter | Type | Description |
|-----------|------|-------------|
| id | Long | Vendor ID |

### Request Body

```json
{
    "vendorName": "Tech Supplies India Pvt. Ltd.",
    "contactName": "Tom Hardy",
    "email": "tom@techsupplies.com",
    "username": "techsupplies",
    "phone": "9876543210",
    "address": "456 Anna Salai, Chennai"
}
```

### Success Response (200 OK)

```json
{
    "message": "Vendor updated successfully."
}
```

### Error Response (404 Not Found)

```json
{
    "message": "Vendor not found."
}
```

---

## 5. Delete Vendor

### Endpoint

| Method | URL |
|--------|-----|
| DELETE | `/api/vendors/{id}` |

### Description

Performs a soft delete by marking the vendor as inactive.

### Path Parameter

| Parameter | Type | Description |
|-----------|------|-------------|
| id | Long | Vendor ID |

### Success Response (200 OK)

```json
{
    "message": "Vendor deleted successfully."
}
```

### Error Response (404 Not Found)

```json
{
    "message": "Vendor not found."
}
```

---

## HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 | Request completed successfully |
| 201 | Vendor created successfully |
| 400 | Invalid request data |
| 404 | Vendor not found |
| 409 | Duplicate vendor email or username |
| 500 | Internal server error |

---

## Vendor API Summary

| API | Method | Endpoint |
|------|--------|----------|
| Add Vendor | POST | `/api/vendors` |
| Get All Vendors | GET | `/api/vendors` |
| Get Vendor By ID | GET | `/api/vendors/{id}` |
| Update Vendor | PUT | `/api/vendors/{id}` |
| Delete Vendor | DELETE | `/api/vendors/{id}` |

---

**Next Module:** Product APIs

# Product APIs

The Product module allows administrators to manage products available for procurement. It provides APIs to create, retrieve, update, and remove products from the system.

---

## 1. Add Product

### Endpoint

| Method | URL |
|--------|-----|
| POST | `/api/products` |

### Description

Creates a new product in the system.

### Request Body

```json
{
    "sku": "LAP-001",
    "name": "Dell Latitude 5440",
    "description": "14-inch Business Laptop",
    "categoryId": 1,
    "createdBy": 1
}
```

### Success Response (201 Created)

```json
{
    "message": "Product created successfully.",
    "productId": 101
}
```

### Error Responses

**400 Bad Request**

```json
{
    "message": "Invalid product details."
}
```

**409 Conflict**

```json
{
    "message": "Product SKU already exists."
}
```

---

## 2. Get All Products

### Endpoint

| Method | URL |
|--------|-----|
| GET | `/api/products` |

### Description

Returns a list of all active products available in the system.

### Success Response (200 OK)

```json
[
    {
        "id": 1,
        "sku": "LAP-001",
        "name": "Dell Latitude 5440",
        "description": "14-inch Business Laptop",
        "category": "Electronics",
        "status": "Active"
    },
    {
        "id": 2,
        "sku": "MON-001",
        "name": "24-inch LED Monitor",
        "description": "Full HD Monitor",
        "category": "Electronics",
        "status": "Active"
    }
]
```

---

## 3. Get Product By ID

### Endpoint

| Method | URL |
|--------|-----|
| GET | `/api/products/{id}` |

### Description

Returns the details of a specific product.

### Path Parameter

| Parameter | Type | Description |
|-----------|------|-------------|
| id | Long | Product ID |

### Example Request

```http
GET /api/products/1
```

### Success Response (200 OK)

```json
{
    "id": 1,
    "sku": "LAP-001",
    "name": "Dell Latitude 5440",
    "description": "14-inch Business Laptop",
    "categoryId": 1,
    "categoryName": "Electronics",
    "createdBy": 1,
    "status": "Active"
}
```

### Error Response (404 Not Found)

```json
{
    "message": "Product not found."
}
```

---

## 4. Update Product

### Endpoint

| Method | URL |
|--------|-----|
| PUT | `/api/products/{id}` |

### Description

Updates the information of an existing product.

### Path Parameter

| Parameter | Type | Description |
|-----------|------|-------------|
| id | Long | Product ID |

### Request Body

```json
{
    "sku": "LAP-001",
    "name": "Dell Latitude 5440 Plus",
    "description": "Updated Business Laptop",
    "categoryId": 1
}
```

### Success Response (200 OK)

```json
{
    "message": "Product updated successfully."
}
```

### Error Response (404 Not Found)

```json
{
    "message": "Product not found."
}
```

---

## 5. Delete Product

### Endpoint

| Method | URL |
|--------|-----|
| DELETE | `/api/products/{id}` |

### Description

Performs a soft delete by marking the product as inactive.

### Path Parameter

| Parameter | Type | Description |
|-----------|------|-------------|
| id | Long | Product ID |

### Success Response (200 OK)

```json
{
    "message": "Product deleted successfully."
}
```

### Error Response (404 Not Found)

```json
{
    "message": "Product not found."
}
```

---

## HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 | Request completed successfully |
| 201 | Product created successfully |
| 400 | Invalid request data |
| 404 | Product not found |
| 409 | Duplicate product SKU |
| 500 | Internal server error |

---

## Product API Summary

| API | Method | Endpoint |
|------|--------|----------|
| Add Product | POST | `/api/products` |
| Get All Products | GET | `/api/products` |
| Get Product By ID | GET | `/api/products/{id}` |
| Update Product | PUT | `/api/products/{id}` |
| Delete Product | DELETE | `/api/products/{id}` |

---

**Next Module:** Category APIs

# Category APIs

The Category module allows administrators to organize products into different categories for efficient procurement management.

---

## 1. Add Category

### Endpoint

| Method | URL |
|--------|-----|
| POST | `/api/categories` |

### Description

Creates a new product category.

### Request Body

```json
{
    "name": "Electronics",
    "description": "Electronic products",
    "createdBy": 1
}
```

### Success Response (201 Created)

```json
{
    "message": "Category created successfully.",
    "categoryId": 1
}
```

---

## 2. Get All Categories

### Endpoint

| Method | URL |
|--------|-----|
| GET | `/api/categories` |

### Description

Returns all available product categories.

---

## 3. Get Category By ID

### Endpoint

| Method | URL |
|--------|-----|
| GET | `/api/categories/{id}` |

### Description

Returns the details of a specific category.

---

## 4. Update Category

### Endpoint

| Method | URL |
|--------|-----|
| PUT | `/api/categories/{id}` |

### Description

Updates an existing category.

---

## 5. Delete Category

### Endpoint

| Method | URL |
|--------|-----|
| DELETE | `/api/categories/{id}` |

### Description

Performs a soft delete of a category.

---

## Category API Summary

| API | Method | Endpoint |
|------|--------|----------|
| Add Category | POST | `/api/categories` |
| Get All Categories | GET | `/api/categories` |
| Get Category By ID | GET | `/api/categories/{id}` |
| Update Category | PUT | `/api/categories/{id}` |
| Delete Category | DELETE | `/api/categories/{id}` |

---

# Purchase Order APIs

The Purchase Order module manages the creation and tracking of purchase orders after purchase requests are approved.

---

## 1. Create Purchase Order

| Method | URL |
|--------|-----|
| POST | `/api/purchase-orders` |

Creates a new purchase order.

---

## 2. Get All Purchase Orders

| Method | URL |
|--------|-----|
| GET | `/api/purchase-orders` |

Returns all purchase orders.

---

## 3. Get Purchase Order By ID

| Method | URL |
|--------|-----|
| GET | `/api/purchase-orders/{id}` |

Returns details of a purchase order.

---

## 4. Update Purchase Order

| Method | URL |
|--------|-----|
| PUT | `/api/purchase-orders/{id}` |

Updates purchase order details.

---

## 5. Cancel Purchase Order

| Method | URL |
|--------|-----|
| DELETE | `/api/purchase-orders/{id}` |

Cancels a purchase order.

---

## Purchase Order API Summary

| API | Method | Endpoint |
|------|--------|----------|
| Create Purchase Order | POST | `/api/purchase-orders` |
| Get All Purchase Orders | GET | `/api/purchase-orders` |
| Get Purchase Order By ID | GET | `/api/purchase-orders/{id}` |
| Update Purchase Order | PUT | `/api/purchase-orders/{id}` |
| Cancel Purchase Order | DELETE | `/api/purchase-orders/{id}` |

---

# Vendor Estimate APIs

The Vendor Estimate module manages quotations submitted by vendors.

---

## 1. Create Vendor Estimate

| Method | URL |
|--------|-----|
| POST | `/api/vendor-estimates` |

Creates a vendor quotation.

---

## 2. Get All Vendor Estimates

| Method | URL |
|--------|-----|
| GET | `/api/vendor-estimates` |

Returns all vendor quotations.

---

## 3. Get Vendor Estimate By ID

| Method | URL |
|--------|-----|
| GET | `/api/vendor-estimates/{id}` |

Returns quotation details.

---

## 4. Update Vendor Estimate

| Method | URL |
|--------|-----|
| PUT | `/api/vendor-estimates/{id}` |

Updates a vendor quotation.

---

## Vendor Estimate API Summary

| API | Method | Endpoint |
|------|--------|----------|
| Create Vendor Estimate | POST | `/api/vendor-estimates` |
| Get All Vendor Estimates | GET | `/api/vendor-estimates` |
| Get Vendor Estimate By ID | GET | `/api/vendor-estimates/{id}` |
| Update Vendor Estimate | PUT | `/api/vendor-estimates/{id}` |

---

# Delivery APIs

The Delivery module tracks the shipment and delivery status of purchase orders.

---

## 1. Create Delivery

| Method | URL |
|--------|-----|
| POST | `/api/deliveries` |

Creates a delivery record.

---

## 2. Get All Deliveries

| Method | URL |
|--------|-----|
| GET | `/api/deliveries` |

Returns all deliveries.

---

## 3. Get Delivery By ID

| Method | URL |
|--------|-----|
| GET | `/api/deliveries/{id}` |

Returns delivery information.

---

## 4. Update Delivery Status

| Method | URL |
|--------|-----|
| PUT | `/api/deliveries/{id}` |

Updates delivery status.

---

## Delivery API Summary

| API | Method | Endpoint |
|------|--------|----------|
| Create Delivery | POST | `/api/deliveries` |
| Get All Deliveries | GET | `/api/deliveries` |
| Get Delivery By ID | GET | `/api/deliveries/{id}` |
| Update Delivery Status | PUT | `/api/deliveries/{id}` |

---

# Receipt APIs

The Receipt module records goods received from vendors.

---

## 1. Generate Receipt

| Method | URL |
|--------|-----|
| POST | `/api/receipts` |

Generates a goods receipt.

---

## 2. Get All Receipts

| Method | URL |
|--------|-----|
| GET | `/api/receipts` |

Returns all receipts.

---

## 3. Get Receipt By ID

| Method | URL |
|--------|-----|
| GET | `/api/receipts/{id}` |

Returns receipt details.

---

## Receipt API Summary

| API | Method | Endpoint |
|------|--------|----------|
| Generate Receipt | POST | `/api/receipts` |
| Get All Receipts | GET | `/api/receipts` |
| Get Receipt By ID | GET | `/api/receipts/{id}` |

---

# Invoice APIs

The Invoice module manages vendor invoices for delivered goods.

---

## 1. Create Invoice

| Method | URL |
|--------|-----|
| POST | `/api/invoices` |

Creates a vendor invoice.

---

## 2. Get All Invoices

| Method | URL |
|--------|-----|
| GET | `/api/invoices` |

Returns all invoices.

---

## 3. Get Invoice By ID

| Method | URL |
|--------|-----|
| GET | `/api/invoices/{id}` |

Returns invoice details.

---

## 4. Update Invoice

| Method | URL |
|--------|-----|
| PUT | `/api/invoices/{id}` |

Updates invoice information.

---

## Invoice API Summary

| API | Method | Endpoint |
|------|--------|----------|
| Create Invoice | POST | `/api/invoices` |
| Get All Invoices | GET | `/api/invoices` |
| Get Invoice By ID | GET | `/api/invoices/{id}` |
| Update Invoice | PUT | `/api/invoices/{id}` |

---

# Payment APIs

The Payment module records payments made against vendor invoices.

---

## 1. Record Payment

| Method | URL |
|--------|-----|
| POST | `/api/payments` |

Records a payment.

---

## 2. Get All Payments

| Method | URL |
|--------|-----|
| GET | `/api/payments` |

Returns all payments.

---

## 3. Get Payment By ID

| Method | URL |
|--------|-----|
| GET | `/api/payments/{id}` |

Returns payment details.

---

## Payment API Summary

| API | Method | Endpoint |
|------|--------|----------|
| Record Payment | POST | `/api/payments` |
| Get All Payments | GET | `/api/payments` |
| Get Payment By ID | GET | `/api/payments/{id}` |

---

# Notification APIs

The Notification module manages system notifications sent to users.

---

## 1. Get Notifications

| Method | URL |
|--------|-----|
| GET | `/api/notifications` |

Returns all notifications.

---

## 2. Mark Notification as Read

| Method | URL |
|--------|-----|
| PUT | `/api/notifications/{id}/read` |

Marks a notification as read.

---

## Notification API Summary

| API | Method | Endpoint |
|------|--------|----------|
| Get Notifications | GET | `/api/notifications` |
| Mark Notification as Read | PUT | `/api/notifications/{id}/read` |

---

# Report APIs

The Report module provides procurement analytics and management reports.

---

## 1. Department Spending Report

| Method | URL |
|--------|-----|
| GET | `/api/reports/department-spending` |

Returns department-wise procurement spending.

---

## 2. Vendor Spending Report

| Method | URL |
|--------|-----|
| GET | `/api/reports/vendor-spending` |

Returns vendor expenditure details.

---

## 3. Monthly Procurement Report

| Method | URL |
|--------|-----|
| GET | `/api/reports/monthly` |

Returns monthly procurement statistics.

---

## 4. Top Vendors Report

| Method | URL |
|--------|-----|
| GET | `/api/reports/top-vendors` |

Returns vendors ranked by purchase volume.

---

## Report API Summary

| API | Method | Endpoint |
|------|--------|----------|
| Department Spending Report | GET | `/api/reports/department-spending` |
| Vendor Spending Report | GET | `/api/reports/vendor-spending` |
| Monthly Procurement Report | GET | `/api/reports/monthly` |
| Top Vendors Report | GET | `/api/reports/top-vendors` |

---

## Overall API Summary

| Module | Endpoints |
|---------|-----------:|
| Authentication | 2 |
| Category | 5 |
| Vendor | 5 |
| Product | 5 |
| Purchase Request | 5 |
| Approval | 3 |
| Purchase Order | 5 |
| Vendor Estimate | 4 |
| Delivery | 4 |
| Receipt | 3 |
| Invoice | 4 |
| Payment | 3 |
| Notification | 2 |
| Reports | 4 |

**Total REST API Endpoints:** **54**