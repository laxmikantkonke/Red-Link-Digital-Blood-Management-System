# Red-link: Blood Management System

Red-link is a comprehensive, Spring Boot-powered web application designed to streamline blood donation, request, and inventory management. It connects donors, hospitals, and those in need of blood through a centralized, easy-to-use platform.

## 🚀 Features

-   **User Authentication & Authorization**: Secure login and registration for Donors and Administrators.
-   **Blood Request Management**: Submit, track, and manage blood requests with urgency levels.
-   **Hospital Directory & Inventory**: Dynamic listing of hospitals and their real-time blood stock.
-   **Admin Dashboard**: Comprehensive overview for administrators to manage users, hospitals, and requests.
-   **Email Notifications**: Automated alerts for password resets and critical updates.
-   **Search & Filter**: Find donors and hospitals by city, state, or blood group.

## 🛠️ Tech Stack

-   **Backend**: Java 21, Spring Boot 2.7.14
-   **Security**: Spring Security
-   **Database**: MySQL 8.0
-   **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
-   **Build Tool**: Maven
-   **Email**: Spring Boot Starter Mail

## ⚙️ Quick Start

### Prerequisites

-   Java 21 or higher
-   Maven 3.x
-   MySQL 8.0

### Setup

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/your-repo/red-link.git
    cd red-link
    ```

2.  **Configure Database**:
    -   Create a database named `blood_link`.
    -   Update `src/main/resources/application.properties` with your MySQL credentials:
        ```properties
        spring.datasource.username=your_username
        spring.datasource.password=your_password
        ```

3.  **Run the Application**:
    ```bash
    mvn spring-boot:run
    ```

4.  **Access the App**:
    Open [http://localhost:8080](http://localhost:8080) in your browser.

## 📄 Documentation

For detailed technical information, including the data model and API details, please refer to [DOCUMENTATION.md](DOCUMENTATION.md).

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## ⚖️ License

This project is licensed under the MIT License - see the LICENSE file for details.
