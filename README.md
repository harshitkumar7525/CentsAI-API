# CentsAI API

A Spring Boot REST API for managing personal finances with AI-powered transaction tracking. This application allows users to register, authenticate, and manage their expenses with intelligent categorization and analysis.

## 📋 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
  - [Authentication](#authentication)
  - [Transactions](#transactions)
  - [AI Features](#ai-features)
- [Request/Response Examples](#requestresponse-examples)
- [Error Handling](#error-handling)
- [Security](#security)
- [Configuration](#configuration)

## 🎯 Overview

CentsAI API is a comprehensive financial management backend that provides:

- **User Authentication**: Secure registration and login with JWT tokens
- **Transaction Management**: Create, read, update, and delete expense transactions
- **AI-Powered Analysis**: Natural language processing for expense categorization
- **User Authorization**: Role-based access control for transaction operations
- **PostgreSQL Database**: Persistent data storage with JPA/Hibernate ORM

## 🛠 Tech Stack

- **Framework**: Spring Boot 4.0.0
- **Language**: Java 21
- **Build Tool**: Gradle
- **Database**: PostgreSQL (Neon)
- **Authentication**: JWT (JSON Web Tokens)
- **ORM**: Spring Data JPA with Hibernate
- **Validation**: Jakarta Bean Validation
- **Security**: Spring Security
- **Logging**: SLF4J with Logback
- **Reactive**: Project Reactor with WebFlux
- **Additional**: Lombok, Spring WebClient

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Gradle 8.x+
- PostgreSQL database
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/harshitkumar7525/CentsAI-API.git
   cd centsaiapi
   ```

2. **Configure environment variables**
   
   Create or update `application.properties` in `src/main/resources/`:
   
   ```properties
   spring.application.name=centsaiapi
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.datasource.url=jdbc:postgresql://your-host:5432/your-database?sslmode=require
   spring.datasource.username=your-username
   spring.datasource.password=your-password
   jwt.secret=your-secret-key-min-32-chars
   jwt.expiration-in-ms=604800000
   fastapi.url=https://your-ai-service-url/generate
   frontend.url=https://your-frontend-url
   ```

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

The API will be available at `http://localhost:8080`

## 📚 API Endpoints

### Authentication

#### 1. Register User

**Endpoint**: `POST /api/v1/users/register`

**Description**: Create a new user account

**Request Headers**:
```
Content-Type: application/json
```

**Request Body**:
```json
{
  "email": "user@example.com",
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Request Validation Rules**:
- `email`: Must be a valid email format and not blank
- `username`: Cannot be blank
- `password`: Minimum 6 characters, cannot be blank

**Response** (201 Created):
```json
{
  "user_id": 1,
  "username": "john_doe",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Error Response** (400 Bad Request):
```json
{
  "message": "Validation error",
  "details": {
    "email": "Email is required",
    "password": "Password must be at least 6 characters"
  }
}
```

---

#### 2. Login User

**Endpoint**: `POST /api/v1/users/login`

**Description**: Authenticate and obtain JWT token

**Request Headers**:
```
Content-Type: application/json
```

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Request Validation Rules**:
- `email`: Must be a valid email format and not blank
- `password`: Cannot be blank

**Response** (200 OK):
```json
{
  "user_id": 1,
  "username": "john_doe",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Error Response** (401 Unauthorized):
```json
{
  "message": "Invalid email or password"
}
```

---

### Transactions

#### 3. Add Transaction (Manual)

**Endpoint**: `POST /api/v1/users/{userId}/transaction`

**Description**: Create a new expense transaction manually

**Path Parameters**:
- `userId` (Long): The ID of the user (must match authenticated user)

**Request Headers**:
```
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body**:
```json
{
  "amount": 50.00,
  "category": "Food",
  "date": "2025-12-05"
}
```

**Request Validation Rules**:
- `amount`: Required, must be a positive number
- `category`: Required, cannot be blank
- `date`: Required, must be a valid date

**Response** (201 Created):
```json
{
  "userId": 1,
  "expenses": [
    {
      "id": 1,
      "amount": 50.00,
      "transactionDate": "2025-12-05",
      "category": "Food"
    }
  ]
}
```

**Error Response** (403 Forbidden):
```json
{
  "message": "User is not authorized to add transaction for this user"
}
```

**Error Response** (401 Unauthorized):
```json
{
  "message": "Unauthorized - Invalid or missing token"
}
```

---

#### 4. Add Transaction via AI

**Endpoint**: `POST /api/v1/users/ai/{userId}/transaction`

**Description**: Create expense transaction using natural language processing

**Path Parameters**:
- `userId` (Long): The ID of the user (must match authenticated user)

**Request Headers**:
```
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body**:
```json
{
  "prompt": "I spent $45 on groceries today"
}
```

**Response** (201 Created):
```json
{
  "userId": 1,
  "expenses": [
    {
      "id": 2,
      "amount": 45.00,
      "transactionDate": "2025-12-05",
      "category": "Groceries"
    }
  ]
}
```

**Error Response** (400 Bad Request):
```json
{
  "message": "Failed to parse transaction from prompt"
}
```

**Error Response** (503 Service Unavailable):
```json
{
  "message": "AI service is temporarily unavailable"
}
```

---

#### 5. Get All Transactions

**Endpoint**: `GET /api/v1/users/{userId}/transactions`

**Description**: Retrieve all transactions for a specific user

**Path Parameters**:
- `userId` (Long): The ID of the user (must match authenticated user)

**Request Headers**:
```
Authorization: Bearer {token}
```

**Response** (200 OK):
```json
{
  "userId": 1,
  "allExpenses": [
    {
      "id": 1,
      "amount": 50.00,
      "transactionDate": "2025-12-05",
      "category": "Food"
    },
    {
      "id": 2,
      "amount": 100.00,
      "transactionDate": "2025-12-04",
      "category": "Transportation"
    },
    {
      "id": 3,
      "amount": 30.00,
      "transactionDate": "2025-12-03",
      "category": "Entertainment"
    }
  ]
}
```

**Error Response** (404 Not Found):
```json
{
  "message": "User not found"
}
```

---

#### 6. Update Transaction

**Endpoint**: `PATCH /api/v1/users/{userId}/transaction/{transactionId}`

**Description**: Update an existing transaction

**Path Parameters**:
- `userId` (Long): The ID of the user (must match authenticated user)
- `transactionId` (Long): The ID of the transaction to update

**Request Headers**:
```
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body**:
```json
{
  "amount": 55.00,
  "category": "Groceries",
  "date": "2025-12-05"
}
```

**Response** (200 OK):
```json
{
  "userId": 1,
  "expenses": [
    {
      "id": 1,
      "amount": 55.00,
      "transactionDate": "2025-12-05",
      "category": "Groceries"
    }
  ]
}
```

**Error Response** (403 Forbidden):
```json
{
  "message": "User is not authorized to update transaction for this user"
}
```

**Error Response** (404 Not Found):
```json
{
  "message": "Transaction not found"
}
```

---

#### 7. Delete Transaction

**Endpoint**: `DELETE /api/v1/users/{userId}/transaction/{transactionId}`

**Description**: Delete a specific transaction

**Path Parameters**:
- `userId` (Long): The ID of the user (must match authenticated user)
- `transactionId` (Long): The ID of the transaction to delete

**Request Headers**:
```
Authorization: Bearer {token}
```

**Response** (204 No Content):
```
(Empty body)
```

**Error Response** (403 Forbidden):
```json
{
  "message": "User is not authorized to delete transaction for this user"
}
```

**Error Response** (404 Not Found):
```json
{
  "message": "Transaction not found"
}
```

---

## 📋 Request/Response Examples

### Complete Flow Example

#### Step 1: Register a new user

```bash
curl -X POST http://localhost:8080/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "username": "alice_smith",
    "password": "MySecurePass123"
  }'
```

**Response**:
```json
{
  "user_id": 5,
  "username": "alice_smith",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1IiwiaWF0IjoxNzAwNjc2ODAwLCJleHAiOjE3MDEyODE2MDB9.abc123def456"
}
```

---

#### Step 2: Login with existing credentials

```bash
curl -X POST http://localhost:8080/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "MySecurePass123"
  }'
```

**Response**:
```json
{
  "user_id": 5,
  "username": "alice_smith",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1IiwiaWF0IjoxNzAwNjc2ODAwLCJleHAiOjE3MDEyODE2MDB9.abc123def456"
}
```

---

#### Step 3: Add a manual transaction

```bash
curl -X POST http://localhost:8080/api/v1/users/5/transaction \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1IiwiaWF0IjoxNzAwNjc2ODAwLCJleHAiOjE3MDEyODE2MDB9.abc123def456" \
  -d '{
    "amount": 125.50,
    "category": "Groceries",
    "date": "2025-12-05"
  }'
```

**Response**:
```json
{
  "userId": 5,
  "expenses": [
    {
      "id": 1,
      "amount": 125.50,
      "transactionDate": "2025-12-05",
      "category": "Groceries"
    }
  ]
}
```

---

#### Step 4: Add transaction via AI

```bash
curl -X POST http://localhost:8080/api/v1/users/5/ai/transaction \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1IiwiaWF0IjoxNzAwNjc2ODAwLCJleHAiOjE3MDEyODE2MDB9.abc123def456" \
  -d '{
    "prompt": "I paid $89.99 for a movie ticket and popcorn"
  }'
```

**Response**:
```json
{
  "userId": 5,
  "expenses": [
    {
      "id": 2,
      "amount": 89.99,
      "transactionDate": "2025-12-05",
      "category": "Entertainment"
    }
  ]
}
```

---

#### Step 5: Retrieve all transactions

```bash
curl -X GET http://localhost:8080/api/v1/users/5/transactions \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1IiwiaWF0IjoxNzAwNjc2ODAwLCJleHAiOjE3MDEyODE2MDB9.abc123def456"
```

**Response**:
```json
{
  "userId": 5,
  "allExpenses": [
    {
      "id": 1,
      "amount": 125.50,
      "transactionDate": "2025-12-05",
      "category": "Groceries"
    },
    {
      "id": 2,
      "amount": 89.99,
      "transactionDate": "2025-12-05",
      "category": "Entertainment"
    }
  ]
}
```

---

#### Step 6: Update a transaction

```bash
curl -X PATCH http://localhost:8080/api/v1/users/5/transaction/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1IiwiaWF0IjoxNzAwNjc2ODAwLCJleHAiOjE3MDEyODE2MDB9.abc123def456" \
  -d '{
    "amount": 130.00,
    "category": "Groceries",
    "date": "2025-12-05"
  }'
```

**Response**:
```json
{
  "userId": 5,
  "expenses": [
    {
      "id": 1,
      "amount": 130.00,
      "transactionDate": "2025-12-05",
      "category": "Groceries"
    }
  ]
}
```

---

#### Step 7: Delete a transaction

```bash
curl -X DELETE http://localhost:8080/api/v1/users/5/transaction/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1IiwiaWF0IjoxNzAwNjc2ODAwLCJleHAiOjE3MDEyODE2MDB9.abc123def456"
```

**Response**: 204 No Content

---

## ⚠️ Error Handling

The API returns standardized error responses with appropriate HTTP status codes:

### Common HTTP Status Codes

| Status Code | Description |
|------------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 204 | No Content - Request successful, no content to return |
| 400 | Bad Request - Invalid request parameters |
| 401 | Unauthorized - Missing or invalid authentication token |
| 403 | Forbidden - Authenticated user lacks permission |
| 404 | Not Found - Requested resource not found |
| 500 | Internal Server Error - Server error occurred |
| 503 | Service Unavailable - External service unavailable |

### Error Response Format

```json
{
  "message": "Error description",
  "timestamp": "2025-12-05T10:30:00Z",
  "status": 400
}
```

---

## 🔒 Security

### Authentication

- **JWT Tokens**: Used for stateless authentication
- **Token Expiration**: 7 days (configurable via `jwt.expiration-in-ms`)
- **Bearer Token**: Include in `Authorization` header as `Bearer {token}`

### Authorization

- **User Isolation**: Users can only access their own data
- **Path Variable Validation**: User ID in path must match authenticated user
- **Forbidden Response**: 403 status returned for unauthorized access

### Best Practices

1. **Store tokens securely** in local storage or session storage
2. **Include token in every request** that requires authentication
3. **Handle token expiration** and refresh before requests
4. **Never expose tokens** in version control or logs
5. **Use HTTPS** in production to encrypt token transmission

---

## ⚙️ Configuration

### Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `spring.datasource.url` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/centsai` |
| `spring.datasource.username` | Database username | `postgres` |
| `spring.datasource.password` | Database password | `your-password` |
| `jwt.secret` | JWT signing secret (min 32 chars) | `your-secret-key-min-32-chars` |
| `jwt.expiration-in-ms` | Token expiration time in ms | `604800000` (7 days) |
| `fastapi.url` | AI service URL | `https://your-ai-service.com/generate` |
| `frontend.url` | Frontend application URL | `https://your-frontend.com` |

### Database Schema

The application automatically creates the following tables:

- **users**: User account information
- **expenses**: Transaction/expense records
- **_prisma_migrations**: Migration history (if using Prisma)

### Logging

Logging is configured via SLF4J and Logback. Adjust in `application.properties`:

```properties
logging.level.root=INFO
logging.level.in.harshitkumar.centsaiapi=DEBUG
```

---

## 📦 Dependencies

### Core Dependencies
- spring-boot-starter-security
- spring-boot-starter-data-jpa
- spring-boot-starter-webmvc
- spring-boot-starter-webflux
- spring-boot-starter-validation

### Authentication
- jjwt-api, jjwt-impl, jjwt-jackson (JWT)

### Database
- postgresql (PostgreSQL driver)

### Development
- lombok (Annotation processing)
- spring-boot-devtools (Hot reload)

---

## 🔗 Related Services

- **AI Service**: Gemini-powered expense categorization
  - URL: `https://centsai-gemini-microservice.onrender.com/generate`
- **Frontend**: React-based user interface
  - URL: `https://cents-ai.vercel.app`

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👨‍💻 Contributors

- **Harshit Kumar** - Main Developer
- GitHub: [@harshitkumar7525](https://github.com/harshitkumar7525)

---

## 📧 Support

For issues, questions, or suggestions, please create an issue in the GitHub repository or contact the development team.

---

## 🗺️ Project Structure

```
centsaiapi/
├── src/
│   ├── main/
│   │   ├── java/in/harshitkumar/centsaiapi/
│   │   │   ├── config/           # Security and Spring configurations
│   │   │   ├── controller/       # REST API endpoints
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── exception/        # Custom exceptions
│   │   │   ├── models/           # Entity models
│   │   │   ├── repository/       # Data access layer
│   │   │   ├── security/         # Security utilities
│   │   │   ├── service/          # Business logic layer
│   │   │   └── utils/            # Utility classes
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/...
├── build.gradle
├── gradlew
└── README.md
```

---

**Last Updated**: December 5, 2025
**Version**: 0.0.1-SNAPSHOT
