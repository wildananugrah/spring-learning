# JPA Learning - Comprehensive Guide

A complete demonstration of Spring Data JPA query methods including Derived Queries, JPQL, Native SQL, and Criteria API.

## Database Setup

```sql
-- Create database
CREATE DATABASE jpa_learning_db;

-- Connect and verify
\c jpa_learning_db
```

## Run the Application

```bash
# Option 1: Using Maven
./mvnw spring-boot:run

# Option 2: With custom database settings
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:postgresql://localhost:5432/jpa_learning_db"
```

Application runs on: `http://localhost:8001`

## Entity Relationship

```
Student (1) ──── (N) Enrollment (N) ──── (1) Course
```

- **Student**: Contains student information (name, email, status, etc.)
- **Course**: Contains course information (code, name, credits, etc.)
- **Enrollment**: Junction table linking students to courses with grades

## API Endpoints Overview

### Students API (`/api/students`)

#### CRUD Operations
- `POST /api/students` - Create student
- `GET /api/students/{id}` - Get student by ID
- `GET /api/students` - Get all students
- `PUT /api/students/{id}` - Update student
- `DELETE /api/students/{id}` - Delete student

#### Derived Query Methods
- `GET /api/students/by-student-id/{studentId}` - Find by student ID
- `GET /api/students/by-email?email={email}` - Find by email
- `GET /api/students/by-status/{status}` - Find by status
- `GET /api/students/by-gender/{gender}` - Find by gender
- `GET /api/students/search-by-name?firstName={name}` - Search by name

#### JPQL Queries
- `GET /api/students/by-full-name?firstName={first}&lastName={last}` - Find by full name
- `GET /api/students/by-email-domain?domain={domain}` - Find by email domain
- `GET /api/students/{id}/with-enrollments` - Get with enrollments (JOIN FETCH)
- `GET /api/students/count-by-gender-status?gender={gender}&status={status}` - Count query

#### Native SQL Queries
- `GET /api/students/younger-than?date={yyyy-MM-dd}` - Find students younger than date
- `GET /api/students/by-course/{courseId}` - Find students by course
- `GET /api/students/with-min-grade?minGrade={grade}` - Find students with minimum grade

#### Criteria API Queries
- `GET /api/students/criteria/by-status/{status}` - Find by status
- `GET /api/students/criteria/by-gender-status?gender={gender}&status={status}` - Multiple conditions
- `GET /api/students/criteria/search-by-name?pattern={pattern}` - LIKE query
- `GET /api/students/criteria/date-range?startDate={date}&endDate={date}` - Date range
- `GET /api/students/criteria/search?firstName={first}&lastName={last}&gender={gender}&status={status}` - Dynamic search
- `GET /api/students/criteria/enrolled-in-course/{courseId}` - JOIN query

#### Update Operations
- `PATCH /api/students/{id}/phone?phoneNumber={phone}` - Update phone
- `PATCH /api/students/bulk-update-status?oldStatus={old}&newStatus={new}` - Bulk update

#### Statistics
- `GET /api/students/statistics/enrollment-count` - Get enrollment statistics
- `GET /api/students/statistics/by-age?minAge={min}&maxAge={max}` - Find by age range

## Query Method Comparison

### 1. Derived Query Methods
**Pros**:
- Simple, no SQL/JPQL needed
- Type-safe
- Automatically implemented by Spring

**Cons**:
- Limited to simple queries
- Method names can get long

**Example**:
```java
List<Student> findByFirstNameAndLastName(String firstName, String lastName);
```

### 2. JPQL (Java Persistence Query Language)
**Pros**:
- Works with entities (database-independent)
- Supports complex queries
- Type-safe with entity references

**Cons**:
- Requires learning JPQL syntax
- Can't use database-specific features

**Example**:
```java
@Query("SELECT s FROM Student s WHERE s.firstName = :firstName")
List<Student> findByFirstName(@Param("firstName") String firstName);
```

### 3. Native SQL
**Pros**:
- Full SQL power
- Can use database-specific features
- Optimal for complex queries

**Cons**:
- Database-specific (not portable)
- Not type-safe
- Direct table/column references

**Example**:
```java
@Query(value = "SELECT * FROM students WHERE first_name = :name", nativeQuery = true)
List<Student> findByFirstNameNative(@Param("name") String name);
```

### 4. Criteria API
**Pros**:
- Fully type-safe
- Dynamic query building
- Programmatic and flexible

**Cons**:
- Verbose
- Steeper learning curve
- More code to write

**Example**:
```java
CriteriaBuilder cb = entityManager.getCriteriaBuilder();
CriteriaQuery<Student> query = cb.createQuery(Student.class);
Root<Student> student = query.from(Student.class);
query.select(student).where(cb.equal(student.get("firstName"), "John"));
```

## When to Use Each Approach

| Scenario | Recommended Approach |
|----------|---------------------|
| Simple CRUD | Derived Query Methods |
| Complex business logic | JPQL |
| Database-specific features | Native SQL |
| Dynamic queries with optional filters | Criteria API |
| Performance-critical queries | Native SQL or JPQL with fetch joins |
| Type-safety is critical | Criteria API |

## Testing Examples

### Create Student
```bash
curl -X POST http://localhost:8001/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": "STU001",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "dateOfBirth": "2000-01-15",
    "gender": "MALE",
    "address": "123 Main St",
    "phoneNumber": "555-1234",
    "status": "ACTIVE"
  }'
```

### Search Using Criteria API
```bash
curl "http://localhost:8001/api/students/criteria/search?firstName=John&status=ACTIVE"
```

### Get Students by Course (Native SQL)
```bash
curl "http://localhost:8001/api/students/by-course/1"
```

## Key JPA Concepts Demonstrated

1. **Entity Relationships**: `@OneToMany`, `@ManyToOne`
2. **Lazy Loading**: `FetchType.LAZY`
3. **N+1 Problem Solution**: `JOIN FETCH`
4. **Derived Queries**: Method name-based queries
5. **JPQL**: Entity-based queries
6. **Native SQL**: Database-specific queries
7. **Criteria API**: Type-safe dynamic queries
8. **Bulk Operations**: `@Modifying` queries
9. **Pagination**: `Pageable` support
10. **Aggregation**: `COUNT`, `AVG`, `GROUP BY`

## Important Notes

- All `@Modifying` queries must be used with `@Transactional`
- Use `JOIN FETCH` to avoid N+1 query problems
- Derived queries are perfect for simple cases
- Criteria API is best for dynamic queries
- Native queries should be used sparingly (database-specific)
- Always test queries with realistic data volumes
