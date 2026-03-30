# E-Commerce Backend - Complete Implementation Guide

## Overview
This document provides a comprehensive guide for building and running the complete E-Commerce microservices backend with 7 independent services.

## System Architecture

### Service Overview

| Service | Port | Database | Purpose |
|---------|------|----------|---------|
| User Service | 8082 | user-service | User authentication & profile management |
| Cart Service | 8004 | cart-service | Shopping cart operations |
| Product Service | 8084 | product-service | Product catalog management |
| Order Service | 8083 | order-service | Order processing & tracking |
| **Payment Service** | **8085** | **payment-service** | Payment processing with mock gateway |
| **Notification Service** | **8086** | **notification-service** | Email/SMS/In-App notifications |
| **Inventory Service** | **8087** | **inventory-service** | Stock management & reservations |

## New Services Implementation

### 1. Payment Service (Port 8085)

**Purpose:** Handles payment processing, transactions, and refunds for orders using a mock payment gateway (95% success rate).

**Key Features:**
- Process payments for orders
- Track transaction status (PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED, CANCELLED)
- Support multiple payment methods (CREDIT_CARD, DEBIT_CARD, NET_BANKING, UPI, WALLET, COD)
- Mock payment gateway with transactionId generation
- Refund functionality with validation

**API Endpoints:**
```
GET    /api/payment/status              - Health check
POST   /api/payment/process             - Process new payment
GET    /api/payment/get/{paymentId}    - Get payment details
GET    /api/payment/order/{orderId}    - Get payment by order
GET    /api/payment/all                 - List all payments
GET    /api/payment/status/{status}    - Filter by payment status
POST   /api/payment/refund/{paymentId} - Refund a payment
```

**Payment Request Model:**
```json
{
  "orderId": 1,
  "amount": 5000.00,
  "method": "CREDIT_CARD"
}
```

**Payment Response Model:**
```json
{
  "paymentId": 1,
  "orderId": 1,
  "amount": 5000.00,
  "status": "COMPLETED",
  "method": "CREDIT_CARD",
  "transactionId": "TXN_1704067200000_a1b2c3d4",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:05"
}
```

---

### 2. Notification Service (Port 8086)

**Purpose:** Handles multi-channel notifications (Email, SMS, In-App) for order events, payments, and refunds.

**Key Features:**
- Send notifications through multiple channels (EMAIL, SMS, IN_APP)
- Support various notification types (ORDER_PLACED, PAYMENT_CONFIRMED, SHIPMENT_DISPATCHED, ORDER_DELIVERED, REFUND_INITIATED, REFUND_COMPLETED, INVENTORY_LOW, PROMOTIONAL)
- Track notification delivery status
- Queue and manage pending notifications
- Mock sending with 100ms delay simulation

**API Endpoints:**
```
GET    /api/notification/status              - Health check
POST   /api/notification/send                - Send new notification
GET    /api/notification/get/{notificationId} - Get notification details
GET    /api/notification/all                 - List all notifications
GET    /api/notification/user/{userId}      - Get user notifications
GET    /api/notification/pending             - Get pending notifications
GET    /api/notification/user/{userId}/sent  - Get sent notifications for user
```

**Notification Request Model:**
```json
{
  "userId": 1,
  "type": "ORDER_PLACED",
  "channel": "EMAIL",
  "recipient": "user@example.com",
  "subject": "Your Order #12345",
  "message": "Your order has been successfully placed"
}
```

**Notification Response Model:**
```json
{
  "notificationId": 1,
  "userId": 1,
  "type": "ORDER_PLACED",
  "channel": "EMAIL",
  "recipient": "user@example.com",
  "subject": "Your Order #12345",
  "message": "Your order has been successfully placed",
  "sent": true,
  "sentAt": "2024-01-01T10:00:05",
  "createdAt": "2024-01-01T10:00:00"
}
```

---

### 3. Inventory Service (Port 8087)

**Purpose:** Manages product stock levels, reservations, and low-stock alerts.

**Key Features:**
- Track available and reserved stock
- Check stock availability before order placement
- Deduct stock on order confirmation
- Add stock on refunds
- Calculate available quantity (available - reserved)
- Low-stock alerts and reorder level tracking
- Stock reservation and release for orders

**API Endpoints:**
```
GET    /api/inventory/status                       - Health check
POST   /api/inventory/create                       - Create new inventory
GET    /api/inventory/get/{inventoryId}           - Get inventory details
GET    /api/inventory/product/{productId}         - Get inventory by product
GET    /api/inventory/all                         - List all inventory
GET    /api/inventory/check/{productId}/{quantity} - Check stock availability
PUT    /api/inventory/deduct/{productId}/{quantity} - Deduct stock for order
PUT    /api/inventory/add/{productId}/{quantity}  - Add stock (refunds)
GET    /api/inventory/low-stock                   - Get low-stock items
```

**Inventory Request Model:**
```json
{
  "productId": 1,
  "quantityAvailable": 100,
  "reorderLevel": 20
}
```

**Inventory Response Model:**
```json
{
  "inventoryId": 1,
  "productId": 1,
  "quantityAvailable": 100,
  "quantityReserved": 0,
  "availableQuantity": 100,
  "reorderLevel": 20,
  "isActive": true,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

---

## Security & Authentication

### JWT Token Configuration

All services use JWT-based stateless authentication with the following configuration:

```yaml
jwt:
  secret: "mySecretKeyForJWTTokenGenerationAndValidation12345"
  expiration: 86400000  # 24 hours in milliseconds
```

### Authentication Flow

1. **Get Token from User Service:**
```bash
curl -X POST http://localhost:8082/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

Response includes JWT token.

2. **Use Token to Access Protected Services:**
```bash
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  http://localhost:8085/api/payment/all
```

### Public Endpoints (No Token Required)
- `/api/service/status` - Health check for any service

### Protected Endpoints (Require Bearer Token)
All other endpoints require JWT token in `Authorization: Bearer <token>` header.

---

## Database Setup

### PostgreSQL Configuration

Each service requires its own PostgreSQL database:

```sql
-- Create databases
CREATE DATABASE "user-service";
CREATE DATABASE "cart-service";
CREATE DATABASE "product-service";
CREATE DATABASE "order-service";
CREATE DATABASE "payment-service";
CREATE DATABASE "notification-service";
CREATE DATABASE "inventory-service";
```

### Application Configuration

Each service's `application.yaml` includes:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/<service-name>
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
```

---

## Building & Running Services

### Prerequisites
- Java 21+
- Maven 3.11.0+
- PostgreSQL 13+

### Build All Services

```bash
cd /Users/deepakmn/Desktop/myWork/projects/E-Commerce\ backend

# Build User Service
cd user-service && mvn clean install && cd ..

# Build Cart Service
cd cart-service && mvn clean install && cd ..

# Build Product Service
cd product-service && mvn clean install && cd ..

# Build Order Service
cd order-service && mvn clean install && cd ..

# Build Payment Service
cd payment-service && mvn clean install && cd ..

# Build Notification Service
cd notification-service && mvn clean install && cd ..

# Build Inventory Service
cd inventory-service && mvn clean install && cd ..
```

### Run All Services

Open separate terminal windows and run each service:

```bash
# Terminal 1 - User Service (8082)
cd user-service && mvn spring-boot:run

# Terminal 2 - Cart Service (8004)
cd cart-service && mvn spring-boot:run

# Terminal 3 - Product Service (8084)
cd product-service && mvn spring-boot:run

# Terminal 4 - Order Service (8083)
cd order-service && mvn spring-boot:run

# Terminal 5 - Payment Service (8085)
cd payment-service && mvn spring-boot:run

# Terminal 6 - Notification Service (8086)
cd notification-service && mvn spring-boot:run

# Terminal 7 - Inventory Service (8087)
cd inventory-service && mvn spring-boot:run
```

---

## Service Startup Sequence

**Recommended Order:**
1. User Service (authentication dependency)
2. Product Service (product data)
3. Inventory Service (stock management)
4. Payment Service (payment processing)
5. Notification Service (event notifications)
6. Cart Service (uses inventory & products)
7. Order Service (uses all services)

---

## Common API Usage Examples

### 1. Create Payment

```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..." # Get from login

curl -X POST http://localhost:8085/api/payment/process \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "amount": 5000.00,
    "method": "CREDIT_CARD"
  }'
```

### 2. Send Notification

```bash
curl -X POST http://localhost:8086/api/notification/send \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "type": "PAYMENT_CONFIRMED",
    "channel": "EMAIL",
    "recipient": "user@example.com",
    "subject": "Payment Received",
    "message": "Your payment of $5000 has been received"
  }'
```

### 3. Create Inventory

```bash
curl -X POST http://localhost:8087/api/inventory/create \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantityAvailable": 100,
    "reorderLevel": 20
  }'
```

### 4. Check Stock Availability

```bash
curl http://localhost:8087/api/inventory/check/1/10 \
  -H "Authorization: Bearer $TOKEN"
```

### 5. Process Refund

```bash
curl -X POST http://localhost:8085/api/payment/refund/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## Monitoring & Debugging

### Health Checks
Each service provides a status endpoint:

```bash
curl http://localhost:8085/api/payment/status
curl http://localhost:8086/api/notification/status
curl http://localhost:8087/api/inventory/status
```

### Logs
Services use Log4j2 for logging. Check console output for:
- Request/response details at DEBUG level
- Service errors and exceptions

### Database Queries
Connect to PostgreSQL and verify data:

```sql
-- Check payments
SELECT * FROM payment;

-- Check notifications
SELECT * FROM notification;

-- Check inventory
SELECT * FROM inventory;
```

---

## Technology Stack

| Component | Version |
|-----------|---------|
| Spring Boot | 4.0.3 |
| Java | 21 |
| Spring Cloud | 2025.1.0 |
| JWT (jjwt) | 0.11.5 |
| PostgreSQL | 13+ |
| Hibernate | Latest (via Spring Boot) |
| Maven | 3.11.0 |
| Lombok | 1.18.30 |
| MapStruct | 1.5.5 |

---

## Notes

- **JWT Secret:** All services share the same JWT secret for token interoperability
- **Database:** Each service has its own PostgreSQL database (database-per-service pattern)
- **Mock Implementations:** 
  - Payment: 95% success rate mock gateway
  - Notification: 100ms delay mock sending
- **Stock Calculation:** Available quantity = quantityAvailable - quantityReserved (calculated, not persisted)

---

## Support & Troubleshooting

### 403 Forbidden Errors
- Verify JWT token is valid and not expired
- Ensure Authorization header format: `Bearer <token>`
- Check that endpoint is not restricted to public-only

### Database Connection Errors
- Verify PostgreSQL is running
- Check database exists and credentials are correct
- Review `application.yaml` datasource configuration

### Service Startup Issues
- Ensure all required dependencies are installed (run `mvn install`)
- Check Java version is 21+
- Verify port numbers are not in use by other services

