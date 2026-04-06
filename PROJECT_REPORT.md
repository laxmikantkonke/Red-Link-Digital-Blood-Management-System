# RED-LINK: Project Report

---

## 1. Introduction of Project
**RED-LINK** is a centralized, real-time blood management platform designed to connect blood donors directly with those in need. By bridging the gap between hospitals, donors, and recipients, the system ensures that critical medical requirements are met swiftly, reducing the life-threatening delays caused by the unavailability of blood.

## 2. Product Overview and Summary

### 2.1 Purpose
The primary purpose is to automate and streamline the blood procurement process. It replaces manual paperwork with a digital ledger for blood inventory and provides a public search interface for genuine donors.

### 2.2 Scope
- **User Management**: Unified registration for Donors and Recipients.
- **Inventory Tracking**: Real-time stock levels of various blood groups across multiple hospitals.
- **Request Processing**: A workflow for posting, viewing, and fulfilling blood requests.
- **Discovery**: Location-aware search for hospitals and donors.

### 2.3 Overview
The system is built as a robust Java-based web application. It utilizes server-side rendering for security and speed, with a dedicated administrative panel for governance.

### 2.4 Feasibility Study
- **Technical**: Leverages the high performance of Java 21 and Spring Boot, making it highly scalable and maintenance-friendly.
- **Operational**: The intuitive UI ensures that even users with minimal technical knowledge can post requests or find donors easily.

## 3. Overall Description

### 3.1 Product Feature
- **Nearby Hospital Search**: Uses GPS coordinates to find medical facilities within 20km.
- **Real-time Inventory**: Hospitals can update their exact number of blood units available.
- **Role-Based Access**: Specialized views for Administrators and standard Users.
- **Offline Readiness**: All UI assets (Bootstrap, Fonts, Icons) are served locally to ensure functionality without stable internet.

### 3.2 Technology Used (Stack)
- **Backend**: Spring Boot 2.7, Spring Security, Spring JPA.
- **Frontend**: Thymeleaf, Bootstrap 5, Vanilla JS, jQuery/Select2.
- **Database**: MySQL 8.
- **Tools**: Maven, Hibernate.

### 3.3 User Classes
- **Administrators**: Manage system integrity, users, and hospital lists.
- **Donors/Recipients**: Interact with requests, search stock, and update personal health profiles.
- **Moderators**: (Future) Regional staff for verification.

### 3.4 General Constraints
- Requires a standard Browser with JavaScript enabled.
- Database requires MySQL 8.0 support.
- Requires standard server hosting (Tomcat/AWS).

## 4. Requirements

### 4.1 Functional Requirements
1. Users must be able to register and login securely.
2. Admins must be able to add/remove hospitals.
3. Users must be able to search donors by City and Blood Group.
4. The system must send email notifications for status updates.

### 4.2 User Interface Requirements
- **Responsiveness**: Mobile-first design for hospital access on the go.
- **Visual Clarity**: High-contrast icons for medical urgency.
- **Performance**: Rapid loading via local caching of styling assets.

## 5. Design

### 5.1 High Level Design
The system follows the **Model-View-Controller (MVC)** pattern:
- **Model**: JPA Entities representing Data.
- **View**: Thymeleaf templates for UI.
- **Controller**: RESTful and Web Controllers managing logic flow.

### 5.2 Database Design
- **Users**: Credentials and medical info.
- **Hospitals**: Location and contact details.
- **Inventory**: Quantity per blood group.
- **Requests**: Patient info and urgency levels.

## 6. Interface (UI)
The interface is designed with a **Glassmorphism** aesthetic, featuring:
- **Dashboard**: Card-based metrics for requests and stock.
- **Maps**: Real-time location detection for nearby hospitals.
- **Forms**: User-friendly validation for registration and medical data.

## 7. Test Report
- **Unit Testing**: Verified critical services (User, Mail).
- **Integration Testing**: Confirmed database persistence for requests.
- **UI Testing**: Cross-browser verification of Bootstrap layouts and local asset loading.

## 8. Project Management Methodology
The project followed an **Incremental Development** approach. Features were built and tested in cycles—starting from the core User authentication, followed by the Inventory system, and finally the polished location-aware search features.

## 9. Future Scope
- **Mobile Application**: Native Android/iOS versions using Flutter.
- **AI Integration**: Predictive analytics for future blood demand based on historical data.
- **Payment Gateway**: To handle logistics/hospital donation funds if required.
- [ ] User classes and constraints added
- [ ] Requirements and Design section completed
- [ ] Test report and management methodology included

## 10. User Interface (Screenshots)

> [!TIP]
> **How to add your logic screenshots**: 
> 1. Run the application and take screenshots of the core pages.
> 2. Save the images in your project (e.g., in `src/main/resources/static/images/sreenshots/`).
> 3. Replace the placeholder paths below with your actual image paths.

### 10.1 Home Page
![Home Page](path/to/home.png)
*Figure 1: The landing page showing the central search and mission.*

### 10.2 User Dashboard
![Dashboard](path/to/dashboard.png)
*Figure 2: Personal dashboard showing metrics and recent requests.*

### 10.3 Nearby Hospitals
![Nearby Search](path/to/nearby.png)
*Figure 3: GPS-based hospital discovery result page.*

### 10.4 Admin Panel
![Admin Module](path/to/admin.png)
*Figure 4: Secure portal for user and infrastructure management.*

### 10.5 Blood Inventory
![Inventory View](path/to/inventory.png)
*Figure 5: Detailed view of hospital blood group availability.*
