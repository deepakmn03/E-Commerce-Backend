# JWT Authentication - Quick Reference

## 🚀 Quick Start

### 1. Build & Run
```bash
cd user-service
mvn clean compile
mvn spring-boot:run
```

### 2. Create Account
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
    "city": "NYC",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  }'
```

### 3. Login (Get Token)
```bash
TOKEN=$(curl -s -X POST http://localhost:8082/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"email": "john@example.com", "password": "SecurePass123"}' \
  | jq -r '.token')

echo $TOKEN  # Copy this token
```

### 4. Use Token to Access Protected Endpoint
```bash
curl -X GET http://localhost:8082/api/user/get/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📋 API Endpoints

### Public (No Auth)
```
POST   /api/user/create         → Register user
POST   /api/user/login          → Get JWT token
GET    /api/user/status         → Health check
```

### Protected (JWT Required)
```
GET    /api/user/get/{id}              → Get user by ID
GET    /api/user/get/email/{email}     → Get user by email
GET    /api/user/get                   → Get all users
PATCH  /api/user/update/{id}           → Update user
DELETE /api/user/remove/{id}           → Delete user
GET    /api/user/exists/{id}           → Check if exists
```

---

## 🔑 JWT Token Format

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNjc2Nzc3MjAwLCJleHAiOjE2NzY4NjM2MDB9.signature

Components:
├── Bearer: Fixed prefix
└── Token: JWT (valid for 24 hours)
```

---

## 📝 Login Response Example

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful",
  "userId": 1,
  "email": "john@example.com"
}
```

**Token Details:**
- Algorithm: HS256
- Expiration: 24 hours from login
- Subject: User email
- Signature: Cannot be forged

---

## 🛠️ Postman Setup

### Step 1: Create User
- **Method**: POST
- **URL**: `http://localhost:8082/api/user/create`
- **Body** (JSON): User details
- **Auth**: None

### Step 2: Login
- **Method**: POST
- **URL**: `http://localhost:8082/api/user/login`
- **Body** (JSON): `{"email": "...", "password": "..."}`
- **Auth**: None
- **Copy Response**: Save the `token` value

### Step 3: Use Token
- **Method**: GET
- **URL**: `http://localhost:8082/api/user/get/1`
- **Auth Tab**:
  - Type: `Bearer Token`
  - Token: `Paste your token here`

---

## ⚙️ Configuration (application.yaml)

```yaml
jwt:
  secret: mySecretKeyForJWTTokenGenerationAndValidation12345
  expiration: 86400000  # 24 hours in ms
```

**For Production:**
```bash
# Generate new secret (run once)
openssl rand -base64 32

# Output:
# AbCdEfGhIjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrSt==
```

---

## 🔐 Error Codes

| Code | Error | Fix |
|------|-------|-----|
| 400 | Invalid email/password | Check credentials |
| 401 | Unauthorized | Add token to header |
| 401 | Token expired | Login again |
| 401 | Invalid token | Copy token correctly |
| 404 | User not found | Create user first |

---

## 📂 New Files Created

```
user-service/
├── src/main/java/.../security/
│   ├── JwtUtil.java                    # Token generation
│   └── JwtAuthenticationFilter.java     # Request interceptor
├── src/main/java/.../dto/
│   ├── LoginRequestDTO.java            # Login input
│   └── LoginResponseDTO.java           # Login output
└── src/main/resources/
    └── application.yaml                # JWT config
```

---

## 🔄 Modified Files

```
✏️  pom.xml                             # Added JWT dependencies
✏️  config/SecurityConfig.java          # JWT security setup
✏️  service/UserService.java            # Login method
✏️  controller/UserController.java      # Login endpoint
✏️  application.yaml                    # JWT configuration
```

---

## 📊 Authentication Flow

```
User → Login → Validate Password → Generate JWT → Return Token
                                       ↓
                            eyJhbGciOiJIUzI1NiIs...
                                       ↓
User + Token → Protected Endpoint → Validate Token → Return Data
                                      ↓
                            Extract Email → Set in Context
```

---

## 🧪 Test Commands

### Create User
```bash
curl -X POST http://localhost:8082/api/user/create \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","email":"test@test.com","password":"Test123","phone":"9999999999","address":"123 St","city":"NY","state":"NY","zipCode":"10001","country":"USA"}'
```

### Login
```bash
curl -X POST http://localhost:8082/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"Test123"}'
```

### Get User (Replace TOKEN)
```bash
TOKEN="your_token_here"
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8082/api/user/get/1
```

### Get All Users (Replace TOKEN)
```bash
TOKEN="your_token_here"
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8082/api/user/get
```

---

## ✅ Verification Checklist

- [ ] User can register via `/api/user/create`
- [ ] User can login via `/api/user/login` and receives JWT
- [ ] Token starts with `eyJ` (Base64 encoded)
- [ ] Protected endpoints reject requests without token (401)
- [ ] Protected endpoints work with valid token (200)
- [ ] Password is stored encrypted in DB
- [ ] Expired tokens are rejected (after 24 hours)
- [ ] Token cannot be modified/forged
- [ ] Multiple logins work and produce different tokens

---

## 🚨 Troubleshooting

| Problem | Solution |
|---------|----------|
| 401 on login | Verify email/password exists |
| 401 on protected endpoint | Include Bearer token in header |
| Token not working | Token might be expired, login again |
| Can't create user | Check all required fields |
| Password fails | Password is case-sensitive |
| Import errors | Run `mvn clean compile` first |

---

## 🎯 Next Steps

1. ✅ Test all endpoints with Postman
2. ✅ Verify token expires after 24 hours
3. ✅ Test with expired token (should get 401)
4. ✅ Test password validation
5. ⏳ Implement refresh token mechanism
6. ⏳ Add role-based access control
7. ⏳ Deploy to production

---

## 📚 Documentation Files

- **JWT_AUTHENTICATION_GUIDE.md** → Complete usage guide
- **JWT_IMPLEMENTATION_NOTES.md** → Technical details
- **README.md** → Project overview

---

**Your JWT authentication is ready! 🎉**
