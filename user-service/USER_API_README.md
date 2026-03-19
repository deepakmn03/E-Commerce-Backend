# User Service API Documentation

## Overview
The User Service is a RESTful microservice built with Spring Boot that handles user management operations in the e-commerce backend system. It provides comprehensive CRUD operations for user accounts with validation, error handling, and proper data mapping.

## Architecture Components

### 1. Entity Layer (`User.java`)
The `User` entity represents a user record in the database with the following fields:
- `userId` - Primary key (auto-generated)
- `firstName` - User's first name (required)
- `lastName` - User's last name (required)
- `email` - User's email (required, unique)
- `password` - User's password (required)
- `phone` - User's phone number (required, unique)
- `address` - User's street address (required)
- `city` - User's city (required)
- `state` - User's state (required)
- `zipCode` - User's zip code (required)
- `country` - User's country (required)

### 2. DTO Layer
#### UserRequestDTO
Used for receiving user data from API requests. Includes comprehensive validation:
- `@NotBlank` - Ensures required fields are not empty
- `@Email` - Validates email format
- `@Size` - Validates password minimum length (8 characters)
- `@Pattern` - Validates phone number format (10 digits)

#### UserResponseDTO
Used for returning user data in API responses. Excludes sensitive fields like passwords.

### 3. Repository Layer (`UserRepository.java`)
Spring Data JPA repository providing database access:
```java
Optional<User> findByEmail(String email);
Optional<User> findByPhone(String phone);
boolean existsByEmail(String email);
boolean existsByPhone(String phone);
```

### 4. Mapper Layer (`UserMapper.java`)
MapStruct mapper for converting between entities and DTOs:
- `toEntity()` - Converts UserRequestDTO to User entity
- `toResponseDTO()` - Converts User entity to UserResponseDTO
- `updateUserFromDTO()` - Updates existing User with DTO data
- Ignores `userId` during creation/updates
- Handles null values gracefully

### 5. Service Layer (`UserService.java`)
Business logic layer with the following operations:
- **Create User** - Validates email/phone uniqueness before saving
- **Get User by ID** - Retrieves user by primary key
- **Get User by Email** - Retrieves user by email address
- **Get All Users** - Returns list of all users
- **Update User** - Updates existing user with validation
- **Delete User** - Removes user from database
- **User Exists** - Checks if user exists by ID

### 6. Controller Layer (`UserController.java`)
REST endpoints for user management:

#### Health Check
```
GET /api/user/status
```
Returns: `"User service is live now!!!"`

#### Create User
```
POST /api/user/create
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "securePassword123",
  "phone": "1234567890",
  "address": "123 Main St",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA"
}
```
Returns: `UserResponseDTO` with HTTP 201 (Created)

#### Get User by ID
```
GET /api/user/get/{userId}
```
Returns: `UserResponseDTO` with HTTP 200 (OK)

#### Get User by Email
```
GET /api/user/get/email/{email}
```
Returns: `UserResponseDTO` with HTTP 200 (OK)

#### Get All Users
```
GET /api/user/get
```
Returns: List of `UserResponseDTO` with HTTP 200 (OK)

#### Update User
```
PATCH /api/user/update/{userId}
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane@example.com",
  "password": "newPassword123",
  "phone": "0987654321",
  "address": "456 Oak Ave",
  "city": "Boston",
  "state": "MA",
  "zipCode": "02101",
  "country": "USA"
}
```
Returns: Updated `UserResponseDTO` with HTTP 200 (OK)

#### Delete User
```
DELETE /api/user/remove/{userId}
```
Returns: `"User has been deleted with userId: {userId}"` with HTTP 200 (OK)

#### Check User Exists
```
GET /api/user/exists/{userId}
```
Returns: `true` or `false` with HTTP 200 (OK)

## Exception Handling

### EntityNotFoundException
Base exception class for entity-related errors:
- Takes error message and optional cause
- Extends RuntimeException

### UserNotFoundException
Specific exception for user not found scenarios:
- Constructor with userId: `"User not found with userId: {userId}"`
- Constructor with email: `"User not found with email: {email}"`
- Returns HTTP 404 (Not Found)

## Database Configuration

### application.yaml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/user-service
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update  # Creates/updates tables automatically

server:
  port: 8082  # User service runs on port 8082
```

## Dependencies
- Spring Boot 4.0.3
- Spring Data JPA
- Spring Cloud OpenFeign
- PostgreSQL Driver
- Lombok
- MapStruct
- Jakarta Validation
- Log4j2

## Building and Running

### Build the Project
```bash
cd user-service
mvn clean package
```

### Run the Application
```bash
java -jar target/user-service-0.0.1-SNAPSHOT.jar
```

Or using Maven:
```bash
mvn spring-boot:run
```

The service will be available at `http://localhost:8082`

## Features

### Validation
- Email format validation
- Password minimum length (8 characters)
- Phone number format (10 digits)
- Unique email and phone constraints
- All required fields validation

### Error Handling
- Proper HTTP status codes
- Meaningful error messages
- Exception handling with custom exceptions
- Input validation on request bodies

### Data Mapping
- Automatic entity to DTO conversion
- Secure response (excludes passwords)
- Flexible update operations

## Microservice Integration

The User Service is configured with Spring Cloud OpenFeign (`@EnableFeignClients`) to allow:
- Inter-service communication
- Service discovery (if Eureka is configured)
- Load balancing

## Testing

To test the API endpoints, use Postman or curl:

```bash
# Create a user
curl -X POST http://localhost:8082/api/user/create \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "securePassword123",
    "phone": "1234567890",
    "address": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  }'

# Get user by ID
curl http://localhost:8082/api/user/get/1

# Get all users
curl http://localhost:8082/api/user/get

# Update user
curl -X PATCH http://localhost:8082/api/user/update/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    ...
  }'

# Delete user
curl -X DELETE http://localhost:8082/api/user/remove/1

# Check health status
curl http://localhost:8082/api/user/status
```

## API Response Examples

### Success Response (HTTP 200/201)
```json
{
  "userId": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "phone": "1234567890",
  "address": "123 Main St",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA"
}
```

### Error Response (HTTP 404)
```json
{
  "timestamp": "2024-03-19T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with userId: 999"
}
```

## Future Enhancements
1. Add authentication and authorization (JWT)
2. Implement password encryption
3. Add email verification
4. Implement user roles and permissions
5. Add API rate limiting
6. Implement caching (Redis)
7. Add comprehensive logging and monitoring
8. Add unit and integration tests
