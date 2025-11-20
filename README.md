# Spring Learning Repository

A comprehensive collection of Spring Boot projects demonstrating core concepts, best practices, and real-world applications.

## Overview

This repository contains multiple Spring Boot projects, each focusing on different aspects of Spring development, from fundamental concepts to advanced patterns and performance optimization.

## Projects

### 1. [Spring Basic](spring-basic/)
**A comprehensive, hands-on tutorial covering fundamental Spring Boot concepts**

- **Topics Covered**:
  - Singleton Pattern (traditional vs Spring)
  - Spring Beans & IoC Container
  - Dependency Injection (Constructor, Setter, Field)
  - Spring Annotations (@Component, @Service, @Repository, @RestController, etc.)
  - Configuration Management (application.properties, @Value, @ConfigurationProperties)
  - Environment Variables & Security Best Practices

- **Technology Stack**: Spring Boot 3.x, Java 17
- **Port**: 9000
- **Key Learning**: Master Spring fundamentals with practical, production-ready examples

[View Documentation](spring-basic/README.md)

---

### 2. [User Account App](user-account-app/)
**Production-ready RESTful banking application with JWT authentication**

- **Features**:
  - User registration & JWT authentication
  - Account management (create, view, delete)
  - Transaction operations (deposit, withdraw, transfer)
  - Transaction history with pagination & filtering
  - Clean 3-layer architecture (Routes → Logic → Service)
  - Global exception handling
  - Input validation with Bean Validation

- **Technology Stack**: Spring Boot 3.5.7, PostgreSQL, Spring Security, JWT, Lombok
- **Port**: 8000
- **Architecture**: Clean layered architecture with separation of concerns

[View Documentation](user-account-app/README.md)

---

### 3. [Spring Gateway](spring-gateway/)
**Spring Cloud Gateway routing requests to the Banking Application**

- **Features**:
  - Backend service routing (Auth, Accounts, Transactions)
  - Custom Gateway Filter Factory
  - Global filters for logging
  - Request/Response header manipulation
  - CORS configuration
  - Actuator endpoints for monitoring
  - Example external API routes

- **Technology Stack**: Spring Cloud Gateway 2024.0.0, Spring Boot 3.4.1
- **Port**: 9090
- **Architecture**: API Gateway pattern with routing to port 8000 backend

[View Documentation](spring-gateway/README.md)

---

### 4. [Spring JPA N+1 Problem](spring-jpa-n-problem/)
**Demonstration of the N+1 query problem and performance optimization solutions**

- **Features**:
  - Complete N+1 query problem demonstration
  - Multiple optimization solutions (FETCH JOIN, EntityGraph)
  - Real performance benchmarks (50x faster with optimization)
  - Configurable dataset sizes (10, 100, 500, 1000 authors)
  - Visual performance comparison endpoints
  - Comprehensive documentation

- **Technology Stack**: Spring Boot 3.5.7, Spring Data JPA, Hibernate, PostgreSQL
- **Port**: 8001
- **Key Learning**: Understanding and solving JPA performance issues

**Performance Impact**:
- N+1 Problem: 101 queries, ~1000ms (with 100 authors)
- FETCH JOIN: 1 query, ~20ms (50x faster!)

[View Documentation](spring-jpa-n-problem/README.md)

---

### 5. [K6 Performance Testing](k6/)
**Comprehensive performance testing suite using k6**

- **Test Types**:
  - **Smoke Test**: Minimal load verification (1 user, 30s)
  - **Load Test**: Realistic user load (5-10 users, 4.5m)
  - **Stress Test**: Push beyond capacity (up to 50 users, 10m)
  - **Spike Test**: Sudden traffic bursts (5→50 users instantly)

- **Features**:
  - Pre-configured test users
  - Complete banking workflow testing
  - Custom metrics & thresholds
  - HTML report generation
  - CI/CD integration examples

- **Technology Stack**: k6, JavaScript
- **Target**: User Account App (port 8000)

[View Documentation](k6/README.md)

---

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL
- k6 (for performance testing)
- Docker (optional, for containerized deployment)

### Getting Started

#### 1. Clone the Repository
```bash
git clone <repository-url>
cd spring-learning
```

#### 2. Start with Spring Basics
```bash
cd spring-basic
./mvnw spring-boot:run
# Access at http://localhost:9000
```

#### 3. Run the Banking Application
```bash
# Setup PostgreSQL database
createdb bank_app

# Navigate and run
cd user-account-app
./mvnw spring-boot:run
# Access at http://localhost:8000
```

#### 4. Try the API Gateway
```bash
cd spring-gateway
./mvnw spring-boot:run
# Access at http://localhost:9090
```

#### 5. Explore N+1 Query Problem
```bash
# Setup database
createdb nproblem_db

cd spring-jpa-n-problem
./mvnw spring-boot:run
# Access at http://localhost:8001
```

#### 6. Run Performance Tests
```bash
# Install k6 (macOS)
brew install k6

# Run tests
cd k6
k6 run smoke-test.js
k6 run load-test.js
```

---

## Learning Path

### Beginner
1. **[Spring Basic](spring-basic/)** - Start here to learn Spring fundamentals
   - Singleton Pattern
   - Spring Beans
   - Annotations
   - Dependency Injection
   - Configuration

### Intermediate
2. **[User Account App](user-account-app/)** - Build a real application
   - RESTful API design
   - JWT authentication
   - Database integration
   - Transaction management
   - Clean architecture

3. **[Spring Gateway](spring-gateway/)** - Learn API Gateway patterns
   - Request routing
   - Custom filters
   - CORS configuration
   - Microservices architecture

### Advanced
4. **[Spring JPA N+1 Problem](spring-jpa-n-problem/)** - Performance optimization
   - Query optimization
   - FETCH JOIN strategies
   - EntityGraph usage
   - Performance benchmarking

5. **[K6 Performance Testing](k6/)** - Production readiness
   - Load testing
   - Stress testing
   - Performance metrics
   - Bottleneck identification

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        Client/Browser                       │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ├──> Spring Basic (Port 9000)
                   │    Learn Fundamentals
                   │
                   ├──> Spring Gateway (Port 9090)
                   │         │
                   │         └──> Routes to Backend
                   │
                   └──> User Account App (Port 8000)
                             │
                             ├──> PostgreSQL
                             │
                             └──> Tested by K6

    Spring JPA N+1 Problem (Port 8001) - Standalone Demo
                   │
                   └──> PostgreSQL
```

---

## Technology Stack

### Core
- **Java**: 17
- **Spring Boot**: 3.x (3.4.1 - 3.5.7)
- **Build Tool**: Maven

### Frameworks & Libraries
- **Spring Web**: RESTful APIs
- **Spring Data JPA**: Database access
- **Spring Security**: Authentication & authorization
- **Spring Cloud Gateway**: API routing
- **Hibernate**: ORM
- **Lombok**: Code generation
- **Jakarta Validation**: Input validation

### Security
- **JWT (JJWT)**: Token-based authentication
- **BCrypt**: Password hashing

### Database
- **PostgreSQL**: Primary database

### Testing & Monitoring
- **k6**: Performance testing
- **Spring Actuator**: Monitoring & health checks

---

## Project Structure

```
spring-learning/
├── spring-basic/                 # Spring Boot fundamentals
│   ├── src/main/java/com/springbasic/
│   │   ├── singleton/
│   │   ├── beans/
│   │   ├── di/
│   │   ├── annotations/
│   │   ├── config/
│   │   └── env/
│   └── README.md
│
├── user-account-app/            # Banking application
│   ├── src/main/java/com/user/account/app/
│   │   ├── config/              # Security, JWT
│   │   ├── dto/                 # Data transfer objects
│   │   ├── entities/            # JPA entities
│   │   ├── exceptions/          # Exception handling
│   │   ├── logics/              # Business logic
│   │   ├── routes/              # Controllers
│   │   └── services/            # Repositories
│   ├── API_DOCUMENTATION.md
│   └── README.md
│
├── spring-gateway/              # API Gateway
│   ├── src/main/java/com/user/account/app/
│   │   ├── config/              # Gateway configuration
│   │   └── filter/              # Custom filters
│   └── README.md
│
├── spring-jpa-n-problem/        # N+1 query demo
│   ├── src/main/java/com/user/account/app/
│   │   ├── entity/              # Author, Book entities
│   │   ├── repository/          # Optimized queries
│   │   ├── service/             # Benchmark service
│   │   └── controller/          # Demo endpoints
│   ├── PERFORMANCE_COMPARISON.md
│   └── README.md
│
├── k6/                          # Performance tests
│   ├── smoke-test.js
│   ├── load-test.js
│   ├── stress-test.js
│   ├── spike-test.js
│   └── README.md
│
├── database/                    # Database scripts
└── README.md                    # This file
```

---

## Common Endpoints

### Spring Basic (Port 9000)
```http
GET /api/singleton/compare
GET /api/beans/component
GET /api/di/compare
GET /api/config/properties
GET /api/env/all
```

### User Account App (Port 8000)
```http
POST /api/auth/register
POST /api/auth/login
POST /api/accounts
GET  /api/accounts
POST /api/transactions/deposit
POST /api/transactions/withdrawal
POST /api/transactions/transfer
GET  /api/transactions
```

### Spring Gateway (Port 9090)
```http
# Routes to User Account App
POST /api/auth/login
GET  /api/accounts
POST /api/transactions/deposit

# Gateway management
GET  /actuator/gateway/routes
```

### Spring JPA N+1 Problem (Port 8001)
```http
GET /api/demo/n-plus-one-problem
GET /api/demo/fetch-join-solution
GET /api/performance/compare?iterations=10
```

---

## Database Setup

### PostgreSQL Databases

```sql
-- Spring Basic (uses in-memory H2 or PostgreSQL)
CREATE DATABASE spring_basic;

-- User Account App
CREATE DATABASE bank_app;

-- Spring JPA N+1 Problem
CREATE DATABASE nproblem_db;
```

### Configuration
Update `application.properties` in each project:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/database_name
spring.datasource.username=postgres
spring.datasource.password=your_password
```

---

## Key Concepts Demonstrated

### Design Patterns
- **Singleton Pattern** (Spring Basic)
- **Repository Pattern** (User Account App)
- **DTO Pattern** (User Account App)
- **Builder Pattern** with Lombok (All projects)
- **API Gateway Pattern** (Spring Gateway)

### Spring Concepts
- **Dependency Injection** (Spring Basic)
- **IoC Container** (Spring Basic)
- **Bean Scopes** (Spring Basic)
- **Configuration Management** (Spring Basic)
- **RESTful API Design** (User Account App)
- **JWT Authentication** (User Account App)
- **Global Exception Handling** (User Account App)
- **Custom Filters** (Spring Gateway)
- **Query Optimization** (Spring JPA N+1 Problem)

### Best Practices
- Clean layered architecture
- Input validation
- Exception handling
- Security (JWT, BCrypt)
- Performance optimization
- Testing strategies
- Monitoring & logging

---

## Performance Insights

### N+1 Query Problem Impact (100 Authors)
| Method | Queries | Time | Improvement |
|--------|---------|------|-------------|
| N+1 Problem | 101 | ~1000ms | Baseline |
| FETCH JOIN | 1 | ~20ms | 99% fewer queries, 50x faster |
| EntityGraph | 1 | ~20ms | 99% fewer queries, 50x faster |

### K6 Performance Testing Results
| Test Type | Duration | Max Users | Purpose |
|-----------|----------|-----------|---------|
| Smoke | 30s | 1 | Sanity check |
| Load | 4.5m | 10 | Normal operations |
| Stress | 10m | 50 | Find limits |
| Spike | 3m | 50 | Burst traffic |

---

## Contributing

This is an educational repository. Feel free to:
- Report issues
- Suggest improvements
- Add more examples
- Improve documentation

---

## Resources

### Official Documentation
- [Spring Framework](https://spring.io/projects/spring-framework)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)

### Additional Learning
- [Baeldung Spring Tutorials](https://www.baeldung.com/spring-tutorial)
- [Spring Guides](https://spring.io/guides)
- [k6 Documentation](https://k6.io/docs/)

---

## License

This project is created for educational purposes.

---

## Acknowledgments

Built with:
- Spring Boot
- PostgreSQL
- JWT
- Lombok
- k6

**Happy Learning!**

Made with ☕ and Spring Boot
