
# ⚙️ Reevo - Backend (Spring Boot)

Core backend API for Reevo, handling business logic, database operations, and real-time communication.

---

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.2
- **Language**: Java 17
- **Modularization**: Spring Modulith (Modular Monolith)
- **Security**: Spring Security + JWT
- **ORM**: Spring Data JPA
- **Database**: PostgreSQL 16
- **Real-Time**: WebSocket (STOMP)
- **Media Storage**: Cloudinary
- **API Docs**: SpringDoc OpenAPI (Swagger UI)

---

## 📋 Prerequisites

- Java Development Kit (JDK): `>= 17`
- Apache Maven: `>= 3.9`
- PostgreSQL: `>= 15`
- Cloudinary Account (for media storage)

---

## 🚀 Installation & Setup

### 1. Create Database
Create a new PostgreSQL database for the project:
```sql
CREATE DATABASE reevo_db;
```

### 2. Configure Environment Variables
Create an `application-dev.yml` file in `src/main/resources/` (see `application.example.yml` for reference):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/reevo_db
    username: your_db_username
    password: your_db_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

# JWT Configuration
app:
  jwt:
    secret-key: your-super-secret-key-at-least-256-bits-long
    access-token-expiration: 900000  # 15 minutes
    refresh-token-expiration: 604800000  # 7 days

# Cloudinary Configuration
cloudinary:
  cloud-name: your-cloud-name
  api-key: your-api-key
  api-secret: your-api-secret

# Recommendation Service URL
recommendation-service:
  url: http://localhost:8000
```

### 3. Build & Run the Backend
```bash
# Navigate to backend directory
cd reevo-backend/Reevo-backend

# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

### 4. View API Documentation
Open your browser and go to:
```
http://localhost:8080/swagger-ui.html
```
Here you can view and test all API endpoints.

---

## 📁 Project Structure

```
src/main/java/com/kynn/reevo_backend/
├── common/                 # Shared Infrastructure
│   ├── config/             # WebSocketConfig, SecurityConfig
│   ├── exception/          # Global Exception Handler
│   └── utils/              # Utility Classes
├── user/                   # User Management Module
├── video/                  # Video Management Module
├── interaction/            # Interaction Module (Like/Comment)
├── feed/                   # Home Feed Module (calls recommendation service)
├── watchtogether/          # Watch Together Module
└── tag/                    # Tag & User Tag Affinity Module
```

