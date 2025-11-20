# Spring Cloud Gateway - Banking App Gateway

This is a comprehensive example of Spring Cloud Gateway routing requests to the **User Account Banking Application** backend service, demonstrating routing configurations, custom filters, and best practices.

## 🌐 Architecture Overview

```
┌─────────────┐         ┌──────────────────┐         ┌─────────────────┐
│             │  HTTP   │                  │  HTTP   │                 │
│   Client    │ ──────> │  Spring Gateway  │ ──────> │  Banking API    │
│ (Port 9090) │         │   (Port 9090)    │         │  (Port 8000)    │
│             │ <────── │                  │ <────── │                 │
└─────────────┘         └──────────────────┘         └─────────────────┘
                              API Gateway              Backend Service
```

## Features

- **Backend Routing** - Routes requests to User Account Banking Application
- **Authentication Routing** - JWT-protected endpoints routing
- **Account Management** - Account CRUD operations routing
- **Transaction Routing** - Deposit, Withdrawal, Transfer operations
- **Custom Gateway Filter Factory** - Request/response logging
- **Global Filter** - Applied to all routes
- **Request/Response header manipulation**
- **CORS configuration** - Enabled for all origins
- **Actuator endpoints** - Monitoring and health checks
- **Example external API routes** - For learning purposes

## Project Structure

```
src/main/java/com/user/account/app/
├── UserAccountApplication.java           # Main application class
├── config/
│   └── GatewayConfig.java               # Programmatic route configuration
└── filter/
    ├── CustomGlobalFilter.java          # Global filter for all routes
    └── LoggingGatewayFilterFactory.java # Custom filter factory
```

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- **User Account Banking App** running on port 8000

### Step 1: Start the Backend Service
```bash
cd ../user-account-app
./mvnw spring-boot:run
# Backend will start on http://localhost:8000
```

### Step 2: Start the Gateway
```bash
cd spring-gateway
./mvnw spring-boot:run
# Gateway will start on http://localhost:9090
```

### Step 3: Test the Gateway
```bash
# Via Gateway (Port 9090)
curl http://localhost:9090/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"pass123","fullName":"Test User"}'

# Direct Backend (Port 8000)
curl http://localhost:8000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test2","email":"test2@example.com","password":"pass123","fullName":"Test User 2"}'
```

## 📋 Routes Configuration

### Backend Service Routes (application.yml)

#### 1. **Authentication Routes** - `/api/auth/**`
- **URI**: `http://localhost:8000`
- **Endpoints**:
  - `POST /api/auth/register` - User registration
  - `POST /api/auth/login` - User login (get JWT token)
- **Filters**:
  - Adds `X-Gateway: SpringCloudGateway` header
  - Adds `X-Routed-By: Gateway` response header

#### 2. **Account Management Routes** - `/api/accounts/**`
- **URI**: `http://localhost:8000`
- **Endpoints**:
  - `POST /api/accounts` - Create account
  - `GET /api/accounts` - Get all accounts
  - `GET /api/accounts/{accountNumber}` - Get specific account
  - `DELETE /api/accounts/{accountNumber}` - Delete account
- **Authentication**: Required (JWT Bearer token)
- **Filters**:
  - Adds `X-Gateway: SpringCloudGateway` header
  - Adds `X-Routed-By: Gateway` response header

#### 3. **Transaction Routes** - `/api/transactions/**`
- **URI**: `http://localhost:8000`
- **Endpoints**:
  - `POST /api/transactions/deposit` - Deposit money
  - `POST /api/transactions/withdrawal` - Withdraw money
  - `POST /api/transactions/transfer` - Transfer between accounts
  - `GET /api/transactions` - Get transaction history
- **Authentication**: Required (JWT Bearer token)
- **Filters**:
  - Adds `X-Gateway: SpringCloudGateway` header
  - Adds `X-Routed-By: Gateway` response header

#### 4. **Actuator Routes** - `/actuator/**`
- **URI**: `http://localhost:8000`
- **Endpoints**:
  - `GET /actuator/health` - Backend health check
  - `GET /actuator/info` - Backend info
  - `GET /actuator/metrics` - Backend metrics

### Test/Example Routes

#### 5. **JSONPlaceholder Route** - `/test/posts/**`
- Forwards to: https://jsonplaceholder.typicode.com
- Demonstrates routing to external API

#### 6. **ReqRes Route** - `/test/users/**`
- Forwards to: https://reqres.in
- Demonstrates response header manipulation

#### 7. **HTTPBin Route** - `/httpbin/**`
- Forwards to: https://httpbin.org
- Demonstrates request inspection

### Java-based Routes (GatewayConfig.java)

1. **GitHub Route** - `/github/**`
   - Routes to GitHub API
   - Adds custom headers

2. **GET Only Route** - `/get-only/**`
   - Only accepts GET requests
   - Routes to HTTPBin

3. **Host Route** - `*.example.com/api/**`
   - Host-based routing example

4. **Query Route** - `/search/**?q=value`
   - Query parameter predicate example

5. **Custom Filter Route** - `/custom/**`
   - Demonstrates inline custom filter

## 🧪 Testing the Gateway

### Using HTTP File
Open [gateway-backend-test.http](gateway-backend-test.http) in VS Code (with REST Client extension) or IntelliJ IDEA.

### Complete Flow Example

#### 1. Register a User (via Gateway)
```bash
curl -X POST http://localhost:9090/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "gateway_user",
    "email": "gateway@example.com",
    "password": "password123",
    "fullName": "Gateway Test User"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "userId": 1,
    "username": "gateway_user",
    "email": "gateway@example.com"
  }
}
```

#### 2. Login and Get JWT Token
```bash
curl -X POST http://localhost:9090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "gateway_user",
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
    "username": "gateway_user"
  }
}
```

#### 3. Create Account (Protected - Requires JWT)
```bash
TOKEN="your-jwt-token-here"

curl -X POST http://localhost:9090/api/accounts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "accountName": "Gateway Savings",
    "initialBalance": 5000.00
  }'
```

#### 4. Make a Deposit
```bash
curl -X POST http://localhost:9090/api/transactions/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "accountNumber": "0123456789",
    "amount": 1000.00,
    "description": "Deposit via Gateway"
  }'
```

#### 5. Get Transaction History
```bash
curl -X GET "http://localhost:9090/api/transactions?accountNumber=0123456789&page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

### Testing External API Routes

#### Test JSONPlaceholder
```bash
curl http://localhost:9090/test/posts/1
```

#### Test ReqRes
```bash
curl http://localhost:9090/test/users/2
```

#### Test HTTPBin
```bash
curl http://localhost:9090/httpbin/get
```

## Actuator Endpoints

### View all routes
```bash
curl http://localhost:8080/actuator/gateway/routes
```

### Check health
```bash
curl http://localhost:8080/actuator/health
```

### View specific route
```bash
curl http://localhost:8080/actuator/gateway/routes/github_route
```

## Custom Filters

### Global Filter
The `CustomGlobalFilter` applies to all routes and:
- Logs incoming requests
- Adds request timestamp header
- Adds response timestamp and duration headers
- Logs response information

### Logging Filter Factory
The `LoggingGatewayFilterFactory` is a configurable filter that:
- Logs request details (method, URI, headers)
- Logs response details (status code, headers)
- Can be configured to enable/disable pre and post logging

To use it in your route configuration:
```yaml
filters:
  - name: Logging
    args:
      preLogger: true
      postLogger: true
```

## Key Concepts

### Predicates
Predicates determine if a route matches a request. Examples:
- `Path=/api/**` - Matches path patterns
- `Method=GET` - Matches HTTP methods
- `Host=*.example.com` - Matches host patterns
- `Query=param` - Matches query parameters

### Filters
Filters modify requests and responses. Built-in filters include:
- `StripPrefix` - Removes path prefix
- `AddRequestHeader` - Adds header to request
- `AddResponseHeader` - Adds header to response
- `RewritePath` - Rewrites request path
- `RedirectTo` - Redirects to another URL

### Global Filters
Applied to all routes automatically. Useful for:
- Authentication
- Logging
- Metrics collection
- Header manipulation

## Configuration Properties

Key configuration in `application.yml`:
- `server.port` - Gateway port (default: 8080)
- `spring.cloud.gateway.routes` - Route definitions
- `spring.cloud.gateway.globalcors` - CORS configuration
- `management.endpoints.web.exposure` - Actuator endpoints

## Dependencies

Main dependencies:
- `spring-cloud-starter-gateway` - Core gateway functionality
- `spring-boot-starter-actuator` - Monitoring and management
- `lombok` - Code generation (optional)

## 🎯 Why Use an API Gateway?

### Benefits

#### 1. **Single Entry Point**
- All client requests go through one unified endpoint
- Simplifies client configuration
- Central point for monitoring and logging

#### 2. **Security**
- Centralized authentication and authorization
- Hide backend service details from clients
- Rate limiting and DDoS protection
- SSL termination

#### 3. **Load Balancing**
- Distribute traffic across multiple backend instances
- Health checks and automatic failover
- Better resource utilization

#### 4. **Request/Response Transformation**
- Modify headers (add, remove, update)
- Request routing based on conditions
- Response aggregation from multiple services

#### 5. **Monitoring & Logging**
- Centralized logging of all requests
- Performance metrics collection
- Easier debugging and troubleshooting

### Gateway vs Direct Backend Access

| Aspect | Via Gateway (Port 9090) | Direct Backend (Port 8000) |
|--------|------------------------|----------------------------|
| **Security** | ✅ Additional layer | ❌ Exposed directly |
| **Monitoring** | ✅ Centralized logging | ⚠️ Per-service logs |
| **Load Balancing** | ✅ Built-in support | ❌ Manual setup needed |
| **Rate Limiting** | ✅ Easy to implement | ⚠️ Per-service config |
| **CORS** | ✅ Global config | ⚠️ Per-service config |
| **Caching** | ✅ Gateway-level cache | ❌ No shared cache |
| **Client Simplicity** | ✅ One endpoint | ❌ Multiple endpoints |

## 📊 Request Flow

### Complete Request Flow with Gateway

```
1. Client sends request
   │
   ├─> POST http://localhost:9090/api/auth/login
   │
2. Gateway receives request
   │
   ├─> Global Filter (CustomGlobalFilter)
   │   └─> Logs request
   │   └─> Adds timestamp header
   │
3. Route Matching
   │
   ├─> Matches: auth_route
   │   └─> Predicate: Path=/api/auth/**
   │   └─> URI: http://localhost:8000
   │
4. Apply Route Filters
   │
   ├─> AddRequestHeader: X-Gateway=SpringCloudGateway
   │
5. Forward to Backend
   │
   ├─> POST http://localhost:8000/api/auth/login
   │   └─> Backend processes request
   │   └─> Returns JWT token
   │
6. Gateway processes response
   │
   ├─> AddResponseHeader: X-Routed-By=Gateway
   │
7. Global Filter (response phase)
   │
   ├─> Logs response
   │   └─> Adds duration header
   │
8. Return to Client
   │
   └─> Response with JWT token
```

## 🔧 Configuration Details

### Port Configuration

**Gateway** ([application.yml](src/main/resources/application.yml))
```yaml
server:
  port: 9090  # Gateway listens on 9090
```

**Backend** ([user-account-app/application.properties](../user-account-app/src/main/resources/application.properties))
```properties
server.port=8000  # Backend listens on 8000
```

### CORS Configuration

The gateway has global CORS enabled for all origins:
```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins: "*"
            allowedMethods: [GET, POST, PUT, DELETE]
            allowedHeaders: "*"
```

This means frontend applications can call the gateway from any domain.

## 🔍 Monitoring & Debugging

### View All Gateway Routes
```bash
curl http://localhost:9090/actuator/gateway/routes | jq
```

### View Specific Route Details
```bash
curl http://localhost:9090/actuator/gateway/routes/auth_route | jq
```

### Gateway Health Check
```bash
curl http://localhost:9090/actuator/health
```

### Backend Health Check (via Gateway)
```bash
curl http://localhost:9090/actuator/health
```

### Check Gateway Logs
The gateway logs will show:
- Incoming requests with timestamps
- Route matching decisions
- Request forwarding to backend
- Response processing
- Added headers

Look for logs like:
```
DEBUG org.springframework.cloud.gateway: Route matched: auth_route
DEBUG reactor.netty.http.client: [id:0x...] CONNECT: localhost:8000
```

## 🚀 Production Considerations

### 1. **Use Environment Variables**
```yaml
routes:
  - id: auth_route
    uri: ${BACKEND_URI:http://localhost:8000}  # Use env var
```

### 2. **Enable Circuit Breaker**
Add resilience4j for circuit breaker pattern:
```yaml
filters:
  - name: CircuitBreaker
    args:
      name: backendCircuitBreaker
      fallbackUri: forward:/fallback
```

### 3. **Add Rate Limiting**
```yaml
filters:
  - name: RequestRateLimiter
    args:
      redis-rate-limiter.replenishRate: 10
      redis-rate-limiter.burstCapacity: 20
```

### 4. **Enable HTTPS**
```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: changeit
```

### 5. **Service Discovery**
Use Eureka or Consul for dynamic service discovery:
```yaml
routes:
  - id: auth_route
    uri: lb://USER-ACCOUNT-SERVICE  # Load-balanced
```

## 📚 Learn More

- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [Gateway Filter Factories](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#gatewayfilter-factories)
- [Route Predicate Factories](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#gateway-request-predicates-factories)
- [User Account Banking App](../user-account-app/README.md)

## 🤝 Related Projects

- **Backend Service**: [user-account-app](../user-account-app/) - The banking application backend
- **Spring Basic**: [spring-basic](../spring-basic/) - Spring Boot fundamentals

---

**Built with Spring Cloud Gateway 2024.0.0 and Spring Boot 3.4.1**
