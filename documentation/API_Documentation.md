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