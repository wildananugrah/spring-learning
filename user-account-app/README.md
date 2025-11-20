# Banking Application - Spring Boot

A production-ready RESTful banking application built with Spring Boot 3.5, featuring clean architecture, JWT authentication, and comprehensive transaction management.

## 🌟 Features

- **User Management**
  - User registration with validation
  - JWT-based authentication
  - Secure password hashing with BCrypt

- **Account Management**
  - Create multiple bank accounts
  - View account list
  - Get account details
  - Delete accounts

- **Transaction Operations**
  - Deposit money
  - Withdraw money
  - Transfer between accounts
  - Transaction history with:
    - Pagination
    - Sorting
    - Date range filtering

- **Security**
  - JWT token authentication
  - Stateless session management
  - Protected endpoints
  - Password encryption

- **Code Quality**
  - Clean architecture (3-layer design)
  - Global exception handling with `@RestControllerAdvice`
  - Input validation with Bean Validation
  - Lombok for cleaner code with `@SneakyThrows`
  - Transaction management

## 🏗️ Architecture

The application follows a clean, layered architecture:

```
┌─────────────────────────────────────┐
│         Route Layer                 │  HTTP Requests/Responses
│      (Controllers in /routes)       │  Authentication, Validation
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Logic Layer                 │  Business Logic
│      (Services in /logics)          │  Transactions, Coordination
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│        Service Layer                │  Data Access
│    (Repositories in /services)      │  Database Operations
└─────────────────────────────────────┘
```

### Layer Responsibilities

**1. Route Layer** (`/routes`)
- Handle HTTP requests and responses
- Validate input data
- Extract authentication information
- Return formatted API responses

**2. Logic Layer** (`/logics`)
- Implement business rules
- Process transactions
- Coordinate between layers
- Handle data transformations

**3. Service Layer** (`/services`)
- JPA repositories for data access
- Database operations
- Query execution

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL
- IDE (IntelliJ IDEA, VS Code, etc.)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd user-account-app
   ```

2. **Set up PostgreSQL database**
   ```sql
   CREATE DATABASE bank_app;
   ```

3. **Configure database** (update `application.properties` if needed)
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:6435/bank_app
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

4. **Build the project**
   ```bash
   ./mvnw clean install
   ```

5. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080`

## 📚 Documentation

- **[SETUP_GUIDE.md](SETUP_GUIDE.md)** - Detailed setup instructions
- **[API_DOCUMENTATION.md](API_DOCUMENTATION.md)** - Complete API reference
- **[MEDIUM_ARTICLE.md](MEDIUM_ARTICLE.md)** - Step-by-step tutorial
- **[api-tests.http](api-tests.http)** - API testing file

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

### Accounts (Protected)
- `POST /api/accounts` - Create account
- `GET /api/accounts` - Get all accounts
- `GET /api/accounts/{accountNumber}` - Get account details
- `DELETE /api/accounts/{accountNumber}` - Delete account

### Transactions (Protected)
- `POST /api/transactions/deposit` - Deposit money
- `POST /api/transactions/withdrawal` - Withdraw money
- `POST /api/transactions/transfer` - Transfer money
- `GET /api/transactions` - Get transaction history

## 📝 Example Usage

### 1. Register a User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "fullName": "John Doe"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "userId": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe"
  }
}
```

### 3. Create an Account

```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "accountName": "Savings Account",
    "initialBalance": 1000.00
  }'
```

### 4. Make a Deposit

```bash
curl -X POST http://localhost:8080/api/transactions/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "accountNumber": "0123456789",
    "amount": 500.00,
    "description": "Salary deposit"
  }'
```

### 5. Get Transaction History

```bash
curl -X GET "http://localhost:8080/api/transactions?accountNumber=0123456789&page=0&size=10&sortBy=createdAt&sortDirection=desc" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security + JWT
- **Validation**: Jakarta Validation (Bean Validation)
- **Code Generation**: Lombok

### Key Dependencies

```xml
<!-- Core -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Database -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<!-- Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>

<!-- Utilities -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```

## 📂 Project Structure

```
src/main/java/com/user/account/app/
├── config/
│   ├── JwtUtil.java                    # JWT token utilities
│   ├── JwtAuthenticationFilter.java    # JWT filter
│   └── SecurityConfig.java             # Security configuration
├── dto/
│   ├── RegisterRequest.java            # Registration DTO
│   ├── LoginRequest.java               # Login DTO
│   ├── AuthResponse.java               # Auth response DTO
│   ├── CreateAccountRequest.java       # Create account DTO
│   ├── AccountResponse.java            # Account response DTO
│   ├── TransactionRequest.java         # Transaction DTO
│   ├── TransactionResponse.java        # Transaction response DTO
│   └── ApiResponse.java                # Generic API response
├── entities/
│   ├── User.java                       # User entity
│   ├── Account.java                    # Account entity
│   └── Transaction.java                # Transaction entity
├── exceptions/
│   ├── ResourceNotFoundException.java  # Not found exception
│   ├── DuplicateResourceException.java # Duplicate exception
│   ├── InsufficientBalanceException.java # Balance exception
│   ├── InvalidCredentialsException.java # Auth exception
│   └── GlobalExceptionHandler.java     # Global exception handler
├── logics/
│   ├── AuthLogic.java                  # Authentication logic
│   ├── AccountLogic.java               # Account logic
│   └── TransactionLogic.java           # Transaction logic
├── routes/
│   ├── AuthController.java             # Auth endpoints
│   ├── AccountController.java          # Account endpoints
│   └── TransactionController.java      # Transaction endpoints
├── services/
│   ├── UserRepository.java             # User repository
│   ├── AccountRepository.java          # Account repository
│   └── TransactionRepository.java      # Transaction repository
└── UserAccountApplication.java         # Main application class
```

## 🧪 Testing

### Run All Tests
```bash
./mvnw test
```

### Run Specific Test
```bash
./mvnw test -Dtest=UserAccountApplicationTests
```

### Using HTTP File
Open `api-tests.http` in VS Code (with REST Client extension) or IntelliJ IDEA and execute requests.

## 🔒 Security Features

- **Password Hashing**: BCrypt with Spring Security
- **JWT Tokens**: Stateless authentication
- **Protected Endpoints**: All endpoints except `/api/auth/**` require authentication
- **Input Validation**: Bean Validation on all requests
- **Exception Handling**: Secure error messages without sensitive data

## 🎯 Design Patterns & Principles

- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: Separate API contracts from entities
- **Builder Pattern**: Clean object construction with Lombok
- **Dependency Injection**: Loose coupling
- **Single Responsibility**: Each class has one job
- **RESTful Design**: Standard HTTP methods and status codes

## 🚦 HTTP Status Codes

- `200 OK` - Request successful
- `201 Created` - Resource created
- `400 Bad Request` - Validation error or bad input
- `401 Unauthorized` - Authentication required
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate resource
- `500 Internal Server Error` - Server error

## 🔧 Configuration

### Database Configuration
Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:6435/bank_app
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=your-secret-key
jwt.expiration=86400000
```

## 🐛 Troubleshooting

See [SETUP_GUIDE.md](SETUP_GUIDE.md#common-issues--solutions) for common issues and solutions.

## 📈 Future Enhancements

- [ ] User roles and permissions (ADMIN, USER)
- [ ] Account statement generation (PDF)
- [ ] Email notifications for transactions
- [ ] Transaction limits and rules
- [ ] Audit logging
- [ ] API rate limiting
- [ ] Swagger/OpenAPI documentation
- [ ] Docker containerization
- [ ] Unit and integration tests
- [ ] CI/CD pipeline

## 📄 License

This project is created for educational purposes.

## 👥 Contributing

This is a tutorial project. Feel free to fork and modify for your learning purposes.

## 📞 Support

For questions or issues:
1. Check the [API Documentation](API_DOCUMENTATION.md)
2. Review the [Setup Guide](SETUP_GUIDE.md)
3. Read the [Tutorial Article](MEDIUM_ARTICLE.md)

## ⭐ Acknowledgments

Built with:
- Spring Boot
- PostgreSQL
- JWT
- Lombok

---

**Made with ☕ and Spring Boot**
