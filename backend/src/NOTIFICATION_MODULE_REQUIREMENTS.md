# Notification Module Requirements Specification

## 1. Executive Summary & Module Purpose
The **Notification Module** serves as the central communication hub for the Enterprise Procurement System. It delivers real-time and asynchronous alerts, system notifications, approval requests, status updates, and critical system warnings to internal enterprise users (Requesters, Department Managers, Finance Managers, Procurement Officers, System Administrators) and external stakeholders (Vendors).

The purpose of this document is to define the functional, architectural, data model, API, and non-functional requirements for the Notification Module.

---

## 2. Core Capabilities & Channel Routing Architecture

### 2.1 Multi-Channel Delivery Capabilities
The Notification Module supports four primary notification channels:
1. **System / In-App Notifications**: Real-time bell alerts, top-navbar notifications, and dedicated notification dashboard pages.
2. **Email Notifications**: Asynchronous transactional emails (HTML formatted) for formal notifications such as Purchase Request approvals, Purchase Order issuances, and RFQ invitations.
3. **SMS Notifications**: Short message alerts for urgent escalation events (e.g., critical approval SLA breach, urgent delivery confirmation).
4. **Push Notifications**: Web/Mobile browser push notifications for real-time mobile and desktop user engagements.

### 2.2 Channel Routing Engine
- **Event-Driven Dispatch**: System events publish `NotificationEvent` payloads to an internal asynchronous event bus.
- **Preference Evaluation**: Before dispatching to a specific channel (Email, SMS, Push), the system checks target user preferences.
- **Fall-back & Escalation**: In-App notification is always recorded as the system audit log of record for user alerts.

---

## 3. Domain Event Triggers Catalog

| Domain Entity | Action / Event | Target Recipient | Notification Channel | Priority |
| :--- | :--- | :--- | :--- | :--- |
| **Purchase Request (PR)** | PR Created & Submitted | Department Approver / Line Manager | In-App, Email | HIGH |
| **Purchase Request (PR)** | PR Approved (Step Level) | Requester, Next Approver | In-App, Email | MEDIUM |
| **Purchase Request (PR)** | PR Fully Approved | Requester, Procurement Officer | In-App, Email | HIGH |
| **Purchase Request (PR)** | PR Rejected | Requester | In-App, Email | HIGH |
| **Purchase Order (PO)** | PO Issued to Vendor | Vendor Primary Contact, Requester | In-App, Email | URGENT |
| **RFQ / Tender** | RFQ Published | Eligible Category Vendors | In-App, Email | HIGH |
| **RFQ / Bid** | Bid Submitted by Vendor | Procurement Officer | In-App | MEDIUM |
| **Delivery / Receipt** | Goods Delivered & Inspected | Requester, Inventory Manager | In-App | MEDIUM |
| **Invoice** | Invoice Submitted / Flagged | Finance Officer | In-App, Email | HIGH |
| **Supplier Risk** | Compliance Expiry Warning | Vendor, Procurement Admin | Email, In-App | HIGH |
| **Budget** | Department Budget > 90% | Department Head, Finance Manager | Email, In-App | CRITICAL |

---

## 4. Data Models & Database Schema

### 4.1 Notifications Entity Schema (`notifications`)
```sql
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('Email', 'SMS', 'System', 'Push') NOT NULL DEFAULT 'System',
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notifications_user_read (user_id, is_read, is_deleted),
    INDEX idx_notifications_created_at (created_at)
);
```

### 4.2 User Preferences Schema (`user_notification_preferences`)
```sql
CREATE TABLE user_notification_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    email_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    sms_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    push_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    in_app_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_pref (user_id)
);
```

---

## 5. REST API Specification

### 5.1 Endpoints Overview

#### `GET /v1/notifications/my`
- **Description**: Retrieves paginated notifications for the currently authenticated user.
- **Parameters**:
  - `page` (integer, default: 0): Page index.
  - `size` (integer, default: 20): Page size.
  - `sort` (string, default: `createdAt,desc`): Sort order.
  - `isRead` (boolean, optional): Filter by read status (`true`/`false`).
  - `type` (string, optional): Filter by notification channel type (`SYSTEM`, `EMAIL`, `SMS`, `PUSH`).
- **Response**: `200 OK` with paginated `NotificationResponse` wrapped in `ApiResponse`.

#### `GET /v1/notifications/unread-count`
- **Description**: Returns total count of unread, non-deleted notifications for the current user.
- **Response**: `200 OK`
```json
{
  "success": true,
  "message": "Unread notification count fetched successfully.",
  "data": {
    "unreadCount": 5
  }
}
```

#### `PUT /v1/notifications/{id}/read`
- **Description**: Marks a specific notification as read.
- **Response**: `200 OK` with updated `NotificationResponse`.

#### `PUT /v1/notifications/read-all`
- **Description**: Marks all notifications for the authenticated user as read.
- **Response**: `200 OK` with affected count message.

#### `DELETE /v1/notifications/{id}`
- **Description**: Soft deletes a specific notification (`is_deleted = true`).
- **Response**: `200 OK`.

---

## 6. Service Architecture & Backend Design

### 6.1 Interface Definition (`NotificationService.java`)
```java
public interface NotificationService {
    Page<NotificationResponse> getMyNotifications(Boolean isRead, NotificationType type, Pageable pageable);
    long getUnreadCount();
    NotificationResponse markAsRead(Long notificationId);
    void markAllAsRead();
    void deleteNotification(Long notificationId);
    Notification createNotification(User targetUser, NotificationType type, String subject, String message);
}
```

### 6.2 Implementation Highlights (`NotificationServiceImpl.java`)
- **Security & Authorization**: Fetches user identity dynamically via `SecurityContextHolder`. Ensures users can only view and update their own notifications.
- **Transactional Scope**: Utilizes `@Transactional(readOnly = true)` for query paths and `@Transactional` for state updates.
- **Validation**: Throws `ResourceNotFoundException` when notification ID does not exist or does not belong to the user.

---

## 7. Frontend Integration Architecture

### 7.1 Notification Service Client (`notificationService.js`)
Exposes unified API service helpers:
- `getMyNotifications({ page, size, isRead, type })`
- `getUnreadCount()`
- `markAsRead(id)`
- `markAllAsRead()`
- `deleteNotification(id)`

### 7.2 UI Components
1. **`TopNavbar.jsx`**: Displays a bell icon with dynamic unread count pill badge (`dashboard-notification-badge`). Polls unread count on mount.
2. **`NotificationsPage.jsx`**: Provides full notification dashboard with:
   - Status Tabs: **All** vs. **Unread**.
   - Bulk Action: **Mark All as Read**.
   - Single item actions: **Mark as Read** (on click or button) and **Delete**.
   - Time formatting & badge indicators.

---

## 8. Non-Functional & Security Requirements
1. **Performance & Scalability**:
   - Database queries leverage compound indexes `(user_id, is_read, is_deleted)`.
   - Notification fetch endpoints must respond in < 100ms for default page sizes.
2. **Data Security & Isolation**:
   - Strict tenant and user isolation: users cannot access or modify notification records belonging to other users.
3. **Auditability**:
   - Soft deletion (`is_deleted`) preserves historic notification records for regulatory compliance and dispute resolution.

---

## 9. Verification & Acceptance Criteria
- [x] Requirement specification defined in `NOTIFICATION_MODULE_REQUIREMENTS.md`.
- [x] Backend `NotificationService` & `NotificationServiceImpl` created and unit tested.
- [x] `NotificationController` expanded with REST endpoints (`/my`, `/unread-count`, `/{id}/read`, `/read-all`, `/{id}`).
- [x] Frontend `notificationService.js` and `NotificationsPage.jsx` updated with filtering, mark as read, mark all as read, and delete.
- [x] Navbar bell icon updated with dynamic unread count badge.
