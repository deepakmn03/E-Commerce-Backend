# JWT Implementation for Order Service

## ✅ Implementation Complete

JWT authentication has been successfully implemented for the Order Service, matching the user-service configuration.

---

## 📦 What Was Added

### 1. **Dependencies** (pom.xml)
- `spring-boot-starter-security` - Spring Security framework
- `jjwt-api` (v0.11.5) - JWT API
- `jjwt-impl` (v0.11.5) - JWT Implementation
- `jjwt-jackson` (v0.11.5) - Jackson integration for JWT

### 2. **Configuration** (application.yaml)
```yaml
jwt:
  secret: mySecretKeyForJWTTokenGenerationAndValidation12345
  expiration: 86400000  # 24 hours in milliseconds
```

### 3. **Security Components**

#### **JwtUtil.java** - Token Management
- `generateToken(String email)` - Creates JWT token valid for 24 hours
- `getEmailFromToken(String token)` - Extracts email from token
- `validateToken(String token)` - Validates token signature and expiration

#### **JwtAuthenticationFilter.java** - Request Interceptor
- Intercepts all HTTP requests
- Extracts JWT from `Authorization: Bearer <token>` header
- Validates token and sets user context in Spring Security

#### **SecurityConfig.java** - Security Configuration
- Public endpoints: `/api/order/status`
- Protected endpoints: All other `/api/order/**` paths
- Disables CSRF for stateless authentication
- Uses stateless session management
- Adds JWT filter to security filter chain

---

## 🔐 How It Works

### Request Flow
```
1. Client sends request with Authorization header:
   Authorization: Bearer <jwt-token>

2. JwtAuthenticationFilter intercepts request

3. Filter validates JWT token

4. If valid, extracts email and sets it in SecurityContext

5. Request proceeds to controller

6. If invalid or missing, returns 401 Unauthorized
```

### Example Request
```bash
curl -X GET http://localhost:8083/api/order/get/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 📋 Protected vs Public Endpoints

| Endpoint | Auth Required | Purpose |
|----------|---|---------|
| `GET /api/order/status` | ❌ No | Health check |
| `GET /api/order/get/{orderId}` | ✅ Yes | Get order by ID |
| `GET /api/order/get/user/{userId}` | ✅ Yes | Get orders by user |
| `GET /api/order/get` | ✅ Yes | Get all orders |
| `POST /api/order/create` | ✅ Yes | Create new order |
| `PATCH /api/order/update/{orderId}` | ✅ Yes | Update order |
| `DELETE /api/order/delete/{orderId}` | ✅ Yes | Delete order |

---

## 🚀 How to Get a JWT Token

Since the Order Service only validates tokens (doesn't generate them), you need to:

### Option 1: Get token from User Service
```bash
curl -X POST http://localhost:8082/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful",
  "email": "user@example.com"
}
```

### Option 2: Use the returned token in Order Service requests
```bash
curl -X GET http://localhost:8083/api/order/get/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 🔒 Security Features

✅ **Token Expiration** - Tokens expire after 24 hours
✅ **HS256 Signature** - HMAC-SHA256 algorithm for token signing
✅ **Stateless** - No server-side session storage
✅ **Microservice Compatible** - Same secret key used across services
✅ **CSRF Protection** - Disabled for stateless JWT auth
✅ **Password Security** - Uses BCryptPasswordEncoder

---

## 📝 Important Notes

1. **Shared Secret Key**: Both user-service and order-service use the same JWT secret for token validation
   - Default: `mySecretKeyForJWTTokenGenerationAndValidation12345`
   - Change in production to a strong, unique key

2. **Token Generation**: Order Service only **validates** tokens from the User Service
   - Tokens are generated in User Service during login
   - Order Service validates them on incoming requests

3. **Email as Subject**: JWT tokens use email as the subject identifier
   - When token is decoded, email is available in `SecurityContext`

4. **24-hour Expiration**: Tokens are valid for 24 hours (86400000 ms)
   - Users need to login again after expiration

---

## 🧪 Testing the Implementation

### Test 1: Access public endpoint (should work without token)
```bash
curl -X GET http://localhost:8083/api/order/status
```
Expected: ✅ 200 OK

### Test 2: Access protected endpoint without token (should fail)
```bash
curl -X GET http://localhost:8083/api/order/get/1
```
Expected: ❌ 401 Unauthorized

### Test 3: Access protected endpoint with valid token (should work)
```bash
# First get token from user service
TOKEN=$(curl -X POST http://localhost:8082/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "password"}' | jq -r '.token')

# Then use it on order service
curl -X GET http://localhost:8083/api/order/get/1 \
  -H "Authorization: Bearer $TOKEN"
```
Expected: ✅ 200 OK with order data

---

## 🔧 Configuration Guide

If you need to change JWT settings, update in `application.yaml`:

```yaml
jwt:
  secret: "change-this-to-a-strong-secret-key-in-production"
  expiration: 86400000  # milliseconds (24 hours = 86400000)
```

⚠️ **Production Security Tips:**
- Use environment variables for the secret key
- Use a strong, random secret (at least 32 characters)
- Use the same secret across all microservices
- Consider using a key management service (KMS)
- Implement token refresh mechanism for better security

---

## 📚 Files Created

```
src/main/java/com/e_commerce_backend/order_service/
├── security/
│   ├── JwtUtil.java                    # Token generation and validation
│   └── JwtAuthenticationFilter.java    # Request interceptor
└── config/
    └── SecurityConfig.java             # Spring Security configuration

src/main/resources/
└── application.yaml                    # JWT configuration
```

---

## ✨ Next Steps

1. **Test the implementation** using the curl commands above
2. **Monitor logs** for any JWT validation errors
3. **Set strong secret key** in production environment
4. **Implement token refresh** for better user experience (optional enhancement)
5. **Add audit logging** for security compliance (optional)

---

## 📞 Troubleshooting

| Issue | Solution |
|-------|----------|
| 401 Unauthorized on protected endpoints | Ensure token is passed in `Authorization: Bearer <token>` header |
| Token validation failures | Check that jwt.secret is the same across both services |
| Expired token errors | User needs to login again to get a new token |
| Missing JWT config | Ensure application.yaml has jwt.secret and jwt.expiration properties |

---

**Implementation Date:** March 24, 2026  
**Status:** ✅ Complete and Ready for Testing
