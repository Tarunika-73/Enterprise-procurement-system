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
| API Style | REST API |
| Data Format | JSON |

---

# Project Overview

The Enterprise Procurement System is designed to automate and streamline the procurement process within an organization.

The application enables employees to submit purchase requests, managers to review and approve requests, administrators to manage vendors and products, finance teams to process invoices and payments, and vendors to supply requested products.

The system improves transparency, reduces manual paperwork, tracks procurement activities, and provides analytical reports for better decision-making.

This document defines the proposed REST API endpoints for all major modules of the Enterprise Procurement System.

---

# Note

This API documentation is a preliminary API specification prepared based on the approved database schema, ER diagram, and procurement workflow.

Since backend implementation is currently under development, the API endpoints, request payloads, response structures, and authentication mechanisms described in this document are proposed designs.

The final implementation may include additional validations, headers, response fields, and security configurations.

---

# Base URL

```
http://localhost:8080/api
```

All API endpoints described in this document use the above Base URL.

---

# Authentication

Authentication APIs are responsible for validating users and controlling secure access to the Enterprise Procurement System.

---

## 1. Login

### Endpoint

| Method | URL |
|--------|-----|
| POST | `/api/auth/login` |

### Description

Authenticates a registered user using email and password.

Upon successful authentication, the server returns an authentication token and the user's role.

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
    "message": "Login Successful"
}
```

### Error Response (401 Unauthorized)

```json
{
    "message": "Invalid Email or Password"
}
```

### Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 | Login Successful |
| 400 | Invalid Request |
| 401 | Invalid Credentials |
| 500 | Internal Server Error |

---

## 2. Logout

### Endpoint

| Method | URL |
|--------|-----|
| POST | `/api/auth/logout` |

### Description

Logs out the currently authenticated user and invalidates the active session or authentication token.

### Request Header

```
Authorization: Bearer <JWT_TOKEN>
```

### Success Response (200 OK)

```json
{
    "message": "Logout Successful"
}
```

### Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 | Logout Successful |
| 401 | Unauthorized |
| 500 | Internal Server Error |

---

## Authentication Workflow

```
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
Return Token + User Role
   │
   ▼
Access Protected APIs
```

---

## Authentication Summary

| API | Method | Endpoint |
|------|--------|----------|
| Login | POST | `/api/auth/login` |
| Logout | POST | `/api/auth/logout` |

---

**Next Module:** User Management APIs

# Vendor APIs

## Get All Vendors

Endpoint

GET /api/vendors

Description

Returns all vendors.

## Add Vendor

Endpoint

POST /api/vendors

Description

Adds a new vendor.

## Update Vendor

Endpoint

PUT /api/vendors/{id}

Description

Updates vendor details.

## Delete Vendor

Endpoint

DELETE /api/vendors/{id}

Description

Deletes a vendor.

# Vendor APIs

The Vendor module allows administrators to manage vendor information. It provides APIs to create, retrieve, update, and deactivate vendor records used throughout the procurement process.

---

## 1. Add Vendor

### Endpoint

**POST** `/api/vendors`

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

### Possible Error Responses

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

**GET** `/api/vendors`

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

**GET** `/api/vendors/{id}`

### Description

Returns the details of a specific vendor.

### Path Parameter

| Parameter | Type | Description |
|-----------|------|-------------|
| id | Long | Vendor ID |

### Example

```
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

**PUT** `/api/vendors/{id}`

### Description

Updates the information of an existing vendor.

### Path Parameter

| Parameter | Type | Description |
|-----------|------|-------------|
| id | Long | Vendor ID |

### Request Body

```json
{
    "vendorName": "Tech Supplies India Pvt Ltd",
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

### Error Response

```json
{
    "message": "Vendor not found."
}
```

---

## 5. Delete Vendor

### Endpoint

**DELETE** `/api/vendors/{id}`

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

### Error Response

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

# Product APIs

The Product module allows administrators to manage products available for procurement. It provides APIs to create, retrieve, update, and remove products from the system.

---

## 1. Add Product

### Endpoint

**POST** `/api/products`

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

### Possible Error Responses

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

**GET** `/api/products`

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

**GET** `/api/products/{id}`

### Description

Returns the details of a specific product.

### Path Parameter

| Parameter | Type | Description |
|-----------|------|-------------|
| id | Long | Product ID |

### Example

```
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

**PUT** `/api/products/{id}`

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

### Error Response

```json
{
    "message": "Product not found."
}
```

---

## 5. Delete Product

### Endpoint

**DELETE** `/api/products/{id}`

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

### Error Response

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

# Category APIs

POST /api/categories

GET /api/categories

GET /api/categories/{id}

PUT /api/categories/{id}

DELETE /api/categories/{id}

# Purchase Order APIs

POST /api/purchase-orders

GET /api/purchase-orders

GET /api/purchase-orders/{id}

PUT /api/purchase-orders/{id}

DELETE /api/purchase-orders/{id}

# Vendor Estimate APIs

POST /api/vendor-estimates

GET /api/vendor-estimates

GET /api/vendor-estimates/{id}

PUT /api/vendor-estimates/{id}

# Delivery APIs

POST /api/deliveries

GET /api/deliveries

GET /api/deliveries/{id}

PUT /api/deliveries/{id}

# Receipt APIs

POST /api/receipts

GET /api/receipts

GET /api/receipts/{id}

# Invoice APIs

POST /api/invoices

GET /api/invoices

GET /api/invoices/{id}

PUT /api/invoices/{id}

# Payment APIs

POST /api/payments

GET /api/payments

GET /api/payments/{id}

# Notification APIs

GET /api/notifications

PUT /api/notifications/{id}/read

# Report APIs

GET /api/reports/department-spending

GET /api/reports/vendor-spending

GET /api/reports/monthly

GET /api/reports/top-vendors