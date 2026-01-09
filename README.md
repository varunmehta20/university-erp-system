# 🎓 University ERP System

A Java-based **University ERP Desktop Application** built using **Java Swing**, **JDBC**, and **MySQL**, supporting **Student**, **Instructor**, and **Admin** roles with **strict role-based access control**.

---

## 📌 Author

- **Varun Mehta** (2024607)  


---

## 📖 Introduction

The **University ERP System** is a desktop-based academic management system designed to simulate **real-world university operations**.

It provides:

- Secure authentication  
- Role-based dashboards  
- Course registration  
- Grade management  
- Administrative control over the system  

The application follows a **layered architecture**:

This ensures **clean separation of concerns**, **scalability**, and **maintainability**.

---

## 👥 User Roles

### 👨‍🎓 Student

- Browse course catalog  
- Register / drop sections  
- View grades and transcript  
- View timetable  
- Export grades as CSV  
- Receive notifications  

---

### 👨‍🏫 Instructor

- View assigned sections  
- Define assessment components  
- Enter & import student scores  
- Compute final grades  
- Export grades and statistics  
- View class statistics  
- Receive notifications  

---

### 🛠️ Admin

- Create users (Student / Instructor / Admin)  
- Manage courses and sections  
- Assign instructors to sections  
- Enable / disable maintenance mode  
- Backup & restore database  
- Set registration deadlines  
- Send notifications  

---

## 🧠 System Features

🔐 Secure authentication with **password hashing**  
⏳ Account lockout after multiple failed login attempts  
🧩 Flexible grading components per section  
📊 CSV import/export for grades  
🗄️ Database backup & restore  
🚫 Strict role-based access control  
🔔 Notification system  
🧪 Fully tested (**no critical issues**)  

---

## ⚙️ Tech Stack

- **Language:** Java (JDK 17 / JDK 21)  
- **UI:** Java Swing  
- **Database:** MySQL 8.x  
- **Connectivity:** JDBC  
- **Security:** jBCrypt (password hashing)  
- **IDE:** IntelliJ IDEA  

---
## 🛠️ How to Run

### 1️⃣ Requirements

- Java JDK 17 or JDK 21  
- IntelliJ IDEA (Community / Ultimate)  
- MySQL Server 8.x  
- MySQL Connector (JDBC)  
- jBCrypt library  

---

### 2️⃣ Database Setup

The project uses **two MySQL databases**:

#### 🔐 Auth Database

Stores login credentials & roles

**Table:**  
- `users_auth`

#### 🎓 ERP Database (`univ_erp`)

**Tables:**  
- `students`  
- `instructors`  
- `courses`  
- `sections`  
- `enrollments`  
- `grades`  
- `assessment_components`  
- `settings`  
- `notification`  
- `section_labels`  

---

### 3️⃣ Database Configuration

Update the following fields in connector classes:

private static final String URL  = "jdbc:mysql://localhost:3306/authen_db";
private static final String USER = "root";
private static final String PASS = "your_mysql_password";


## 🗂️ Project Structure

```text
ERP/
│
├── src/
│   ├── authen/                 # Authentication logic
│   ├── erp/
│   │   ├── data/
│   │   ├── domain/
│   │   ├── dto/
│   │   ├── service/
│   │   └── ERPConnector.java
│   │
│   ├── ui/
│   │   ├── Admin/
│   │   ├── Instructor/
│   │   ├── Student/
│   │   ├── ERPMain.java
│   │   ├── LoginFrame.java
│   │   └── RoleRouter.java
│   │
│   └── Main.java
│
└── README.md

