# CentsAI API

CentsAI API is a Spring Boot REST API for personal finance tracking with JWT authentication and AI-assisted transaction capture. The current codebase uses MongoDB for persistence and stores users and expenses as Mongo collections.

## Overview

The API provides:

- User registration and login with JWT tokens
- Manual expense creation and management
- AI-assisted expense parsing from natural language prompts
- Per-user access control on all protected endpoints
- MongoDB persistence for users and expenses

## Tech Stack

- Spring Boot 4.0.0
- Java 21
- Gradle
- MongoDB
- Spring Security
- Spring Data MongoDB
- JWT (JJWT)
- Spring WebMVC and WebFlux
- Jakarta Validation
- Lombok

## Getting Started

### Prerequisites

- Java 21+
- Gradle 8.x+
- MongoDB database
- Git

### Configuration

Create environment variables for the application:

```properties
SPRING_APPLICATION_NAME=centsaiapi
MONGO_URI=mongodb+srv://<username>:<password>@<host>/<database>?retryWrites=true&w=majority
JWT_SECRET=your-secret-key-min-32-chars
JWT_EXPIRATION=604800000
MICROSERVICE_URI=https://your-ai-service-url/generate
FRONTEND_URL=https://your-frontend-url
PORT=8080
```

The app reads these values from `src/main/resources/application.properties`.

### Run the App

```bash
./gradlew build
./gradlew bootRun
```

The API will be available at `http://localhost:8080` by default.

## API Endpoints

### Authentication

#### Register User

`POST /api/v1/users/register`

Request body:

```json
{
  "email": "user@example.com",
  "username": "john_doe",
  "password": "securePassword123"
}
```

Validation rules:

- `email` must be present and valid
- `username` must not be blank
- `password` must not be blank and must be at least 6 characters

Response: `201 Created`

```json
{
  "user_id": "67d1c2f4a1b2c3d4e5f67890",
  "username": "john_doe",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

Common errors:

- `400 Bad Request` for validation failures
- `409 Conflict` when the email already exists

#### Login User

`POST /api/v1/users/login`

Request body:

```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

Response: `200 OK`

```json
{
  "user_id": "67d1c2f4a1b2c3d4e5f67890",
  "username": "john_doe",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

Common errors:

- `400 Bad Request` for validation failures
- `404 Not Found` for invalid credentials

### Transactions

All transaction endpoints require `Authorization: Bearer <token>`.

#### Add Transaction Manually

`POST /api/v1/users/{userId}/transaction`

Path parameter:

- `userId` is the Mongo user id string and must match the authenticated user

Request body:

```json
{
  "amount": 50.0,
  "category": "Food",
  "date": "2025-12-05"
}
```

Notes:

- `amount` must be greater than 0
- `category` is optional in the current service layer
- `date` is optional and defaults to today when omitted

Response: `200 OK`

```json
{
  "userId": "67d1c2f4a1b2c3d4e5f67890",
  "expenses": [
    {
      "id": "67d1d0e7a1b2c3d4e5f67891",
      "amount": 50.0,
      "transactionDate": "2025-12-05",
      "category": "Food"
    }
  ]
}
```

Common errors:

- `400 Bad Request` when amount is missing or not positive
- `403 Forbidden` when the path user id does not match the authenticated user
- `404 Not Found` when the user does not exist

#### Add Transaction via AI

`POST /api/v1/users/ai/{userId}/transaction`

Request body:

```json
{
  "prompt": "I spent $45 on groceries today"
}
```

Response: `201 Created`

```json
{
  "userId": "67d1c2f4a1b2c3d4e5f67890",
  "expenses": [
    {
      "id": "67d1d0e7a1b2c3d4e5f67892",
      "amount": 45.0,
      "transactionDate": "2025-12-05",
      "category": "Groceries"
    }
  ]
}
```

Common errors:

- `400 Bad Request` when the AI service returns no valid expense data
- `403 Forbidden` when the path user id does not match the authenticated user
- `404 Not Found` when the user does not exist
- `503 Service Unavailable` when the AI microservice fails

#### Get All Transactions

`GET /api/v1/users/{userId}/transactions`

Response: `200 OK`

```json
{
  "userId": "67d1c2f4a1b2c3d4e5f67890",
  "allExpenses": [
    {
      "id": "67d1d0e7a1b2c3d4e5f67891",
      "amount": 50.0,
      "transactionDate": "2025-12-05",
      "category": "Food"
    }
  ]
}
```

Common errors:

- `403 Forbidden` when the path user id does not match the authenticated user
- `404 Not Found` when the user does not exist

#### Update Transaction

`PATCH /api/v1/users/{userId}/transaction/{transactionId}`

Request body:

```json
{
  "amount": 55.0,
  "category": "Groceries",
  "date": "2025-12-05"
}
```

Response: `200 OK`

```json
{
  "message": "Transaction updated successfully"
}
```

Common errors:

- `403 Forbidden` when the path user id does not match the authenticated user
- `401 Unauthorized` when the transaction belongs to another user
- `404 Not Found` when the transaction does not exist

#### Delete Transaction

`DELETE /api/v1/users/{userId}/transaction/{transactionId}`

Response: `200 OK`

```json
{
  "message": "Transaction deleted successfully"
}
```

Common errors:

- `403 Forbidden` when the path user id does not match the authenticated user
- `401 Unauthorized` when the transaction belongs to another user
- `404 Not Found` when the transaction does not exist

## Request and Response Notes

Validation errors are returned in this format:

```json
{
  "message": "Validation failed",
  "errors": {
    "email": "Email cannot be blank",
    "password": "Password must be at least 6 characters"
  }
}
```

The API uses string identifiers for MongoDB documents, so user ids and expense ids are returned as strings rather than numeric values.

## Security

- JWT tokens are used for stateless authentication
- Include the token in the `Authorization` header as `Bearer <token>`
- Users can only access resources tied to their own account
- The backend checks the path user id against the authenticated user before processing protected operations

## MongoDB Schema

The application stores data in these collections:

- `users`
- `expenses`

## Related Services

- AI microservice: configured through `MICROSERVICE_URI`
- Frontend application: configured through `FRONTEND_URL`

## Project Structure

```text
src/
├── main/
│   ├── java/in/harshitkumar/centsaiapi/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── models/
│   │   ├── repository/
│   │   ├── security/
│   │   ├── service/
│   │   └── utils/
│   └── resources/
│       └── application.properties
└── test/
    └── java/
```

## License

This project is licensed under the MIT License.