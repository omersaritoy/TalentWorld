# 🚀 TalentWorld Backend

**TalentWorld Backend Application** is a scalable and production-ready RESTful API designed to manage job postings and application processes.

This project provides a complete backend solution for a talent-job matching platform, including authentication, authorization, caching, rate limiting, and testing.

---

## ✨ Features

- 👤 User management  
- 🧑 Talent profile management  
- 🏢 Job posting system  
- 📩 Job application system  
- 🔐 JWT-based authentication  
- 🛡️ Role-based authorization (ADMIN, RECRUITER, TALENT, USER)  
- ⚡ Redis caching (performance optimization)  
- 🚦 Rate limiting (request control & protection)  
- 📄 Pagination support (scalable data handling)  
- 🧪 Unit & Integration Testing  
- 📜 Centralized logging  
- ❗ Global exception handling  
- ✅ Request validation (`@Valid`)  
- 🔄 DTO-based architecture  
- Mail Sender
---

## 🛠️ Technologies Used

- Java 21  
- Spring Boot  
- Spring Security  
- Spring Data JPA  
- MySQL  
- Redis  
- Lombok  
- JWT (io.jsonwebtoken)  
- Spring Boot Actuator  
- JUnit & Mockito  
- Spring Boot Test  
- SLF4J / Logback  

---

## 🏗️ Architecture

The project follows a **Layered Architecture**:

```
Controller → Service → Repository → Database
```

Additional components:

- Security Layer (JWT + Filters)  
- Cache Layer (Redis)  
- Rate Limiting Layer  
- Exception Handling Layer  

---

## 🔐 Role System

| Role      | Permissions                                |
|-----------|--------------------------------------------|
| ADMIN     | Manage users, job posts, applications      |
| RECRUITER | Manage job posts, view applications        |
| TALENT    | Apply to jobs, view own applications       |
| USER      | Manage own profile                         |

---

# 📡 API Endpoints

## 🔑 Authentication

### `POST /api/auth`
Register a new user  

### `POST /api/auth/signin`
Login (returns JWT)  

### `POST /api/auth/logout`
Logout user  

---

## 👤 User Management (`/api/users`)

| Method | Endpoint                    | Authorization  | Description        |
|--------|---------------------------|---------------|--------------------|
| GET    | `/`                       | ADMIN         | Get all users      |
| GET    | `/getByEmail/{email}`     | ADMIN         | Get user by email  |
| GET    | `/activeUsers`            | ADMIN         | Get active users   |
| GET    | `/inActiveUsers`          | ADMIN         | Get inactive users |
| DELETE | `/{id}`                   | ADMIN         | Delete user        |
| PATCH  | `/updateUser/{userId}`    | ADMIN / Owner | Update user        |
| PATCH  | `/changeEmailById/{userId}` | ADMIN / Owner | Change email    |
| GET    | `/me`                     | Authenticated | Get current user   |

---

## 🧑 Talent Profile (`/api/talentProfile`)

| Method | Endpoint | Authorization | Description     |
|--------|---------|--------------|-----------------|
| GET    | `/`     | USER         | Get own profile |
| POST   | `/`     | USER         | Create profile  |
| PATCH  | `/`     | USER         | Update profile  |

---

## 🏢 Job Post (`/api/jobPost`)

| Method | Endpoint     | Authorization     | Description        |
|--------|--------------|------------------|--------------------|
| POST   | `/`          | RECRUITER        | Create job post    |
| GET    | `/`          | Public           | Get all job posts  |
| GET    | `/byId/{id}` | Public           | Get job post by ID |
| PATCH  | `/{id}`      | RECRUITER        | Update job post    |
| DELETE | `/{id}`      | RECRUITER / ADMIN| Delete job post    |

---

## 📩 Job Applications (`/api/applications`)

| Method | Endpoint                       | Authorization     | Description                |
|--------|--------------------------------|------------------|----------------------------|
| POST   | `/job-posts/{jobPostId}/apply` | TALENT           | Apply to job               |
| GET    | `/job-posts/{jobPostId}`       | RECRUITER / ADMIN| Get applications           |
| GET    | `/my`                          | TALENT           | Get my applications        |
| PATCH  | `/{applicationId}/status`      | RECRUITER / ADMIN| Update status              |

---

# ⚡ Pagination

All list endpoints support pagination:

```
GET /api/jobPost?page=0&size=10&sort=createdAt,desc
```

---

# 🚦 Rate Limiting

Rate limiting is applied to protect the API from excessive requests.

Example:

```
100 requests / minute per IP
```

(Customizable via configuration)

---

# ⚡ Caching (Redis)

Redis is used for:

- Frequently accessed data  
- Reducing database load  
- Improving response time  

---

# 🧪 Testing

### ✅ Unit Tests
- Service layer tested with **JUnit & Mockito**

### ✅ Integration Tests
- Full flow tested with **Spring Boot Test**

---

# 📜 Logging

Centralized logging using:

- SLF4J  
- Logback  

Includes:

- Request/Response logs  
- Error logs  
- Security logs  

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
```

---

## 4️⃣ Run the project

```bash
mvn clean install
mvn spring-boot:run
```

App runs at:

```
http://localhost:8081
```

---

# 🔐 JWT Authentication

Include JWT token in requests:

```
Authorization: Bearer <token>
```

---
