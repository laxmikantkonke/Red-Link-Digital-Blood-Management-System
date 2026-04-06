# 🩸 RedLink - Digital Blood Management System

## 📌 Overview

RedLink is a web-based Blood Management System designed to streamline blood donation, requests, and inventory management. It connects donors, recipients, and administrators on a single platform, making the process efficient and accessible.

---

## 🚀 Features

* 🧑‍🤝‍🧑 Donor Registration & Management
* 🩸 Blood Request System
* 🏥 Blood Inventory Tracking
* 🔐 Admin Dashboard
* 📧 Email Notification Support (optional)
* 🔎 Search & Filter Donors/Blood Groups

---

## 🛠️ Tech Stack

* **Backend:** Java, Spring Boot
* **Frontend:** Thymeleaf, HTML, CSS
* **Database:** MySQL
* **ORM:** Hibernate / JPA
* **Build Tool:** Maven

---

## 📂 Project Structure

```
Red-Link-Digital-Blood-Management-System/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── templates/
│   │       ├── static/
│   │       ├── application.properties (ignored)
│   │       └── application-example.properties
├── pom.xml
├── README.md
└── .gitignore
```

---

## ⚙️ Setup Instructions

### 1️⃣ Clone the Repository

```
git clone https://github.com/laxmikantkonke/Red-Link-Digital-Blood-Management-System.git
cd Red-Link-Digital-Blood-Management-System
```

---

### 2️⃣ Configure Application Properties

* Navigate to:

  ```
  src/main/resources/
  ```
* Copy the template file:

  ```
  application-example.properties
  ```
* Rename it to:

  ```
  application.properties
  ```

---

### 3️⃣ Update Configuration

Edit `application.properties` and add your details:

```
spring.datasource.url=jdbc:mysql://localhost:3306/redlink_db
spring.datasource.username=root
spring.datasource.password=yourpassword
```

---

### 4️⃣ Create Database

Open MySQL and run:

```
CREATE DATABASE redlink_db;
```

---

### 5️⃣ Run the Application

Using Maven:

```
mvn spring-boot:run
```

Or run the main class from your IDE.

---

### 6️⃣ Access the Application

Open your browser:

```
http://localhost:8080
```

---

## 🔐 Security Note

* `application.properties` is **not included in GitHub** to protect sensitive data.
* Use `application-example.properties` as a reference template.

---

## 📸 Screenshots (Optional)
![Home Page](https://github.com/laxmikantkonke/Red-Link-Digital-Blood-Management-System/blob/main/HOME.jpeg?raw=true)

---

## 💡 Future Enhancements

* REST API integration
* Role-based authentication (JWT / Spring Security)
* Deployment (AWS / Docker)
* Mobile app integration

---

## 👨‍💻 Author

**Laxmikant Konke**

---

