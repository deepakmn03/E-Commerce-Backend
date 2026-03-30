# Cart Service - Complete Documentation

## Overview
The Cart Service is a microservice that manages shopping carts for e-commerce users. It handles cart creation, item management, and cart operations such as adding, updating, and removing items.

## Service Configuration
- **Port:** 8004
- **Base URL:** `http://localhost:8004`
- **Context Path:** `/`
- **Database:** PostgreSQL (ecommerce_cart_db)

## Architecture

### Key Components

#### 1. Entity Layer
- **Cart** - Represents a shopping cart with items and metadata
- **CartItem** - Represents individual items in a cart
- **CartStatus** - Enum with states: ACTIVE, COMPLETED, ABANDONED

#### 2. Data Transfer Objects (DTOs)
- **CartResponseDTO** - Response object for cart queries
- **CartItemDTO** - Response object for cart items
- **AddToCartRequestDTO** - Request to add items to cart
- **UpdateCartItemRequestDTO** - Request to update item quantity

#### 3. Repository Layer
- **CartRepository** - JPA repository for Cart operations
- **CartItemRepository** - JPA repository for CartItem operations

#### 4. Service Layer
- **CartService** - Business logic for cart operations

#### 5. Controller Layer
- **CartController** - REST API endpoints

#### 6. Mapper Layer
- **CartMapper** - MapStruct mapper for entity-DTO conversions

## Database Schema

### Cart Table
```sql
CREATE TABLE carts (
    cart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_price DECIMAL(19,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
);
```

### Cart Items Table
```sql
CREATE TABLE cart_items (
    item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(19,2) NOT NULL,
    added_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(cart_id) ON DELETE CASCADE,
    INDEX idx_cart_id (cart_id),
    INDEX idx_product_id (product_id)
);
```

## API Endpoints

### Service Status
```http
GET /api/cart/status
```
Returns service health status.

**Response:**
```json
"Cart service is live!"
```

---

### Get Cart for User
```http
GET /api/cart/user/{userId}
```
Retrieves the active cart for a user or creates a new one if it doesn't exist.

**Path Parameters:**
- `userId` (required) - The user ID

**Response (200 OK):**
```json
{
  "cartId": 1,
  "userId": 1,
  "status": "ACTIVE",
  "totalPrice": 299.98,
  "itemCount": 3,
  "items": [
    {
      "itemId": 1,
      "productId": 1,
      "productName": "Product 1",
      "quantity": 2,
      "unitPrice": 99.99,
      "subtotal": 199.98
    }
  ],
  "createdAt": "2026-03-30T10:00:00",
  "updatedAt": "2026-03-30T10:15:00"
}
```

---

### Add Item to Cart
```http
POST /api/cart/user/{userId}/add
```
Adds an item to the user's active cart or updates quantity if item already exists.

**Path Parameters:**
- `userId` (required) - The user ID

**Request Body:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

**Response (200 OK):** Returns updated cart

---

### Update Cart Item Quantity
```http
PATCH /api/cart/user/{userId}/item/{itemId}
```
Updates the quantity of an existing cart item.

**Path Parameters:**
- `userId` (required) - The user ID
- `itemId` (required) - The cart item ID

**Request Body:**
```json
{
  "quantity": 5
}
```

**Response (200 OK):** Returns updated cart

---

### Remove Item from Cart
```http
DELETE /api/cart/user/{userId}/item/{itemId}
```
Removes an item from the cart.

**Path Parameters:**
- `userId` (required) - The user ID
- `itemId` (required) - The cart item ID

**Response (200 OK):** Returns updated cart

---

### Get Cart by ID
```http
GET /api/cart/{cartId}
```
Retrieves a specific cart by ID.

**Path Parameters:**
- `cartId` (required) - The cart ID

**Response (200 OK):** Returns cart details

---

### Clear Cart
```http
POST /api/cart/user/{userId}/clear
```
Removes all items from the user's active cart.

**Path Parameters:**
- `userId` (required) - The user ID

**Response (200 OK):** Returns empty cart

---

### Checkout Cart
```http
POST /api/cart/user/{userId}/checkout
```
Marks the cart as completed (checkout).

**Path Parameters:**
- `userId` (required) - The user ID

**Response (201 Created):** Returns completed cart with COMPLETED status

---

### Delete Cart
```http
DELETE /api/cart/user/{userId}/delete/{cartId}
```
Deletes a cart and all its items.

**Path Parameters:**
- `userId` (required) - The user ID
- `cartId` (required) - The cart ID

**Response (200 OK):**
```json
"Cart deleted successfully"
```

---

## Features

### Cart Management
- Create cart automatically on first add
- Manage multiple cart items
- Track total price and item count
- Support for cart statuses (ACTIVE, COMPLETED, ABANDONED)

### Item Management
- Add items with quantity
- Update item quantities
- Remove individual items
- Auto-merge when adding duplicate products

### Business Logic
- Automatic total price calculation
- Timestamp tracking (created, updated)
- User-specific cart isolation
- Transaction support for data consistency

## Error Handling

The service handles the following error scenarios:

| Error | HTTP Status | Message |
|-------|------------|---------|
| Cart not found | 404 | Cart not found: {cartId} |
| Item not found | 404 | Cart item not found: {itemId} |
| Unauthorized access | 403 | Unauthorized: Item does not belong to user |
| Empty cart checkout | 400 | Cannot checkout empty cart |
| Invalid quantity | 400 | Quantity must be at least 1 |

## Dependencies

- Spring Boot 4.0.5
- Spring Data JPA
- PostgreSQL Driver
- Lombok
- MapStruct 1.5.5
- Spring Security
- Spring Cloud OpenFeign
- JWT (JJWT)
- Log4j2
- Jakarta Validation

## Running the Service

### Prerequisites
- Java 21
- Maven 3.6+
- PostgreSQL

### Build
```bash
cd cart-service
./mvnw clean compile
```

### Run
```bash
./mvnw spring-boot:run
```

### Database Setup
Ensure PostgreSQL is running and create the database:
```sql
CREATE DATABASE ecommerce_cart_db;
```

## Testing

Use the provided Postman collection: `CART_SERVICE_API.postman_collection.json`

### Quick Test Flow
1. Get cart for user (creates if not exists)
2. Add items to cart
3. View cart items
4. Update quantities
5. Remove items
6. Checkout

## Configuration

Update `application.yaml` to customize:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommerce_cart_db
    username: postgres
    password: root
  jpa:
    hibernate:
      ddl-auto: update

server:
  port: 8004
```

## Integration Points

- **Product Service** - For product details and pricing
- **Order Service** - For order creation during checkout
- **User Service** - For user validation and authentication

## Future Enhancements

- [ ] Integration with Product Service for real-time pricing
- [ ] Integration with Order Service for checkout
- [ ] Cart abandonment notifications
- [ ] Wish list functionality
- [ ] Cart sharing between users
- [ ] Promotional code/coupon support
- [ ] Cart persistence across sessions
- [ ] Analytics and cart metrics
