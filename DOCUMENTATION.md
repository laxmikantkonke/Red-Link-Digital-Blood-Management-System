# Red-link: Blood Management System
**Project Documentation & User Manual**

---

## 1. Executive Summary

**Red-link** is a centralized web-based platform designed to bridge the gap between blood donors, hospitals, and patients in need. The system facilitates real-time communication, streamlines blood request processing, and manages hospital blood inventories effectively. By leveraging modern web technologies, Red-link aims to reduce the response time in critical medical emergencies.

---

## 2. System Architecture

### 2.1 Technology Stack

#### Backend
*   **Framework**: **Spring Boot 2.7.14 (Java 21)**
    *   *Role*: The core application framework providing dependency injection, embedded web server (Tomcat), and auto-configuration to speed up development.
*   **Data Access**: **Spring Data JPA & Hibernate**
    *   *Role*: Handles Object-Relational Mapping (ORM). It allows Java objects (Entities) to be automatically saved and retrieved from the database without writing complex SQL queries manually.
*   **Database**: **MySQL 8.0.33**
    *   *Role*: A high-performance relational database management system used to store all persistent data like user profiles, hospital lists, and blood requests.
*   **Security**: **Spring Security**
    *   *Role*: Robust Role-Based Access Control (RBAC). It manages user authentication (login/logout), session management, and protects sensitive admin routes (`/admin/**`) from unauthorized access.
*   **Input Validation**: **Hibernate Validator (JSR-380)**
    *   *Role*: Uses annotations like `@NotBlank` and `@Email` in the Entity classes to ensure only valid data is stored in the database.
*   **Communication**: **Spring Boot Mail**
    *   *Role*: Integrated service for sending automated email notifications (e.g., for blood request updates or registration confirmations).
*   **Build Tool**: **Maven**
    *   *Role*: Manages project dependencies, build lifecycles, and ensures consistent builds across different environments.

#### Frontend
*   **Template Engine**: **Thymeleaf** (Server-side rendering for secure and SEO-friendly pages)
*   **UI Framework**: **Bootstrap 5.1.3** (Primary framework for responsive layouts, cards, and navigation)
*   **Design System**: **Custom CSS3** featuring:
    *   **Glassmorphism**: Modern frosted-glass UI effects on dashboard cards.
    *   **Micro-animations**: Smooth transitions (cubic-bezier) and pulse effects for critical elements.
    *   **Design Tokens**: CSS Variables for a consistent brand color palette (Emerald/Dark Blue).
*   **Interactivity**: **Vanilla JavaScript** & **jQuery** for dynamic form validation, real-time search, and theme/preference management.
*   **Enhanced Components**:
    *   **Select2**: Searchable dropdowns for location and blood group selection.
    *   **FontAwesome 6.0.0**: Comprehensive icon set for intuitive navigation.
    *   **Outfit Typography**: Modern, clean font (served locally) for a professional look.

### 2.2 Functional Modules

1.  **Authentication Module**: Handles user registration, login, and secure session management.
2.  **Donor Management**: Allows users to register as donors, update profiles, and view donation history.
3.  **Hospital Module**: Manages hospital profiles, inventory levels (`BloodInventory`), and responds to blood requests.
4.  **Admin Dashboard**: A centralized control panel for managing users, hospitals, and viewing system-wide analytics.
5.  **Search & Discovery**: Enables users to find donors or hospitals based on City, State, and Blood Group.

### 2.3 User Roles & Permissions
*   **Admin (`ROLE_ADMIN`)**: Has full control over the system, including deleting users and blood requests, and managing hospital inventory.
*   **User (`ROLE_USER`)**: Standard access to post blood requests, search for donors, and view hospital information.
*   **Moderator (`ROLE_MODERATOR`)**: A placeholder role for future staff-level permissions.

---

## 3. Database Schema (Data Model)

The application uses a Relational Database with the following key entities:

### 3.1 Core Entities

*   **User**: Represents system users (Donors, Admins).
    *   *Fields*: `id`, `fullName`, `email`, `password`, `bloodGroup`, `city`, `state`, `roles`.
*   **Hospital**: Represents medical institutions.
    *   *Fields*: `id`, `name`, `address`, `city`, `contactNumber`.
*   **BloodInventory**: Tracks blood stock per hospital.
    *   *Fields*: `id`, `hospital_id` (FK), `bloodRequest` (FK/Type), `quantity`, `lastUpdated`.
*   **BloodRequest**: Records requests for blood.
    *   *Fields*: `id`, `requester_id` (FK), `patientName`, `bloodGroup`, `urgencyLevel`, `status` (PENDING, FULFILLED, REJECTED).

---

## 4. API & Controller Overview

The system is organized into several controllers handling specific routes:

| Controller | Base Path | Description |
| :--- | :--- | :--- |
| **AuthController** | `/auth`, `/login`, `/register` | Handles user sign-up, sign-in, and resets. |
| **AdminController** | `/admin/**` | Protected routes for User/Hospital management. |
| **BloodRequestController**| `/requests/**` | Creating and viewing status of blood requests. |
| **HospitalController** | `/hospitals/**` | Public viewing of hospital lists and stocks. |
| **DashboardController** | `/dashboard` | User-specific dashboard (Donor/Hospital view). |

---

## 5. Installation & Setup Guide

### 5.1 Prerequisites
*   **Java Development Kit (JDK)**: Version 21
*   **Maven**: 3.6+
*   **MySQL Server**: 8.0+

### 5.2 Configuration
1.  **Clone the Source**:
    ```bash
    git clone <repository-url>
    cd red-link
    ```
2.  **Database Setup**:
    *   Open MySQL Workbench or CLI.
    *   Create the schema: `CREATE DATABASE blood_link;`
    *   The application will automatically create tables (`hibernate.ddl-auto=update` is recommended for dev).
3.  **Properties File**:
    *   Edit `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/blood_link
    spring.datasource.username=root
    spring.datasource.password=YOUR_PASSWORD
    ```

### 5.3 Running the Application
Run the following command in the project root:
```bash
mvn spring-boot:run
```
The server will start on port **8080**.
Access URL: `http://localhost:8080`

---

## 6. User Guide

### For Admins
*   Login with Admin credentials.
*   Navigate to **Admin Dashboard**.
*   **Manage Users**: View, edit, or delete registered users.
*   **Manage Hospitals**: Add new hospitals and update their details.

### For Donors/Users
*   **Register**: Sign up with valid details (Name, Blood Group, Location).
*   **Request Blood**: Fill out the request form with urgency level.
*   **Dashboard**: Track the status of your requests.
*   **Search**: Use the home page to find blood availability in nearby hospitals.

### 6.1 Nearby Hospitals Feature Guide
This feature allows users to find hospitals within a **20 km radius** of their current location.

**For Users (How to Search):**
1.  Navigate to **Nearby Hospitals** in the top menu.
2.  Click the **"Find Near Me"** button.
3.  **Allow Location Access** when prompted by your browser.
4.  The system will automatically detect your GPS coordinates (Latitude & Longitude).
5.  A list of hospitals near you will appear instantly.
6.  *Note: If no hospitals are found, try searching by City manually.*

**For Admins (How to Enable):**
For the search to work, hospitals must have valid coordinates.
1.  Go to **Admin Dashboard** > **Add Hospital**.
2.  Enter the hospital details (Name, City, etc.).
3.  **Crucial Step**: Enter the **Latitude** and **Longitude** of the hospital.
    *   *Tip: You can get these numbers from Google Maps (Right-click a location).*
4.  Save the hospital. It will now appear in nearby searches for users in that area.

---

---

## 7. User Interface (Screenshots)

*Please insert screenshots of the application below.*

### 7.1 Home Page
![Home Page](path/to/home-page-screenshot.png)
*Figure 1: Landing page with search functionality.*

### 7.2 Login & Registration
![Login Page](path/to/login-screenshot.png)
*Figure 2: User authentication screen.*

### 7.3 User Dashboard
![User Dashboard](path/to/user-dashboard-screenshot.png)
*Figure 3: Donor dashboard showing blood requests.*

### 7.4 Admin Panel
![Admin Panel](path/to/admin-panel-screenshot.png)
*Figure 4: Admin controls for managing users and hospitals.*

### 7.5 Hospital Inventory
![Hospital Inventory](path/to/hospital-inventory-screenshot.png)
*Figure 5: Real-time blood stock view.*

---
*Generated by Red-link Application Team*
