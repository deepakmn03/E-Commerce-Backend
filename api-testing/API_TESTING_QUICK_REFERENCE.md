# API Testing Quick Reference

## Your New API Testing Setup

### Files Created

| File | Purpose | Size |
|------|---------|------|
| `docs/api/openapi.yaml` | Complete API specification (1,138 lines) | 35 KB |
| `requests.http` | 80+ manual test requests | 20 KB |
| `API_TESTING_GUIDE.md` | Comprehensive strategy guide | 18 KB |
| `API_TESTING_SETUP_COMPLETE.md` | Setup summary & next steps | 12 KB |
| `.env.example` | Configuration template | 2 KB |
| `.gitignore` | Updated secret protection | 1 KB |

### Files Removed

All 8 Postman collection JSON files removed (as requested in Message 17):
- ❌ CART_SERVICE_API.postman_collection.json
- ❌ INVENTORY_SERVICE_Postman_Collection.json
- ❌ NEW_SERVICES_Postman_Collection.json
- ❌ NOTIFICATION_SERVICE_Postman_Collection.json
- ❌ Order_Service_Bulk_Upload_20Orders.postman_collection.json
- ❌ PAYMENT_SERVICE_Postman_Collection.json
- ❌ PRODUCT_SERVICE_API.postman_collection.json
- ❌ User_Service_Bulk_Upload.postman_collection.json

---

## How to Use

### 1. REST Client Testing (Recommended for Development)

```bash
# Install VS Code Extension
# VS Code → Extensions → Search "REST Client"
# Install by Huachao Mao

# Open requests.http
# Click "Send Request" above any test
# View response in panel
```

**Example request in requests.http:**

```http
### Login
POST {{baseUrl}}/api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

> {%
  client.global.set("token", response.body.token);
%}

### Get Products
GET {{baseUrl}}/api/v1/products
Authorization: Bearer {{token}}
```

### 2. View API Documentation

```bash
# Go to: https://editor.swagger.io/
# Paste content of: docs/api/openapi.yaml
# Get beautiful interactive API docs
```

### 3. Generate Postman Collection (Optional)

```bash
# If you prefer Postman UI
npm install -g openapi-postman
openapi-postman -i docs/api/openapi.yaml -o postman_collection.json

# Import into Postman:
# - Open Postman
# - Click "Import"
# - Select postman_collection.json
```

### 4. Setup Environment Variables

```bash
# Copy template
cp .env.example .env

# Edit .env with your values
# (Never commit .env)
```

---

## Key Differences: Old vs New

### Old Approach (Removed)
```
8 Postman JSON files
  ↓
Merge conflicts
  ↓
Manual testing only
  ↓
Hard to automate
```

### New Approach (Now Active)
```
1 OpenAPI specification
  ↓
Clean version control
  ↓
Multiple testing tools
  ↓
CI/CD ready
```

---

## API Endpoints Overview

### Authentication
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/refresh` - Refresh token

### Users
- `GET /api/v1/users` - List users (admin)
- `GET /api/v1/users/{id}` - Get user
- `PUT /api/v1/users/{id}` - Update user
- `DELETE /api/v1/users/{id}` - Delete user (admin)

### Products
- `GET /api/v1/products` - List products
- `POST /api/v1/products` - Create product (admin)
- `GET /api/v1/products/{id}` - Get product
- `PUT /api/v1/products/{id}` - Update product (admin)
- `DELETE /api/v1/products/{id}` - Delete product (admin)

### Shopping Cart
- `GET /api/v1/carts` - Get cart
- `POST /api/v1/carts/items` - Add to cart
- `PUT /api/v1/carts/items/{itemId}` - Update quantity
- `DELETE /api/v1/carts/items/{itemId}` - Remove item
- `DELETE /api/v1/carts/clear` - Clear cart

### Orders
- `GET /api/v1/orders` - List orders
- `POST /api/v1/orders` - Create order
- `GET /api/v1/orders/{id}` - Get order
- `PUT /api/v1/orders/{id}` - Update order status (admin)
- `POST /api/v1/orders/{id}/cancel` - Cancel order

### Payments
- `POST /api/v1/payments` - Process payment
- `GET /api/v1/payments/{id}` - Get payment
- `POST /api/v1/payments/{id}/refund` - Refund (admin)

### Inventory
- `GET /api/v1/inventory` - List inventory (admin)
- `GET /api/v1/inventory/product/{productId}` - Get product inventory
- `PUT /api/v1/inventory/{id}` - Update inventory (admin)

### Notifications
- `GET /api/v1/notifications` - Get notifications
- `PUT /api/v1/notifications/{id}` - Mark as read
- `DELETE /api/v1/notifications/{id}` - Delete notification
- `PUT /api/v1/notifications/read-all` - Mark all as read

### System
- `GET /health` - Health check
- `GET /health/live` - Liveness probe
- `GET /health/ready` - Readiness probe

---

## Environment Variables Needed

```bash
# Copy from .env.example and fill in:

API_URL=http://localhost:8080
API_PORT=8080
JWT_SECRET=your-secret-key-at-least-32-chars
JWT_EXPIRATION=86400000

# Database URLs (update ports for each service)
USER_SERVICE_DB_HOST=localhost
USER_SERVICE_DB_PORT=5432
USER_SERVICE_DB_NAME=user_db
USER_SERVICE_DB_USER=postgres
USER_SERVICE_DB_PASSWORD=postgres

# Service URLs
EUREKA_SERVER_URL=http://localhost:8761/eureka
USER_SERVICE_URL=http://localhost:8082
PRODUCT_SERVICE_URL=http://localhost:8084
ORDER_SERVICE_URL=http://localhost:8083

# Testing
TEST_USER_EMAIL=testuser@example.com
TEST_USER_PASSWORD=TestPassword123!
ENVIRONMENT=development
```

---

## Common Tasks

### Task 1: Login and Get Token

```http
POST {{baseUrl}}/api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

> {%
  client.global.set("token", response.body.token);
%}
```

### Task 2: List Products

```http
GET {{baseUrl}}/api/v1/products?page=1&limit=20
Authorization: Bearer {{token}}
```

### Task 3: Create Product (Admin)

```http
POST {{baseUrl}}/api/v1/products
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 1299.99,
  "stock": 50,
  "category": "electronics",
  "sku": "LAPTOP-001"
}
```

### Task 4: Add to Cart

```http
POST {{baseUrl}}/api/v1/carts/items
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "productId": 1,
  "quantity": 2
}
```

### Task 5: Create Order

```http
POST {{baseUrl}}/api/v1/orders
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "shippingAddress": "123 Main St, City, State 12345"
}
```

### Task 6: Process Payment

```http
POST {{baseUrl}}/api/v1/payments
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "orderId": 1,
  "amount": 299.98,
  "paymentMethod": "CREDIT_CARD",
  "cardToken": "tok_visa_4242424242424242"
}
```

---

## Best Practices

✅ **Do:**
- Use `{{baseUrl}}` and `{{token}}` variables
- Store requests in `requests.http` (version control)
- Use `.env.example` for configuration template
- Never commit `.env` with secrets
- Run tests frequently
- Keep API spec updated

❌ **Don't:**
- Commit `.env` files
- Store credentials in requests.http
- Commit Postman JSON files
- Use hardcoded URLs
- Skip test documentation
- Forget to update OpenAPI spec

---

## Testing Workflow

1. **Setup**: Copy `.env.example` to `.env`, fill in values
2. **Read Guide**: Open `API_TESTING_GUIDE.md` for strategy
3. **Manual Test**: Use `requests.http` for quick tests
4. **View Docs**: Paste `openapi.yaml` to Swagger Editor
5. **Share**: Commit to Git (except `.env`)
6. **Automate**: Setup Jest tests (Phase 2)
7. **CI/CD**: Add GitHub Actions workflow (Phase 2)

---

## Troubleshooting

### Q: REST Client extension not sending requests?
A: Install "REST Client" by Huachao Mao, reload VS Code, click "Send Request" link

### Q: Getting 401 Unauthorized?
A: 1) Login first to get token, 2) Use {{token}} in Authorization header, 3) Token may be expired

### Q: Can't find variables like {{baseUrl}}?
A: Add to top of requests.http:
```http
@baseUrl = http://localhost:8080
@token = your_jwt_token_here
```

### Q: How do I share with team?
A: 1) Commit `requests.http`, 2) Each team member copies `.env.example` to `.env`, 3) Everyone uses same requests

---

## Next Steps (Optional)

- [x] Remove Postman files ✅
- [x] Create OpenAPI spec ✅
- [x] Setup REST Client ✅
- [ ] Add automated tests (Jest)
- [ ] Setup GitHub Actions
- [ ] Generate Postman collection
- [ ] Create API documentation website
- [ ] Add test coverage reports
- [ ] Setup API monitoring

---

## Resources

- **REST Client Extension**: https://marketplace.visualstudio.com/items?itemName=humao.rest-client
- **OpenAPI 3.0 Spec**: https://spec.openapis.org/oas/v3.0.3
- **Swagger Editor**: https://editor.swagger.io/
- **Full Guide**: See `API_TESTING_GUIDE.md`

---

Quick Start: Open `requests.http` in VS Code, click "Send Request", done! 🚀
