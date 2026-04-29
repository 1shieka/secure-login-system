Secure Login System

A Java-based secure authentication system built using **OOP concepts, Swing GUI, and MySQL**, designed to demonstrate real-world login security mechanisms.

## Features
-  **Password Hashing (SHA-256)**  
-  **Auto Student Registration**  
-  **Role-Based Access (Admin / Student)**  
-  **Failed Login Attempt Tracking (Persistent)**  
-  **Temporary Account Blocking (2 Minutes)**  
-  **Admin Panel (View & Delete Users)**  
-  **Last Login / Attempt Timestamp**

---

##  Concepts Covered
- Object-Oriented Programming (OOP)
  - Abstraction  
  - Inheritance  
  - Polymorphism  
  - Encapsulation  
- JDBC (Database Connectivity)  
- SHA-256 Hashing  
- Layered Architecture (UI → Service → DAO → DB)  

---

##  Project Structure
Secure_Login_Project/
│
├── src/
│ ├── model/ # User, Admin, Student
│ ├── dao/ # Database operations (UserDAO)
│ ├── service/ # Login logic (LoginService)
│ ├── ui/ # Swing UI (LoginUI, DashboardUI)
│ ├── util/ # DBConnection, HashUtil
│ └── Main.java
│
├── lib/ # MySQL Connector JAR
├── README.md
└── .gitignore



---

## Tech Stack
- Java (OOP)
- Java Swing (GUI)
- JDBC
- MySQL
- SHA-256 (MessageDigest)

---

## Database Schema

**Table: users**

| Field | Description |
|------|------------|
| id | Primary Key |
| username | Unique username |
| password_hash | Encrypted password |
| role | ADMIN / STUDENT |
| failed_attempts | Login attempt count |
| last_login | Timestamp |

---

## How to Run

1. Compile
javac -cp ".:lib/mysql-connector-j-9.6.0.jar" -d . $(find src -name "*.java")

2. Run
java -cp ".:lib/mysql-connector-j-9.6.0.jar" Main
