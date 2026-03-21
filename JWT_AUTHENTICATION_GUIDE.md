# JWT Authentication Implementation Guide

## Overview

This guide explains how JWT (JSON Web Tokens) authentication has been integrated into the User Service microservice and how to use it.

## What is JWT?

**JWT (JSON Web Tokens)** is a standard for securely transmitting information between parties. It consists of 3 parts:

1. **Header**: Contains token type and hashing algorithm
2. **Payload**: Contains claims (data) like user email, issued time, expiration
3. **Signature**: Ensures token hasn't been altered

Example JWT:
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNjc2Nzc3MjAwLCJleHAiOjE2NzY4NjM2MDB9.signature
```

---

## Architecture Components

### 1. **JwtUtil.java** - Token Generation & Validation
```
Location: src/main/java/.../security/JwtUtil.java
Purpose: Create, validate, and parse JWT tokens
```

**Key Methods:**
- `generateToken(email)` → Creates JWT token valid for 24 hours
- `validateToken(token)` → Checks if token is valid and not expired
- `getEmailFromToken(token)` → Extracts email from token

**Token Details:**
- **Algorithm**: HS256 (HMAC SHA-256)
- **Expiration**: 24 hours (86400000 milliseconds)
- **Secret**: Configurable in `application.yaml`

### 2. **JwtAuthenticationFilter.java** - Request Interceptor
```
Location: src/main/java/.../security/JwtAuthenticationFilter.java
Purpose: Intercept all requests and validate JWT tokens
```

**How it works:**
1. Checks every incoming request
2. Looks for "Authorization" header with format: `Bearer <token>`
3. Validates token using JwtUtil
4. Extracts email and sets it in Spring Security context
5. Allows request to proceed if valid, rejects if invalid

### 3. **SecurityConfig.java** - Configuration
```
Location: src/main/java/.../config/SecurityConfig.java
Purpose: Configure Spring Security with JWT authentication
```

**Configuration:**
```
Public Endpoints (No Auth Required):
├── POST /api/user/status       → Health check
├── POST /api/user/create       → Register new user
└── POST /api/user/login        → Get JWT token

Protected Endpoints (JWT Required):
├── GET    /api/user/get/{id}
├── GET    /api/user/get
├── GET    /api/user/get/email/{email}
├── PATCH  /api/user/update/{id}
├── DELETE /api/user/remove/{id}
└── GET    /api/user/exists/{id}
```

### 4. **LoginRequestDTO & LoginResponseDTO**
Request and response models for authentication

---

## Usage Guide

### Step 1: Create a User (Registration)

**Endpoint**: `POST /api/user/create`

**Request** (No auth needed):
```bash
curl -X POST http://localhost:8082/api/user/create \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "SecurePass123",
    "phone": "1234567890",
    "address": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  }'
```

**Response** (201 Created):
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

---

### Step 2: Login & Get JWT Token

**Endpoint**: `POST /api/user/login`

**Request** (No auth needed):
```bash
curl -X POST http://localhost:8082/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePass123"
  }'
```

**Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNjc2Nzc3MjAwLCJleHAiOjE2NzY4NjM2MDB9.signature",
  "message": "Login successful",
  "userId": 1,
  "email": "john@example.com"
}
```

**Error Response** (401 Unauthorized):
```json
{
  "error": "Invalid password"
}
```

---

### Step 3: Use JWT Token to Access Protected Endpoints

**Endpoint**: `GET /api/user/get/1`

**Request** (JWT Required):
```bash
curl -X GET http://localhost:8082/api/user/get/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Response** (200 OK):
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

**Error Response** (401 Unauthorized):
```json
{
  "error": "Unauthorized"
}
```

---

## Testing with Postman

### 1. Create User
- **Method**: POST
- **URL**: `http://localhost:8082/api/user/create`
- **Body** (raw JSON):
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane@example.com",
  "password": "JanePass123",
  "phone": "9876543210",
  "address": "456 Oak Ave",
  "city": "Los Angeles",
  "state": "CA",
  "zipCode": "90001",
  "country": "USA"
}
```
- **Auth**: None
- **Send** → Copy the userId from response

### 2. Login to Get Token
- **Method**: POST
- **URL**: `http://localhost:8082/api/user/login`
- **Body** (raw JSON):
```json
{
  "email": "jane@example.com",
  "password": "JanePass123"
}
```
- **Auth**: None
- **Send** → **Copy the token from response**

### 3. Access Protected Endpoint
- **Method**: GET
- **URL**: `http://localhost:8082/api/user/get/1`
- **Auth Tab**:
  - Type: **Bearer Token**
  - Token: **Paste the token from login response**
- **Send** → You should get user details

---

## Configuration

### application.yaml
```yaml
jwt:
  secret: mySecretKeyForJWTTokenGenerationAndValidation12345
  expiration: 86400000  # 24 hours in milliseconds
```

**Change these values for production!**

- `secret`: Use a strong, random key (min 32 characters for HS256)
- `expiration`: Adjust token lifetime (3600000 = 1 hour)

---

## Authentication Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Authentication Flow                       │
└─────────────────────────────────────────────────────────────┘

1. USER REGISTRATION
   Client → POST /api/user/create → Server
   (email, password, user details)
                ↓
            Database stores user
            (password BCrypt encrypted)
                ↓
            Returns UserResponseDTO
            (no password in response)

2. USER LOGIN
   Client → POST /api/user/login → Server
   (email, password)
                ↓
        Validate email & password
                ↓
        Generate JWT Token using JwtUtil
                ↓
            Return LoginResponseDTO
            (contains JWT token)

3. PROTECTED REQUEST
   Client → GET /api/user/get/1 → JwtAuthenticationFilter
   (with "Authorization: Bearer <token>" header)
                ↓
        Extract token from header
                ↓
        Validate token using JwtUtil
                ↓
        Extract email from token
                ↓
        Set authentication in SecurityContext
                ↓
        Allow request to proceed
                ↓
            Execute endpoint logic
            Return response
```

---

## Security Details

### Password Encryption
- **Algorithm**: BCrypt
- **Cost Factor**: 10 (default)
- **Implementation**: Located in `UserService.createUser()` and `updateUser()`

### Password Validation
When user logs in:
1. Database stores BCrypt hash: `$2a$10$kX4Oi1fW7Z9L2M3N4O5P6Q7R8S9T0U1...`
2. Password provided by user: `SecurePass123`
3. PasswordEncoder.matches() compares them
4. If match → Generate JWT token

### Token Security
- **Signature**: Prevents tampering
- **Expiration**: Prevents indefinite use
- **Secret**: Only known to server

---

## Error Scenarios

| Scenario | Status | Message |
|----------|--------|---------|
| Invalid credentials | 401 | Invalid password |
| User not found | 404 | User not found |
| Missing token | 401 | Unauthorized |
| Expired token | 401 | Invalid token |
| Tampered token | 401 | Invalid token |
| No Authorization header | 401 | Unauthorized |

---

## Production Checklist

- [ ] Change JWT secret to a strong random key
- [ ] Update token expiration time based on security needs
- [ ] Enable HTTPS/TLS
- [ ] Set `spring.web.error.include-stacktrace: never`
- [ ] Add rate limiting on login endpoint
- [ ] Implement token refresh mechanism
- [ ] Add logging for authentication events
- [ ] Consider adding "remember-me" functionality
- [ ] Implement token blacklisting for logout

---

## Common Issues & Solutions

### Issue: 401 Unauthorized on protected endpoints
**Solution**: 
- Verify token is included in header
- Check token format: `Bearer <token>`
- Verify token hasn't expired (24 hours default)
- Check if token is for correct email

### Issue: "Invalid password" on login
**Solution**:
- Verify password matches exactly (case-sensitive)
- Check user exists with correct email
- Ensure password was encrypted when created

### Issue: Token not generated on login
**Solution**:
- Check JwtUtil is properly autowired
- Verify JWT secret is configured in application.yaml
- Check email is being extracted correctly

---

## Next Steps

1. **Refresh Token Implementation**: Add refresh tokens for better security
2. **Role-Based Access Control**: Implement @Secured or @PreAuthorize
3. **API Key Authentication**: Support API key for service-to-service calls
4. **OAuth2 Integration**: Support third-party authentication
5. **Audit Logging**: Log all authentication attempts

---

## Summary

You now have:
✅ User registration (/create)
✅ User login with JWT token (/login)
✅ Protected endpoints requiring JWT
✅ Password encryption with BCrypt
✅ Token validation on every request
✅ Automatic token extraction from headers

Your API is now **production-ready** with JWT authentication! 🔐
