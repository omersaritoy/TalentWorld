# 🚀 TalentWorld Backend

**TalentWorld Backend Application** is a RESTful API designed to manage job postings and application processes.

This project includes:

* 👤 User management
* 🧑 Talent profile creation and management
* 🏢 Job posting system
* 📩 Job application system
* 🔐 JWT-based authentication
* 🛡️ Role-based authorization (ADMIN, RECRUITER, TALENT, USER)

---

## 🛠️ Technologies Used

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* MySQL
* Lombok
* JWT (io.jsonwebtoken)
* Spring Boot Actuator
* Spring Boot Test

---

## 🔐 Role System

| Role      | Permissions                                |
| --------- | ------------------------------------------ |
| ADMIN     | Manage users, job posts, and applications  |
| RECRUITER | Create/update job posts, view applications |
| TALENT    | Apply to jobs, view own applications       |
| USER      | Create and manage talent profile           |

---

# 📡 API Endpoints

## 🔑 Authentication

### `POST /api/auth`

Register a new user (Signup)

### `POST /api/auth/signin`

Login (Returns JWT token)

### `POST /api/auth/logout`

Logout user

---

## 👤 User Management (`/api/users`)

| Method | Endpoint                    | Authorization  | Description        |
| ------ | --------------------------- | -------------- | ------------------ |
| GET    | `/`                         | ADMIN          | Get all users      |
| GET    | `/getByEmail/{email}`       | ADMIN          | Get user by email  |
| GET    | `/activeUsers`              | ADMIN          | Get active users   |
| GET    | `/inActiveUsers`            | ADMIN          | Get inactive users |
| DELETE | `/{id}`                     | ADMIN          | Delete user        |
| PATCH  | `/updateUser/{userId}`      | ADMIN or owner | Update user        |
| PATCH  | `/changeEmailById/{userId}` | ADMIN or owner | Change email       |
| GET    | `/me`                       | Authenticated  | Get current user   |

---

## 🧑 Talent Profile (`/api/talentProfile`)

| Method | Endpoint | Authorization | Description     |
| ------ | -------- | ------------- | --------------- |
| GET    | `/`      | USER          | Get own profile |
| POST   | `/`      | USER          | Create profile  |
| PATCH  | `/`      | USER          | Update profile  |

---

## 🏢 Job Post (`/api/jobPost`)

| Method | Endpoint     | Authorization     | Description        |
| ------ | ------------ | ----------------- | ------------------ |
| POST   | `/`          | RECRUITER         | Create job post    |
| GET    | `/`          | Public            | Get all job posts  |
| GET    | `/byId/{id}` | Public            | Get job post by ID |
| PATCH  | `/{id}`      | RECRUITER         | Update job post    |
| DELETE | `/{id}`      | RECRUITER / ADMIN | Delete job post    |

---

## 📩 Job Applications (`/api/applications`)

| Method | Endpoint                       | Authorization     | Description                |
| ------ | ------------------------------ | ----------------- | -------------------------- |
| POST   | `/job-posts/{jobPostId}/apply` | TALENT            | Apply to a job             |
| GET    | `/job-posts/{jobPostId}`       | RECRUITER / ADMIN | Get applications for a job |
| GET    | `/my`                          | TALENT            | Get my applications        |
| PATCH  | `/{applicationId}/status`      | RECRUITER / ADMIN | Update application status  |

---

# ⚙️ Installation

## 1️⃣ Clone the repository

```bash
git clone https://github.com/your-username/talentworld-backend.git
cd talentworld-backend
```

## 2️⃣ Create MySQL database

```sql
CREATE DATABASE talentworld;
```

## 3️⃣ Configure `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/talentworld
spring.datasource.username=root
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## 4️⃣ Run the project

```bash
mvn clean install
mvn spring-boot:run
```

Application will run at:

```
http://localhost:8081
```

---

# 🔐 JWT Authentication

After login, include the returned token in your requests:

```
Authorization: Bearer <your_token>
```

---





The project follows a **Layered Architecture** approach.

---

# 📌 Features

* JWT-based authentication
* Role-based authorization
* Global exception handling
* Request validation (@Valid)
* DTO mapping
* Clean layered architecture
* RESTful API design

---


