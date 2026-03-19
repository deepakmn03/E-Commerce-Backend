# E-Commerce Microservices Backend

A Spring Boot microservices architecture for an e-commerce platform with separate User and Order services communicating via Spring Cloud Feign.

## Architecture Overview

- **User Service**: Manages user accounts and profiles (Port: 8082)
- **Order Service**: Manages orders with user validation (Port: 8083)
- **Database**: PostgreSQL (Port: 5432)
- **Inter-service Communication**: Spring Cloud Feign

## Prerequisites

- Java 21
- PostgreSQL 5432
- Maven 3.6+

## Database Setup

Create two PostgreSQL databases:
```sql
CREATE DATABASE "user-service";
CREATE DATABASE "order-service";
```

The schema will be auto-created via Hibernate `ddl-auto: update` on first startup.

## Running the Services

### User Service
```bash
cd user-service
mvn spring-boot:run
# Runs on http://localhost:8082
```

### Order Service
```bash
cd order-service
mvn spring-boot:run
# Runs on http://localhost:8083
```

---

## USER SERVICE API DOCUMENTATION

**Base URL**: `http://localhost:8082/api/user`

### 1. Create User
- **Endpoint**: `POST /create`
- **Description**: Create a new user account
- **Request Body**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "phone": "9876543210",
  "address": "123 Main St",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA"
}
```
- **Response** (201 Created):
```json
{
  "userId": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "phone": "9876543210",
  "address": "123 Main St",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA"
}
```
- **Validations**:
  - firstName, lastName: Required, non-blank
  - email: Required, valid email format, unique
  - phone: Required, 10 digits, unique
  - password: Required, minimum 8 characters
  - All address fields: Required, non-blank

---

### 2. Get User by ID
- **Endpoint**: `GET /get/{userId}`
- **Description**: Retrieve user details by ID
- **Path Parameter**: `userId` (Long)
- **Response** (200 OK):
```json
{
  "userId": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "phone": "9876543210",
  "address": "123 Main St",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA"
}
```
- **Error Response** (404 Not Found):
```json
{
  "error": "User not found"
}
```

---

### 3. Get User by Email
- **Endpoint**: `GET /get/email/{email}`
- **Description**: Retrieve user details by email address
- **Path Parameter**: `email` (String)
- **Response** (200 OK): Same as Get User by ID
- **Error Response** (404 Not Found): User not found

---

### 4. Get All Users
- **Endpoint**: `GET /get`
- **Description**: Retrieve all users in the system
- **Response** (200 OK):
```json
[
  {
    "userId": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "phone": "9876543210",
    "address": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  },
  {
    "userId": 2,
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane@example.com",
    "phone": "8765432109",
    "address": "456 Oak Ave",
    "city": "Los Angeles",
    "state": "CA",
    "zipCode": "90001",
    "country": "USA"
  }
]
```

---

### 5. Update User
- **Endpoint**: `PATCH /update/{userId}`
- **Description**: Update existing user information
- **Path Parameter**: `userId` (Long)
- **Request Body**: Same as Create User (all fields optional except userId)
```json
{
  "firstName": "Jonathan",
  "email": "newemail@example.com",
  "phone": "8765432109"
}
```
- **Response** (200 OK): Updated user object
- **Error Response** (404 Not Found): User not found

---

### 6. Delete User
- **Endpoint**: `DELETE /remove/{userId}`
- **Description**: Delete a user account
- **Path Parameter**: `userId` (Long)
- **Response** (200 OK):
```json
"User with ID: 1 has been deleted."
```
- **Error Response** (404 Not Found): User not found

---

### 7. Check User Exists
- **Endpoint**: `GET /exists/{userId}`
- **Description**: Check if a user exists
- **Path Parameter**: `userId` (Long)
- **Response** (200 OK):
```json
{
  "exists": true,
  "userId": 1
}
```

---

### 8. Service Status
- **Endpoint**: `GET /status`
- **Description**: Check if User Service is running
- **Response** (200 OK):
```
User service is live now!!!
```

---

## ORDER SERVICE API DOCUMENTATION

**Base URL**: `http://localhost:8083/api/order`

### 1. Create Order
- **Endpoint**: `POST /create`
- **Description**: Create a new order (validates user exists via User Service)
- **Request Body**:
```json
{
  "userId": 1,
  "orderValue": 199.99
}
```
- **Response** (200 OK):
```json
{
  "orderId": 1,
  "userId": 1,
  "orderValue": 199.99,
  "status": "PENDING",
  "createdAt": "2026-03-19T10:30:45.123456",
  "updatedAt": "2026-03-19T10:30:45.123456"
}
```
- **Validations**:
  - userId: Required, positive, must exist in User Service
  - orderValue: Required, positive number (Double)
- **Error Response** (400 Bad Request):
```json
{
  "error": "Cannot create order: User not found."
}
```
- **Error Response** (503 Service Unavailable):
```json
{
  "error": "Service unavailable. Please try again later."
}
```

---

### 2. Get Order by ID
- **Endpoint**: `GET /get/{orderId}`
- **Description**: Retrieve order details by order ID
- **Path Parameter**: `orderId` (Long)
- **Response** (200 OK):
```json
{
  "orderId": 1,
  "userId": 1,
  "orderValue": 199.99,
  "status": "PENDING",
  "createdAt": "2026-03-19T10:30:45.123456",
  "updatedAt": "2026-03-19T10:30:45.123456"
}
```
- **Error Response** (404 Not Found): Order not found

---

### 3. Get Orders by User ID
- **Endpoint**: `GET /get/user/{userId}`
- **Description**: Retrieve all orders for a specific user
- **Path Parameter**: `userId` (Long)
- **Response** (200 OK):
```json
[
  {
    "orderId": 1,
    "userId": 1,
    "orderValue": 199.99,
    "status": "PENDING",
    "createdAt": "2026-03-19T10:30:45.123456",
    "updatedAt": "2026-03-19T10:30:45.123456"
  },
  {
    "orderId": 2,
    "userId": 1,
    "orderValue": 299.99,
    "status": "COMPLETED",
    "createdAt": "2026-03-19T11:00:00.123456",
    "updatedAt": "2026-03-19T11:05:00.123456"
  }
]
```

---

### 4. Get All Orders
- **Endpoint**: `GET /get`
- **Description**: Retrieve all orders in the system
- **Response** (200 OK): Array of order objects (same structure as above)

---

### 5. Delete Order
- **Endpoint**: `DELETE /remove/{orderId}`
- **Description**: Delete an order
- **Path Parameter**: `orderId` (Long)
- **Response** (200 OK):
```json
"Order with order ID: 1 has been deleted."
```
- **Error Response** (404 Not Found): Order not found

---

### 6. Service Status
- **Endpoint**: `GET /status`
- **Description**: Check if Order Service is running
- **Response** (200 OK):
```
Order service is live now!!!
```

---

## Data Models

### User Entity
```
userId (Long, PK, Auto-generated)
firstName (String, Required)
lastName (String, Required)
email (String, Required, Unique)
password (String, Required, encrypted)
phone (String, Required, Unique)
address (String, Required)
city (String, Required)
state (String, Required)
zipCode (String, Required)
country (String, Required)
```

### Order Entity
```
orderId (Long, PK, Auto-generated)
userId (Long, FK, Required)
orderValue (Double, Required)
status (String, Default: "PENDING")
createdAt (LocalDateTime, Auto-set on creation)
updatedAt (LocalDateTime, Auto-updated on modification)
```

---

## Testing with Postman

### Import Collections
Pre-built Postman collections are available:
- `Order_Service_Bulk_Upload.postman_collection.json` - 10 users
- `Order_Service_Bulk_Upload_20Orders.postman_collection.json` - 20 orders (2 per user)

### Manual Testing Steps

1. **Create Users** (User Service):
   - POST http://localhost:8082/api/user/create
   - Create at least 2-3 users

2. **Create Orders** (Order Service):
   - POST http://localhost:8083/api/order/create
   - Use valid userIds from created users

3. **Verify Data**:
   - GET http://localhost:8083/api/order/get
   - GET http://localhost:8082/api/user/get

---

## Error Handling

All services follow standard HTTP status codes:
- **200 OK**: Successful request
- **201 Created**: Resource created successfully
- **400 Bad Request**: Invalid input or validation failure
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error
- **503 Service Unavailable**: Service unavailable (inter-service communication failure)

---

## Configuration Files

### User Service (application.yaml)
```yaml
server:
  port: 8082

spring:
  application:
    name: user-service
  datasource:
    url: jdbc:postgresql://localhost:5432/user-service
    username: postgres
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

### Order Service (application.yaml)
```yaml
server:
  port: 8083

spring:
  application:
    name: order-service
  datasource:
    url: jdbc:postgresql://localhost:5432/order-service
    username: postgres
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

---

## Technologies Used

- **Spring Boot 4.0.3**
- **Spring Data JPA** - ORM and database access
- **Spring Cloud OpenFeign** - Inter-service HTTP communication
- **MapStruct 1.5.5** - Bean mapping
- **PostgreSQL** - Relational database
- **Lombok 1.18.30** - Boilerplate reduction
- **Jakarta Validation** - Request validation
- **Log4j2** - Logging

---

## Development Notes

- Auto-generated fields (orderId, createdAt, updatedAt, status) are handled by the database and @PrePersist/@PreUpdate annotations
- Password is encrypted at the service layer (implement as needed)
- Email and phone uniqueness is enforced at the database level
- Order creation validates user existence via Feign client call to User Service
- All DTOs exclude sensitive fields (e.g., password from UserResponseDTO)

---

## Future Enhancements

- Add JWT authentication and authorization
- Implement order status workflow (PENDING → PROCESSING → SHIPPED → DELIVERED)
- Add payment service integration
- Implement inventory management
- Add API rate limiting
- Implement caching layer (Redis)
- Add comprehensive logging and monitoring
- Deploy to cloud (AWS, GCP, Azure)

---

## Support

For issues or questions, please check the service logs or refer to the configuration files.
