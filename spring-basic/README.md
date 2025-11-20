# Spring Boot Basics Tutorial

A comprehensive, hands-on tutorial covering fundamental Spring Boot concepts with practical, production-ready examples.

## Table of Contents

1. [Singleton Pattern](#1-singleton-pattern)
2. [Spring Beans](#2-spring-beans)
3. [Dependency Injection](#3-dependency-injection)
4. [Annotations](#4-annotations)
5. [Configuration (application.properties)](#5-configuration)
6. [Environment Variables](#6-environment-variables)
7. [Setup Instructions](#setup-instructions)
8. [Testing the Examples](#testing-the-examples)

---

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Your favorite IDE (IntelliJ IDEA, VS Code, Eclipse)
- Postman or curl for testing REST endpoints
- Basic understanding of Java

---

## 1. Singleton Pattern

### What is Singleton?

The Singleton pattern ensures a class has **only one instance** throughout the application lifecycle and provides a global point of access to it.

### Traditional vs Spring Singleton

| Aspect | Traditional Singleton | Spring Singleton |
|--------|----------------------|------------------|
| **Implementation** | Manual (private constructor, static instance) | Automatic (@Service, @Component) |
| **Thread Safety** | Must implement yourself | Spring handles it |
| **Lifecycle** | You manage | Spring container manages |
| **Testing** | Difficult to mock | Easy with DI |
| **Scope** | JVM-wide | Application context-wide |

### Examples Location
```
src/main/java/com/springbasic/singleton/
├── DatabaseConnection.java          # Traditional singleton
├── SingletonService.java             # Spring singleton
└── SingletonDemoController.java      # Demo endpoints
```

### Key Endpoints
```http
GET http://localhost:9000/api/singleton/traditional
GET http://localhost:9000/api/singleton/spring
GET http://localhost:9000/api/singleton/compare
POST http://localhost:9000/api/singleton/process
```

### When to Use
- **Database connections**
- **Configuration managers**
- **Caching mechanisms**
- **Logging services**
- **Thread pools**

### Best Practices
✅ Use Spring-managed singletons (default scope)
✅ Let Spring handle lifecycle and thread safety
✅ Avoid global state in singletons
❌ Don't use traditional singleton in Spring applications

---

## 2. Spring Beans

### What is a Spring Bean?

A **Spring Bean** is an object that is instantiated, assembled, and managed by the Spring IoC (Inversion of Control) container.

### Bean Creation Methods

#### 1. Component Scanning (`@Component`, `@Service`, `@Repository`, `@Controller`)
```java
@Service
public class UserService {
    // Spring automatically creates and manages this bean
}
```

#### 2. Java Configuration (`@Configuration` + `@Bean`)
```java
@Configuration
public class AppConfig {
    @Bean
    public DataSource dataSource() {
        return new HikariDataSource();
    }
}
```

### Bean Scopes

| Scope | Description | Use Case |
|-------|-------------|----------|
| **singleton** (default) | One instance per Spring container | Services, repositories, configurations |
| **prototype** | New instance each time requested | Stateful objects, temporary workers |
| **request** | One instance per HTTP request | Web request-specific data |
| **session** | One instance per HTTP session | User session data |
| **application** | One instance per ServletContext | Application-wide shared data |

### Examples Location
```
src/main/java/com/springbasic/beans/
├── AppConfig.java              # @Configuration with @Bean methods
├── UserService.java            # @Component bean
├── EmailService.java           # Bean via @Configuration
├── BeanScopesDemo.java         # Scope demonstrations
└── BeanDemoController.java     # Demo endpoints
```

### Key Endpoints
```http
GET http://localhost:9000/api/beans/component
GET http://localhost:9000/api/beans/configuration
GET http://localhost:9000/api/beans/scopes/singleton
GET http://localhost:9000/api/beans/scopes/prototype
POST http://localhost:9000/api/beans/email
```

### Bean Lifecycle

```
Container Started
      ↓
Bean Instantiation
      ↓
Dependency Injection
      ↓
Post-Construction (@PostConstruct)
      ↓
Bean Ready for Use
      ↓
Pre-Destruction (@PreDestroy)
      ↓
Container Shutdown
```

---

## 3. Dependency Injection (DI)

### What is Dependency Injection?

**Dependency Injection** is a design pattern where objects receive their dependencies from external sources rather than creating them internally.

### Benefits
- ✅ **Loose Coupling**: Classes don't depend on concrete implementations
- ✅ **Testability**: Easy to mock dependencies for unit testing
- ✅ **Maintainability**: Changes in one class don't affect others
- ✅ **Flexibility**: Easy to swap implementations

### Types of Dependency Injection

#### 1. Constructor Injection (RECOMMENDED ⭐)
```java
@Service
@RequiredArgsConstructor  // Lombok generates constructor
public class NotificationService {
    private final MessageService messageService;  // Injected via constructor
}
```

**Pros:**
- Immutable dependencies (final fields)
- Required dependencies are explicit
- Easy to test
- Thread-safe

#### 2. Setter Injection
```java
@Service
public class OrderService {
    private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
}
```

**Use for:** Optional dependencies

#### 3. Field Injection (NOT RECOMMENDED ⚠️)
```java
@RestController
public class UserController {
    @Autowired
    private UserService userService;  // Don't do this in production!
}
```

**Why avoid:**
- Cannot make fields final (not immutable)
- Hard to test (need Spring context)
- Hides dependencies

### Qualifier and Primary

When multiple beans of the same type exist:

```java
@Service
@Primary  // This will be injected by default
public class EmailMessageService implements MessageService { }

@Service
public class SmsMessageService implements MessageService { }

// Using @Qualifier to choose specific implementation
@RequiredArgsConstructor
public class NotificationService {
    @Qualifier("smsMessageService")
    private final MessageService messageService;
}
```

### Examples Location
```
src/main/java/com/springbasic/di/
├── MessageService.java           # Interface
├── EmailMessageService.java      # Implementation 1 (@Primary)
├── SmsMessageService.java        # Implementation 2
├── NotificationService.java      # Constructor injection
├── UserController.java           # Field injection (demo only)
├── OrderService.java             # Setter injection
└── DIDemoController.java         # Demo endpoints
```

### Key Endpoints
```http
POST http://localhost:9000/api/di/constructor/notify
POST http://localhost:9000/api/di/setter/order
POST http://localhost:9000/api/di/qualifier/sms
POST http://localhost:9000/api/di/primary/email
GET http://localhost:9000/api/di/compare
```

---

## 4. Annotations

### What are Annotations?

Annotations are **metadata** that provide information about the code to the Spring Framework. They tell Spring how to configure and manage components.

### Common Spring Annotations

#### Stereotype Annotations

| Annotation | Purpose | Use Case |
|------------|---------|----------|
| `@Component` | Generic Spring component | General-purpose beans |
| `@Service` | Business logic layer | Services, business logic |
| `@Repository` | Data access layer | DAOs, database operations |
| `@Controller` | MVC controller | Traditional web controllers |
| `@RestController` | REST API controller | RESTful web services |

#### Configuration Annotations

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@Configuration` | Java-based configuration | Config classes |
| `@Bean` | Define a bean in @Configuration | Factory methods |
| `@Value` | Inject property values | `@Value("${app.name}")` |
| `@PropertySource` | Load properties files | External configurations |
| `@ConfigurationProperties` | Bind properties to POJO | Type-safe config |

#### Dependency Injection Annotations

| Annotation | Purpose | Usage |
|------------|---------|-------|
| `@Autowired` | Auto-wire dependencies | Fields, constructors, setters |
| `@Qualifier` | Specify which bean to inject | Multiple implementations |
| `@Primary` | Mark preferred bean | Default selection |
| `@Required` | Mark required dependency | Mandatory setters |

#### Bean Lifecycle Annotations

| Annotation | Purpose | When Called |
|------------|---------|-------------|
| `@PostConstruct` | Initialization callback | After DI complete |
| `@PreDestroy` | Cleanup callback | Before bean destroyed |

### Examples Location
```
src/main/java/com/springbasic/annotations/
├── ComponentExample.java          # @Component
├── ServiceExample.java            # @Service
├── RepositoryExample.java         # @Repository
├── ControllerExample.java         # @Controller
├── RestControllerExample.java     # @RestController
├── ConfigurationExample.java      # @Configuration
├── ValueExample.java              # @Value
├── AutowiredExample.java          # @Autowired
├── QualifierExample.java          # @Qualifier
└── AnnotationDemoController.java  # Demo endpoints
```

### Key Endpoints
```http
GET http://localhost:9000/api/annotations/component
POST http://localhost:9000/api/annotations/service
GET http://localhost:9000/api/annotations/repository
GET http://localhost:9000/api/annotations/configuration
GET http://localhost:9000/api/annotations/value
POST http://localhost:9000/api/annotations/qualifier/payment
GET http://localhost:9000/api/annotations/stereotypes/compare
```

---

## 5. Configuration (application.properties)

### What is application.properties?

The `application.properties` file is the primary configuration file for Spring Boot applications. It allows you to externalize configuration so you don't hard-code values.

### Configuration Methods

#### 1. @Value Annotation
```java
@Value("${app.name}")
private String appName;

@Value("${app.max-connections:100}")  // With default value
private int maxConnections;
```

#### 2. @ConfigurationProperties (Type-Safe)
```java
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    private String name;
    private String version;
    private int maxConnections;
}
```

#### 3. Environment Object
```java
@Autowired
private Environment environment;

public String getAppName() {
    return environment.getProperty("app.name");
}
```

### Property Placeholders

```properties
# Simple value
app.name=My Application

# With default value
app.timeout=${app.timeout-ms:30000}

# Property reference
app.full-description=${app.name} - ${app.version}

# Random values
app.secret=${random.value}
app.number=${random.int}
```

### Spring Expression Language (SpEL)

```properties
app.connections=100
```

```java
@Value("#{${app.connections} * 2}")  // Mathematical operation
private int maxConnections;

@Value("#{'${app.name}'.toUpperCase()}")  // String operation
private String upperName;
```

### Profile-Specific Properties

```
application.properties           # Default
application-development.properties
application-staging.properties
application-production.properties
```

Activate with:
```properties
spring.profiles.active=production
```

Or command line:
```bash
java -jar app.jar --spring.profiles.active=production
```

### Examples Location
```
src/main/java/com/springbasic/config/
├── AppProperties.java         # @Value and @ConfigurationProperties
├── DatabaseConfig.java        # Configuration class
└── ConfigDemoController.java  # Demo endpoints

src/main/resources/
└── application.properties     # Main configuration file
```

### Key Endpoints
```http
GET http://localhost:9000/api/config/properties
GET http://localhost:9000/api/config/database
GET http://localhost:9000/api/config/features
POST http://localhost:9000/api/config/database/connect
```

---

## 6. Environment Variables

### What are Environment Variables?

Environment variables are **system-level** key-value pairs that can be accessed by applications. They're essential for:
- **Security**: Store secrets outside source code
- **Flexibility**: Different values per environment
- **12-Factor App**: Follow cloud-native principles

### Why Use Environment Variables?

| Aspect | Hard-Coded | application.properties | Environment Variables |
|--------|------------|----------------------|----------------------|
| **Security** | ❌ Very Poor | ⚠️ Medium | ✅ Best |
| **Flexibility** | ❌ No | ⚠️ Limited | ✅ High |
| **Cloud-Ready** | ❌ No | ⚠️ Partial | ✅ Yes |
| **Secret Management** | ❌ Exposed | ⚠️ File-based | ✅ Secure |

### Setting Environment Variables

#### Linux/Mac:
```bash
export DB_PASSWORD=mysecretpassword
export API_KEY=abc123xyz
```

#### Windows (Command Prompt):
```cmd
set DB_PASSWORD=mysecretpassword
set API_KEY=abc123xyz
```

#### Windows (PowerShell):
```powershell
$env:DB_PASSWORD = "mysecretpassword"
$env:API_KEY = "abc123xyz"
```

#### Docker:
```yaml
environment:
  - DB_PASSWORD=mysecretpassword
  - API_KEY=abc123xyz
```

#### IntelliJ IDEA:
1. Run → Edit Configurations
2. Environment Variables → Add
3. Name: `DB_PASSWORD`, Value: `mysecretpassword`

### Using .env Files (Development)

Create `.env` file:
```env
DB_PASSWORD=mysecretpassword
API_KEY=abc123xyz
```

**IMPORTANT:**
```gitignore
# Add to .gitignore
.env
.env.local
.env.*.local
```

### Accessing in Spring Boot

#### 1. @Value Annotation
```java
@Value("${DB_PASSWORD}")
private String dbPassword;
```

#### 2. Environment Object
```java
@Autowired
private Environment env;

public String getDbPassword() {
    return env.getProperty("DB_PASSWORD");
}
```

#### 3. System.getenv()
```java
String dbPassword = System.getenv("DB_PASSWORD");
```

### Naming Conventions

✅ **Good:**
```
DB_HOST
DB_PORT
API_KEY
AWS_ACCESS_KEY_ID
```

❌ **Bad:**
```
dbHost          # Not uppercase
db-port         # Hyphens not underscores
apikey          # Not descriptive
```

### Best Practices

1. **Use UPPER_SNAKE_CASE** for variable names
2. **Never commit** secrets to version control
3. **Use different values** per environment (dev/staging/prod)
4. **Validate required** variables at startup
5. **Mask sensitive values** in logs
6. **Rotate secrets** regularly
7. **Use secret management** tools (AWS Secrets Manager, HashiCorp Vault)

### Examples Location
```
src/main/java/com/springbasic/env/
├── EnvironmentConfig.java     # Environment variable configuration
└── EnvDemoController.java     # Demo endpoints

Project Root:
├── .env.example               # Example environment variables
└── .gitignore                 # Ignore .env files
```

### Key Endpoints
```http
GET http://localhost:9000/api/env/system
GET http://localhost:9000/api/env/database
GET http://localhost:9000/api/env/api
GET http://localhost:9000/api/env/all
GET http://localhost:9000/api/env/profiles
GET http://localhost:9000/api/env/best-practices
```

---

## Setup Instructions

### 1. Clone and Build

```bash
cd spring-basic
./mvnw clean install
```

### 2. Configure Environment Variables

Copy the example file:
```bash
cp .env.example .env
```

Edit `.env` with your values:
```env
DB_PASSWORD=your_password
API_KEY=your_api_key
```

### 3. Set Environment Variables

**Option A: Export in Terminal**
```bash
export DB_PASSWORD=mypassword
export API_KEY=myapikey
```

**Option B: IDE Configuration**
- IntelliJ: Run → Edit Configurations → Environment Variables
- VS Code: Add to launch.json

**Option C: Use .env file (with dotenv library)**

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on: `http://localhost:9000`

---

## Testing the Examples

### Using curl

```bash
# Singleton Demo
curl http://localhost:9000/api/singleton/spring

# Bean Demo
curl http://localhost:9000/api/beans/scopes/singleton

# Dependency Injection
curl -X POST http://localhost:9000/api/di/constructor/notify \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello from DI!"}'

# Annotations
curl http://localhost:9000/api/annotations/component

# Configuration
curl http://localhost:9000/api/config/properties

# Environment Variables
curl http://localhost:9000/api/env/all
```

### Using Postman

1. Import the collection (if provided)
2. Test each endpoint category:
   - Singleton (`/api/singleton/*`)
   - Beans (`/api/beans/*`)
   - DI (`/api/di/*`)
   - Annotations (`/api/annotations/*`)
   - Config (`/api/config/*`)
   - Environment (`/api/env/*`)

### Using Browser

Simply navigate to:
```
http://localhost:9000/api/singleton/compare
http://localhost:9000/api/beans/component
http://localhost:9000/api/config/properties
http://localhost:9000/api/env/best-practices
```

---

## Project Structure

```
spring-basic/
├── src/main/java/com/springbasic/
│   ├── singleton/          # Singleton pattern examples
│   ├── beans/              # Spring beans examples
│   ├── di/                 # Dependency injection examples
│   ├── annotations/        # Annotation examples
│   ├── config/             # Configuration examples
│   └── env/                # Environment variable examples
├── src/main/resources/
│   └── application.properties  # Application configuration
├── .env.example            # Example environment variables
├── pom.xml                 # Maven configuration
└── README.md               # This file
```

---

## Learning Path

### Beginner (Start Here)
1. **Singleton Pattern** - Understand object lifecycle
2. **Spring Beans** - Learn IoC container basics
3. **Annotations** - Master common annotations

### Intermediate
4. **Dependency Injection** - Grasp loose coupling
5. **Configuration** - External configuration management

### Advanced
6. **Environment Variables** - Production-ready secrets management

---

## Common Interview Questions

### Singleton
**Q: Difference between singleton scope and singleton pattern?**
A: Singleton pattern ensures one instance per JVM. Spring singleton scope ensures one instance per Spring container. Multiple Spring contexts = multiple instances.

### Beans
**Q: What's the difference between @Component and @Bean?**
A: @Component is class-level, auto-detected by component scanning. @Bean is method-level in @Configuration classes for explicit bean definition.

### Dependency Injection
**Q: Why is constructor injection preferred?**
A: Immutability (final fields), required dependencies are explicit, easier testing, thread-safe.

### Configuration
**Q: @Value vs @ConfigurationProperties?**
A: @Value for single properties, simple. @ConfigurationProperties for grouped properties, type-safe, validation support, better for complex configs.

---

## Additional Resources

- [Spring Framework Documentation](https://spring.io/projects/spring-framework)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/reference/)
- [Baeldung Spring Tutorials](https://www.baeldung.com/spring-tutorial)
- [Spring Guides](https://spring.io/guides)

---

## Troubleshooting

### Port Already in Use
```bash
# Change port in application.properties
server.port=9001
```

### Bean Creation Errors
- Check @ComponentScan base package
- Verify @Configuration class is in correct package
- Check for circular dependencies

### Environment Variable Not Found
- Verify variable is set: `echo $DB_PASSWORD`
- Check spelling and case sensitivity
- Ensure IDE/runtime has access to variables

---

## Contributing

This is an educational project. Feel free to:
- Report issues
- Suggest improvements
- Add more examples
- Improve documentation

---

## License

This tutorial is provided for educational purposes.

---

**Happy Learning! 🚀**

For questions or feedback, please refer to the code comments and JavaDoc documentation in each class.
