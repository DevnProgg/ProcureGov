# ProcureGov - Government Procurement Management System

ProcureGov is a comprehensive web-based platform designed to streamline and automate the government procurement process. It facilitates transparent tender management, competitive bidding, and rigorous evaluation workflows, ensuring efficiency and accountability in public procurement.

## Features

### User Management & Security
- **Role-Based Access Control (RBAC):** Distinct permissions for Procurement Officers, Board Members, and Suppliers.
- **Secure Authentication:** Robust login system with password hashing.
- **Account Profiles:** Detailed management of employee and supplier profiles.

### Tender Management
- **Lifecycle Management:** Tenders progress through stages: `DRAFT`, `OPEN`, `CLOSED`, `UNDER_EVALUATION`, `EVALUATED`, and `AWARDED`.
- **Creation & Editing:** Procurement officers can create detailed tender notices with technical specifications and estimated values.
- **Document Attachments:** Support for uploading tender notice documents (PDFs).

### Bidding & Evaluation
- **Supplier Bidding:** External suppliers can submit competitive bids, including pricing, compliance statements, and supporting documents.
- **Multi-Criteria Evaluation:** Board members evaluate bids based on:
  - Price Score
  - Technical Compliance
  - Delivery Timeline
- **Automated Scoring:** Weighted total calculation for objective bid comparison.

### Awarding & Notifications
- **Award Management:** Formalize tender results with justification and awarded value.
- **Email Notifications:** Automated background tasks to notify suppliers of tender updates and award results.
- **PDF Reports:** Generation of procurement-related documents and notices.

## Tech Stack

- **Backend:** Java 17, Jakarta EE (Servlets, JSP)
- **Frontend:** JSP, JSTL, Vanilla CSS, JavaScript
- **Database:** MySQL 8.x
- **Libraries:**
  - **Lombok:** Reducing boilerplate code.
  - **OpenPDF:** PDF document generation.
  - **JavaMail:** Handling email notifications.
  - **MySQL Connector:** Database connectivity.

## Project Structure

- `src/main/java/com/ProcureGov/`
  - `controller/`: Servlet-based request handling.
  - `service/`: Business logic layer.
  - `repository/`: Data Access Object (DAO) layer for MySQL.
  - `model/`: Entity definitions.
  - `backgroundtasks/`: Managed tasks like the Email Broker.
  - `middleware/`: Filters for authentication and authorization.
  - `utils/`: Utility classes for PDF generation, email sending, and file handling.
  - `dto/`: Data Transfer Objects for encapsulating data between layers.
- `src/main/webapp/`
  - `WEB-INF/views/`: JSP templates for the UI.
  - `css/` & `js/`: Static assets.
  - `uploads/`: Storage for tender and bid documents.
- `schema/`: SQL scripts for database initialization.

## Getting Started

### Prerequisites
- JDK 17 or higher
- Apache Tomcat 10.1+
- MySQL Server 8.0+

### Setup
1. **Database:**
   - Execute the `schema/schema.sql` script in your MySQL instance to create the `procure_gov` database and tables.
2. **Configuration:**
   - Update database connection details in `src/main/webapp/META-INF/context.xml` (or your local Tomcat `context.xml`).
   - Configure SMTP settings in the same `context.xml` using `EMAIL_HOST`, `EMAIL_PORT`, `EMAIL_USERNAME`, `EMAIL_PASSWORD`, `EMAIL_AUTH`, `EMAIL_TLS`, and optionally `EMAIL_FROM_ADDRESS` / `EMAIL_FROM_NAME`.
   - If you use Gmail, `EMAIL_PASSWORD` must be a Google **App Password** when 2-Step Verification is enabled; your normal account password will be rejected with `534 5.7.9`.
3. **Deploy:**
   - Copy the generated `target/ProcureGov-1.0-SNAPSHOT.war` to your Tomcat `webapps` directory.

## Roles
- **Procurement Officer:** Manages tenders, uploads notices, and finalizes awards.
- **Board Member:** Evaluates submitted bids and provides scores.
- **Supplier:** Views open tenders and submits competitive bids.

---
*Developed for efficient and transparent public sector procurement.*
