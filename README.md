<div align="center">

# 🏢 Enterprise Procurement System

### 🚀 Smart • Secure • Automated • Role-Based Procurement Management

A full-stack enterprise procurement platform designed to streamline the complete procurement lifecycle — from requisition creation and approval to supplier management, purchase orders, delivery tracking, and analytics.

<br>

![Project Status](https://img.shields.io/badge/Project-Completed-success?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)
![Frontend](https://img.shields.io/badge/Frontend-React-61DAFB?style=for-the-badge&logo=react&logoColor=white)
![Backend](https://img.shields.io/badge/Backend-Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Database](https://img.shields.io/badge/Database-MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

<br>

**Developed as part of the Infosys Springboard Virtual Internship 7.0**

</div>

---

# 📌 Table of Contents

- [✨ Overview](#-overview)
- [🎯 Objectives](#-objectives)
- [🌟 Key Features](#-key-features)
- [👥 User Roles](#-user-roles)
- [🔄 Procurement Workflow](#-procurement-workflow)
- [🏗️ System Architecture](#️-system-architecture)
- [🛠️ Technology Stack](#️-technology-stack)
- [📂 Project Structure](#-project-structure)
- [📊 Dashboard Modules](#-dashboard-modules)
- [🤖 AI-Powered Supplier Recommendation](#-ai-powered-supplier-recommendation)
- [🚨 High-Priority Request Alerts](#-high-priority-request-alerts)
- [🔐 Security](#-security)
- [📧 Notifications](#-notifications)
- [☁️ Cloud Storage](#️-cloud-storage)
- [🗄️ Database](#️-database)
- [🚀 Getting Started](#-getting-started)
- [⚙️ Environment Configuration](#️-environment-configuration)
- [🧪 API Testing](#-api-testing)
- [📚 Documentation](#-documentation)
- [🎨 UI/UX](#-uiux)
- [📈 Future Enhancements](#-future-enhancements)
- [🧑‍💻 Development Practices](#-development-practices)
- [🏆 Project Outcome](#-project-outcome)
- [👨‍💻 Team](#-team)
- [📄 License](#-license)
- [🙏 Acknowledgements](#-acknowledgements)

---

# ✨ Overview

The **Enterprise Procurement System** is a full-stack web application developed to digitize and simplify enterprise procurement operations.

Traditional procurement processes often involve manual requests, emails, spreadsheets, approval delays, supplier coordination, and difficulty tracking purchase orders.

This platform brings these activities together into a **single centralized procurement system**.

The application provides role-based dashboards and workflows for employees, managers, finance users, procurement officers, and administrators.

### The system supports:

- 📝 Purchase requisition creation and management
- ✅ Requisition approval and rejection
- 🏢 Supplier management
- 🤖 AI-assisted supplier recommendations
- 📦 Purchase order generation
- 🚚 Delivery tracking
- 💰 Finance-related procurement activities
- 📊 Procurement analytics
- 🔔 Notifications and alerts
- 🚨 High-priority request monitoring
- 🔐 Role-based access control
- 🌓 Light and dark theme support

---

# 🎯 Objectives

The main objectives of the Enterprise Procurement System are:

### 1️⃣ Digitize Procurement

Replace manual procurement activities with a centralized digital platform.

### 2️⃣ Streamline Approval Processes

Provide a structured workflow for reviewing, approving, and rejecting purchase requisitions.

### 3️⃣ Improve Supplier Selection

Help procurement officers identify suitable suppliers using supplier performance and procurement-related factors.

### 4️⃣ Improve Visibility

Provide dashboards, KPIs, charts, alerts, and reports for different organizational roles.

### 5️⃣ Reduce Manual Work

Automate repetitive procurement activities and improve the overall efficiency of the procurement lifecycle.

### 6️⃣ Improve Decision Making

Provide analytics and AI-assisted recommendations to support better procurement decisions.

### 7️⃣ Maintain Security

Protect application resources using authentication, authorization, and role-based access control.

---

# 🌟 Key Features

| Feature | Description |
|---|---|
| 🔐 Authentication | Secure user login and authentication |
| 👥 Role-Based Access | Role-specific dashboards and permissions |
| 📝 Requisition Management | Create, edit, submit, and track purchase requisitions |
| ✏️ Requisition Editing | Edit eligible requests before approval |
| ✅ Approval Workflow | Structured approval process |
| ❌ Rejection Management | Reject requests through the appropriate workflow |
| 🏢 Supplier Management | Manage supplier information and records |
| 🤖 AI Supplier Recommendation | Recommend suitable suppliers using procurement factors |
| 🏆 Top Supplier Ranking | Display ranked supplier recommendations |
| 📦 Purchase Orders | Generate and manage purchase orders |
| 🚚 Delivery Tracking | Monitor procurement delivery progress |
| 📊 Analytics | Procurement statistics, KPIs, and charts |
| 🔔 Notifications | Procurement notifications and alerts |
| 🚨 Priority Management | Identify high-priority requisitions |
| 📧 Email Notifications | Email-based procurement communication |
| ☁️ Document Storage | Cloud-based document management |
| 🌓 Theme Support | Light and dark dashboard themes |
| 🔎 Filtering | Filter and search procurement records |
| 📈 Dashboard KPIs | Role-specific procurement performance indicators |

---

# 👥 User Roles

The system provides different functionality based on the user's organizational role.

---

## 👨‍💻 Employee

Employees can:

- 📝 Create purchase requisitions
- ✏️ Edit eligible requisitions
- 📤 Submit requisitions
- 👀 View submitted requests
- 📊 Track requisition status
- 🚨 Select and view request priority
- 📦 Track purchase-order information where applicable

---

## 👨‍💼 Manager

Managers can:

- 📋 View department requisitions
- 🔎 Review pending requests
- ✅ Approve requisitions
- ❌ Reject requisitions
- 🚨 Monitor high-priority requests
- 📊 View procurement KPIs
- 📈 Analyze requisition status
- 🎯 Monitor priority distribution

---

## 💰 Finance

Finance users can:

- 📋 Review relevant requisitions
- ⏳ Monitor pending requests
- 💵 Review procurement amounts
- 📊 View procurement statistics
- 🚨 Monitor high-priority requests
- ✅ Process requests according to the finance workflow

---

## 🛒 Procurement Officer

Procurement officers can:

- 📋 Review approved requisitions
- 🏢 Manage suppliers
- ⚖️ Compare supplier options
- 🤖 Request AI supplier recommendations
- 🏆 View top supplier recommendations
- 🎯 Select an AI-recommended supplier
- 📦 Generate purchase orders
- 📋 Track purchase orders
- 🚚 Monitor delivery status

---

## 👑 Administrator

Administrators can manage system-level activities, users, and organizational information according to the configured authorization policies.

---

# 🔄 Procurement Workflow

The procurement lifecycle follows a structured workflow:

```text
┌──────────────────────┐
│       Employee       │
│    Creates Request   │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│ Purchase Requisition │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│    Manager Review    │
└──────────┬───────────┘
           │
       ┌───┴────┐
       │        │
       ▼        ▼
   APPROVE    REJECT
       │
       ▼
┌──────────────────────┐
│    Finance Review    │
└──────────┬───────────┘
           │
           ▼
┌────────────────────────┐
│  Procurement Officer   │
└──────────┬─────────────┘
           │
           ▼
┌────────────────────────────┐
│ Supplier Selection /       │
│ AI Recommendation          │
└────────────┬───────────────┘
             │
             ▼
┌──────────────────────┐
│  Purchase Order      │
│      Generation      │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│ Delivery Tracking    │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│   Completed Order    │
└──────────────────────┘
🏗️ System Architecture

The application follows a full-stack architecture consisting of a React frontend, Spring Boot backend, MySQL database, and supporting external services.

                         ┌─────────────────────┐
                         │        Users        │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │   React Frontend    │
                         │                     │
                         │ • Dashboards        │
                         │ • Forms             │
                         │ • Charts            │
                         │ • Tables            │
                         │ • Navigation        │
                         └──────────┬──────────┘
                                    │
                              REST API / HTTP
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │   Spring Boot API   │
                         │                     │
                         │ • Controllers       │
                         │ • Services          │
                         │ • Repositories      │
                         │ • DTOs              │
                         │ • Security          │
                         └──────────┬──────────┘
                                    │
              ┌─────────────────────┼─────────────────────┐
              │                     │                     │
              ▼                     ▼                     ▼
       ┌─────────────┐      ┌─────────────┐       ┌─────────────┐
       │    MySQL    │      │ Cloudinary  │       │    Email    │
       │  Database   │      │   Storage   │       │   Service   │
       └─────────────┘      └─────────────┘       └─────────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │   AI Integration    │
                         │ Supplier Recommend. │
                         └─────────────────────┘
🛠️ Technology Stack
🎨 Frontend
Technology	Purpose
⚛️ React	User interface
🟨 JavaScript	Application logic
🎨 CSS	Styling and responsive layouts
📊 Chart Components	Procurement analytics
🌐 REST APIs	Frontend-backend communication
⚙️ Backend
Technology	Purpose
☕ Java	Backend programming
🌱 Spring Boot	Backend framework
🌐 REST API	Client-server communication
🗃️ Spring Data JPA	Database interaction
🔐 JWT	Authentication and authorization
📧 Spring Mail	Email notifications
🤖 AI API	AI-assisted procurement functionality
🗄️ Database

MySQL is used as the primary relational database.

The database stores procurement-related information such as:

👤 Users
👥 Roles
📝 Requisitions
🏢 Suppliers
📦 Purchase orders
✅ Approvals
🚚 Delivery information
📊 Procurement records
☁️ Supporting Services
Service	Purpose
☁️ Cloudinary	File and document storage
📧 Gmail SMTP	Email communication
🤖 AI Service	Supplier recommendation and procurement assistance
📂 Project Structure
Enterprise-procurement-system/
│
├── 📁 api-test/
│   └── API testing resources
│
├── 📁 assets/
│   └── Project assets
│
├── 📁 backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   │
│   │   └── test/
│   │
│   └── pom.xml
│
├── 📁 database/
│   ├── schema.sql
│   ├── sample_data.sql
│   ├── functions.sql
│   ├── procedures.sql
│   ├── triggers.sql
│   ├── views.sql
│   └── other database resources
│
├── 📁 documentation/
│   ├── Agile Documentation
│   ├── Design Documentation
│   ├── UTP
│   └── Defect Tracking
│
├── 📁 frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   └── assets/
│   │
│   ├── public/
│   └── package.json
│
├── 📁 ui-design/
│   └── UI/UX resources
│
├── 📄 .gitignore
├── 📄 LICENSE
└── 📄 README.md

Note: Generated folders such as node_modules, dist, and Maven target directories should not be committed to the repository.

📊 Dashboard Modules

The system provides dedicated dashboards for different roles.

👨‍💻 Employee Dashboard

The Employee Dashboard provides:

👋 Personalized welcome message
📊 KPI cards
📝 Requisition creation
📋 Request history
📈 Request status
🚨 Request priority
✏️ Request editing where permitted
🌓 Light/Dark theme support
👨‍💼 Manager Dashboard

The Manager Dashboard provides:

📊 Procurement KPIs
⏳ Pending requisitions
✅ Approved requisitions
❌ Rejected requisitions
📈 Requisition status analytics
🎯 Priority distribution
🚨 High-priority request alerts
📋 Requisition tables
🔎 Filtering and review functionality
🌓 Light/Dark theme support
💰 Finance Dashboard

The Finance Dashboard provides:

📊 Finance-related KPIs
⏳ Pending requests
✅ Approved requests
❌ Rejected requests
📈 Procurement analytics
🎯 Priority distribution
🚨 High-priority request alerts
📋 Request management
🌓 Light/Dark theme support
🛒 Procurement Officer Dashboard

The Procurement Officer Dashboard provides:

📊 Procurement KPIs
📦 Purchase-order statistics
🏢 Supplier information
📋 Approved requisitions
🤖 AI supplier recommendations
🚨 High-priority request alerts
📈 Procurement analytics
📦 Purchase-order management
🚚 Delivery tracking
🌓 Light/Dark theme support
🤖 AI-Powered Supplier Recommendation

The system provides AI-assisted supplier recommendation functionality for procurement officers.

When generating a Purchase Order, the procurement officer can request supplier recommendations.

The system evaluates procurement-related factors such as:

⭐ Supplier rating
💰 Price competitiveness
🚚 Delivery performance
✅ Supplier eligibility

The suppliers are ranked and the top recommendations are displayed.

Recommendation Flow
Generate Purchase Order
          │
          ▼
   Request AI Recommendation
          │
          ▼
 ┌────────────────────────┐
 │   Supplier Ranking     │
 ├────────────────────────┤
 │ 🥇 Top Recommendation  │
 │ 🥈 Second              │
 │ 🥉 Third               │
 └───────────┬────────────┘
             │
             ▼
    Use AI Recommendation
             │
             ▼
      Supplier Selected
             │
             ▼
    Generate Purchase Order

The recommendation feature is designed to reduce manual supplier comparison and assist procurement officers in making informed decisions.

🚨 High-Priority Request Alerts

The system supports priority-based procurement monitoring.

When a requisition is marked as High Priority, the relevant dashboard displays an alert.

High-priority requests are visually highlighted using:

🔴 Red priority indicators
🚨 Alert cards
📊 Priority distribution charts
📋 Priority columns in request tables

The alert is displayed only when relevant high-priority requests require attention.

Users can select the alert to navigate directly to the relevant request view.

🔐 Security

Security is an important part of the application.

The system uses:

🔑 Authentication
🎫 JWT-based authorization
👥 Role-based access control
🔒 Protected API endpoints
🛡️ Environment-based configuration
🚫 Role-specific access restrictions
Security Best Practice

Sensitive information such as:

Database passwords
API keys
JWT secrets
Email credentials
Cloud service secrets

should be stored using environment variables or secure configuration and must not be committed to GitHub.

📧 Notifications

The application supports notification mechanisms for important procurement events.

Notifications can be associated with:

🔔 New requisitions
✅ Approvals
❌ Rejections
🚨 High-priority requests
📦 Purchase-order activities
🚚 Delivery-related activities

Email functionality is integrated using SMTP configuration.

☁️ Cloud Storage

The application supports cloud-based document and file storage using Cloudinary.

Typical use cases include:

📄 Procurement documents
📎 Supporting attachments
🧾 Supplier documents
📁 Other procurement-related files
🗄️ Database

The project uses MySQL as its relational database.

The repository contains database resources for:

🏗️ Database schema
📊 Sample data
🔧 Functions
⚙️ Stored procedures
🔔 Triggers
👁️ Database views
🚀 Indexes
🤖 Supplier recommendation data
🔄 Database migrations

Example database configuration:

spring.datasource.url=jdbc:mysql://localhost:3306/procurement_db
spring.datasource.username=YOUR_DATABASE_USERNAME
spring.datasource.password=YOUR_DATABASE_PASSWORD

⚠️ Never commit real database passwords to the repository.

🚀 Getting Started

Follow the steps below to run the project locally.

1️⃣ Prerequisites

Install the following:

☕ Java 17 or later
🟢 Node.js
📦 npm
🗄️ MySQL
🔧 Maven
🐙 Git
2️⃣ Clone the Repository
git clone https://github.com/Tarunika-73/Enterprise-procurement-system.git

Move into the project:

cd Enterprise-procurement-system
🗄️ 3️⃣ Setup MySQL

Make sure MySQL is installed and running.

Create the database:

CREATE DATABASE procurement_db;

Execute the required SQL scripts from the database/ directory according to the project setup.

⚙️ 4️⃣ Configure Backend

Navigate to the backend:

cd backend

Configure your local database and service credentials.

Example:

spring.datasource.url=jdbc:mysql://localhost:3306/procurement_db
spring.datasource.username=YOUR_DATABASE_USERNAME
spring.datasource.password=YOUR_DATABASE_PASSWORD

jwt.secret_key=YOUR_JWT_SECRET

Configure external services where required:

spring.mail.username=YOUR_EMAIL
spring.mail.password=YOUR_EMAIL_APP_PASSWORD

cloudinary.cloud_name=YOUR_CLOUD_NAME
cloudinary.api_key=YOUR_CLOUDINARY_API_KEY
cloudinary.api_secret=YOUR_CLOUDINARY_API_SECRET

groq.api.key=YOUR_AI_API_KEY
▶️ 5️⃣ Run Backend

From the backend directory:

mvn spring-boot:run

The backend will start on the configured Spring Boot port.

🎨 6️⃣ Run Frontend

Open another terminal and navigate to the frontend:

cd frontend

Install dependencies:

npm install

Start the development server:

npm run dev

The frontend will be available at the URL displayed in the terminal.

🧪 7️⃣ Build and Test
Frontend Build
npm run build
Frontend Lint
npm run lint
Backend Tests

From the backend directory:

mvn clean test
⚙️ Environment Configuration

Sensitive configuration should be maintained outside the source code.

Example environment configuration:

DATABASE_USERNAME=your_database_username
DATABASE_PASSWORD=your_database_password

JWT_SECRET=your_jwt_secret

MAIL_USERNAME=your_email
MAIL_PASSWORD=your_email_app_password

CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret

AI_API_KEY=your_ai_api_key

Use a local environment/configuration file for actual values.

🔐 Never commit real credentials or API keys to GitHub.

🧪 API Testing

The repository contains an api-test/ directory for API testing resources.

The application exposes REST APIs for major procurement operations such as:

Authentication
      │
      ▼
    Users
      │
      ▼
 Requisitions
      │
      ▼
   Approvals
      │
      ▼
  Suppliers
      │
      ▼
Purchase Orders
      │
      ▼
  Delivery

API testing can be performed using tools such as:

🟠 Postman
🔵 REST clients
🌐 API testing tools
📚 Documentation

Project documentation is maintained inside the documentation/ directory.

The documentation covers project development and testing activities such as:

📋 Agile documentation
📝 User stories
📅 Sprint planning
🔄 Sprint retrospectives
🧪 Unit Test Plan
🐞 Defect tracking
🏗️ Design documentation
📊 Project planning
🎨 UI/UX

The application follows a modern enterprise dashboard design.

The interface includes:

🎨 Consistent visual design
📊 KPI cards
📈 Analytics charts
🌓 Light/Dark theme
📱 Responsive layouts
🔔 Notification indicators
🚨 Priority alerts
🧭 Role-specific navigation
📋 Structured tables
✏️ Request editing
🤖 AI recommendation interface

The ui-design/ directory contains relevant UI/UX resources.

📈 Future Enhancements

Potential future improvements include:

📱 Mobile application
📊 Advanced procurement analytics
🤖 More intelligent supplier scoring
📈 Procurement forecasting
💰 Advanced spend analysis
📦 Inventory integration
🔗 ERP integration
🔔 Real-time push notifications
📄 Automated procurement document generation
🧠 Advanced AI procurement assistance
☁️ Cloud deployment
🔄 CI/CD automation
🧑‍💻 Development Practices

The project follows collaborative Git-based development practices.

The general development workflow is:

       Create Feature
             │
             ▼
       Create Branch
             │
             ▼
          Develop
             │
             ▼
           Test
             │
             ▼
          Commit
             │
             ▼
           Push
             │
             ▼
      Pull Request
             │
             ▼
       Code Review
             │
             ▼
           Merge

Agile practices are used for planning, sprint execution, tracking, and retrospectives.

📌 Project Highlights
Area	Implementation
🖥️ Frontend	React-based enterprise dashboard
⚙️ Backend	Spring Boot REST API
🗄️ Database	MySQL
🔐 Security	JWT + Role-Based Access
🤖 AI	AI-assisted supplier recommendation
☁️ Storage	Cloudinary integration
📧 Communication	Email notifications
📊 Analytics	KPIs and procurement charts
🚨 Priority	High-priority request monitoring
📦 Procurement	Purchase-order generation
🚚 Delivery	Delivery tracking
🌓 UI	Light/Dark theme support
📁 Repository Organization

The repository is organized into separate areas for application development, database management, testing, documentation, and UI resources.

Enterprise-procurement-system/
│
├── backend/          → Spring Boot backend
│
├── frontend/         → React frontend
│
├── database/         → MySQL scripts and database resources
│
├── documentation/    → Agile, UTP, Design and Defect documents
│
├── api-test/         → API testing resources
│
├── ui-design/        → UI/UX resources
│
├── assets/           → Project assets
│
├── LICENSE           → MIT License
│
├── .gitignore        → Git ignored files
│
└── README.md         → Project documentation
🏆 Project Outcome

The Enterprise Procurement System provides a centralized platform for managing procurement activities across multiple organizational roles.

The system brings together:

Requisition
     ↓
Approval
     ↓
Finance
     ↓
Supplier Selection
     ↓
Purchase Order
     ↓
Delivery
     ↓
Completion

into an integrated procurement workflow.

The project focuses on improving:

Efficiency • Transparency • Automation • Control • Decision Making

👨‍💻 Team
Enterprise Procurement System

Developed as part of the:

🎓 Infosys Springboard Virtual Internship 7.0

The project was collaboratively developed using:

⚛️ React
☕ Java
🌱 Spring Boot
🗄️ MySQL
🔐 JWT
🌐 REST APIs
🤖 AI Integration
🐙 Git & GitHub
📋 Agile Methodology
📄 License

This project is licensed under the MIT License.

See the LICENSE file for details.

🙏 Acknowledgements

Special thanks to:

🎓 Infosys Springboard
🏢 Infosys Springboard Internship Program
👨‍🏫 Project mentors and coordinators
👥 Team members and contributors

for providing the opportunity, guidance, and support to develop this Enterprise Procurement System.

<div align="center">