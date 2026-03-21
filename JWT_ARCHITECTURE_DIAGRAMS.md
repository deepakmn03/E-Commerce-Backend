# JWT Architecture & Flow Diagrams

## 1. High-Level JWT Authentication Flow

```
┌────────────────────────────────────────────────────────────────────┐
│                     JWT AUTHENTICATION FLOW                         │
└────────────────────────────────────────────────────────────────────┘

STEP 1: USER REGISTRATION
════════════════════════════════════════════════════════════════════
Client                              Server
  │                                   │
  │──── POST /api/user/create ───────→│
  │     {firstName, lastName,         │
  │      email, password, phone...}   │
  │                                   │
  │                           ┌──────────────────┐
  │                           │ Validate Input   │
  │                           │ Hash Password    │
  │                           │ Store in DB      │
  │                           └──────────────────┘
  │                                   │
  │←─── 201 CREATED ─────────────────│
  │     {userId, firstName...}        │
  │     (no password)                 │
  │                                   │

STEP 2: USER LOGIN
════════════════════════════════════════════════════════════════════
Client                              Server
  │                                   │
  │──── POST /api/user/login ────────→│
  │     {email, password}             │
  │                                   │
  │                           ┌──────────────────┐
  │                           │ Find user        │
  │                           │ Check password   │
  │                           │ Generate JWT     │
  │                           └──────────────────┘
  │                                   │
  │←─── 200 OK ──────────────────────│
  │     {token, userId, email...}     │
  │     token = eyJhbGc...            │
  │                                   │
  │  (Save token in memory/storage)   │
  │                                   │

STEP 3: ACCESS PROTECTED ENDPOINT
════════════════════════════════════════════════════════════════════
Client                              Server
  │                                   │
  │──── GET /api/user/get/1 ────────→│
  │     Header:                       │
  │     Authorization:                │
  │     Bearer eyJhbGc...             │
  │                                   │
  │                           ┌──────────────────┐
  │                           │ JwtFilter        │
  │                           │ Extract token    │
  │                           │ Validate sig     │
  │                           │ Check exp        │
  │                           │ Extract email    │
  │                           │ Set context      │
  │                           └──────────────────┘
  │                                   │
  │                           ┌──────────────────┐
  │                           │ Controller       │
  │                           │ Get user data    │
  │                           │ Return 200       │
  │                           └──────────────────┘
  │                                   │
  │←─── 200 OK ──────────────────────│
  │     {userId, firstName...}        │
  │                                   │
```

---

## 2. JWT Token Structure

```
┌─────────────────────────────────────────────────────────────────┐
│                        JWT TOKEN                                 │
└─────────────────────────────────────────────────────────────────┘

Token = Header . Payload . Signature

┌──────────────────────────────────────────────────────────────────┐
│ HEADER (Base64URL encoded)                                       │
├──────────────────────────────────────────────────────────────────┤
│ {                                                                │
│   "alg": "HS256",      ← Algorithm (HMAC SHA-256)               │
│   "typ": "JWT"         ← Type                                   │
│ }                                                                │
│                                                                  │
│ Encoded: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9                    │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│ PAYLOAD (Base64URL encoded)                                      │
├──────────────────────────────────────────────────────────────────┤
│ {                                                                │
│   "sub": "john@example.com",    ← User identifier (email)       │
│   "iat": 1676777200,             ← Issued At (Unix timestamp)   │
│   "exp": 1676863600              ← Expiration (24 hrs later)    │
│ }                                                                │
│                                                                  │
│ Encoded: eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNjc2Nzc3... │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│ SIGNATURE                                                        │
├──────────────────────────────────────────────────────────────────┤
│ HMACSHA256(                                                      │
│   base64UrlEncode(header) + "." +                               │
│   base64UrlEncode(payload),                                     │
│   "mySecretKeyForJWTTokenGenerationAndValidation12345"          │
│ )                                                                │
│                                                                  │
│ Encoded: SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c             │
└──────────────────────────────────────────────────────────────────┘

COMPLETE JWT:
═════════════════════════════════════════════════════════════════════
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNjc2Nzc3MjAwLCJleHAiOjE2NzY4NjM2MDB9.
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

---

## 3. Token Validation Process

```
┌──────────────────────────────────────────────────────────────┐
│              JWT TOKEN VALIDATION FLOW                        │
└──────────────────────────────────────────────────────────────┘

Client Request
      │
      ├─ Header: Authorization: Bearer <token>
      │
      ↓
JwtAuthenticationFilter.doFilterInternal()
      │
      ├─→ getJwtFromRequest()
      │   Regex: Authorization = "Bearer " + token
      │   Extract: "eyJhbGciOiJ..."
      │
      ↓
jwtUtil.validateToken(token)
      │
      ├─→ Split token by "."
      │   [header, payload, signature]
      │
      ├─→ Parse with JwtParser
      │   Provide signing key: secret
      │
      ├─→ Verify Signature ─────────┐
      │                             │
      │   Compute: HMACSHA256(      │ Match?
      │     header.payload,         │
      │     secret                  ├─→ ✗ Invalid
      │   )                         │   401 Unauthorized
      │                             │
      │   Compare with token sig   ┤─→ ✓ Valid
      │                             │
      └─────────────────────────────┘
      │
      ↓
if (signature valid) {
      │
      ├─→ Check Expiration
      │   exp claim > current time?
      │
      ├─→ ✗ Expired → 401
      │
      ├─→ ✓ Valid → Continue
      │
      ↓
      getEmailFromToken()
      Extract: subject = "john@example.com"
      │
      ↓
      Set Authentication in SecurityContext
      │
      ├─ UsernamePasswordAuthenticationToken
      ├─ Principal: "john@example.com"
      ├─ Credentials: null
      ├─ Authorities: []
      │
      ↓
      filterChain.doFilter() → Continue to Controller
}
```

---

## 4. Request/Response Cycle

```
┌───────────────────────────────────────────────────────────────┐
│          HTTP REQUEST/RESPONSE CYCLE WITH JWT                 │
└───────────────────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════
PUBLIC ENDPOINT (POST /api/user/login)
═══════════════════════════════════════════════════════════════════

REQUEST:
────────────────────────────────────────────────────────────────
POST /api/user/login HTTP/1.1
Host: localhost:8082
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePass123"
}

PROCESSING:
────────────────────────────────────────────────────────────────
1. SecurityFilterChain: No auth required (permitAll)
2. UserController.login() called
3. UserService.login() executed:
   ├─ Find user by email
   ├─ BCryptPasswordEncoder.matches(password)
   ├─ JwtUtil.generateToken(email)
   └─ Return LoginResponseDTO
4. Response serialized to JSON

RESPONSE (200 OK):
────────────────────────────────────────────────────────────────
HTTP/1.1 200 OK
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful",
  "userId": 1,
  "email": "john@example.com"
}

═══════════════════════════════════════════════════════════════════
PROTECTED ENDPOINT (GET /api/user/get/1)
═══════════════════════════════════════════════════════════════════

REQUEST:
────────────────────────────────────────────────────────────────
GET /api/user/get/1 HTTP/1.1
Host: localhost:8082
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

PROCESSING:
────────────────────────────────────────────────────────────────
1. JwtAuthenticationFilter:
   ├─ Extract token from Authorization header
   ├─ Validate token (signature + expiration)
   ├─ Extract email from token
   └─ Set in SecurityContext
2. SecurityFilterChain: Auth required (authenticated)
   ├─ Check SecurityContext has authentication
   ├─ ✓ Present → Continue
   ├─ ✗ Missing → 401 Unauthorized
3. UserController.getUserById(1)
4. UserService.getUserById(1):
   ├─ Query database by ID
   ├─ Return UserResponseDTO
5. Response serialized to JSON

RESPONSE (200 OK):
────────────────────────────────────────────────────────────────
HTTP/1.1 200 OK
Content-Type: application/json

{
  "userId": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "phone": "1234567890",
  "address": "123 Main St",
  "city": "NYC",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA"
}

═══════════════════════════════════════════════════════════════════
ERROR: NO TOKEN (GET /api/user/get/1)
═══════════════════════════════════════════════════════════════════

REQUEST:
────────────────────────────────────────────────────────────────
GET /api/user/get/1 HTTP/1.1
Host: localhost:8082
(No Authorization header)

PROCESSING:
────────────────────────────────────────────────────────────────
1. JwtAuthenticationFilter:
   ├─ getJwtFromRequest() → null
   ├─ No token found
   └─ No authentication set
2. SecurityFilterChain: Auth required
   ├─ Check SecurityContext
   ├─ No authentication present
   └─ Deny access
3. Response 401 Unauthorized

RESPONSE (401 UNAUTHORIZED):
────────────────────────────────────────────────────────────────
HTTP/1.1 401 Unauthorized
Content-Type: application/json

{
  "error": "Unauthorized"
}

═══════════════════════════════════════════════════════════════════
ERROR: INVALID/EXPIRED TOKEN (GET /api/user/get/1)
═══════════════════════════════════════════════════════════════════

REQUEST:
────────────────────────────────────────────────────────────────
GET /api/user/get/1 HTTP/1.1
Host: localhost:8082
Authorization: Bearer eyJhbGciOiJIUzI1NiJhHBCd...  ← Invalid/Expired

PROCESSING:
────────────────────────────────────────────────────────────────
1. JwtAuthenticationFilter:
   ├─ Extract token
   ├─ jwtUtil.validateToken() → false
      └─ Signature mismatch OR expired
   └─ No authentication set
2. SecurityFilterChain: Auth required
   ├─ No authentication
   └─ Deny access
3. Response 401 Unauthorized

RESPONSE (401 UNAUTHORIZED):
────────────────────────────────────────────────────────────────
HTTP/1.1 401 Unauthorized
Content-Type: application/json

{
  "error": "Unauthorized"
}
```

---

## 5. Component Interaction Diagram

```
┌──────────────────────────────────────────────────────────────┐
│           SPRING SECURITY COMPONENT ARCHITECTURE              │
└──────────────────────────────────────────────────────────────┘

HTTP Request
    │
    ↓
┌─────────────────────────────────────────┐
│  SecurityFilterChain                    │
│  ├─ CsrfFilter (disabled)               │
│  ├─ JwtAuthenticationFilter ◄━━━┓       │
│  │  ├─ Extract JWT from header  │       │
│  │  ├─ Validate with JwtUtil    │       │
│  │  └─ Set SecurityContext      │       │
│  └─ FilterSecurityInterceptor   │       │
│     ├─ Check authorization      │       │
│     └─ Allow/Deny               │       │
└─────────────────────────────────────────┘
    │                              │
    ↓                              │
   YES ◄─ Is public endpoint? ─────┤
    │   (permitAll)                │
    ↓                              │
  Controller                  Validate
    │                         Signature
    ├─ UserController         Check Exp
    │   ├─ POST /login        Extract
    │   ├─ GET /get/{id}      Email
    │   ├─ POST /create       │
    │   └─ ...                │
    │                         │
    ↓                         ↓
  UserService         SecurityContext
    ├─ login()        ├─ Set Principal
    ├─ getUserById()  ├─ Set Authorities
    ├─ createUser()   └─ Store Auth
    └─ ...                  Token
    │
    ↓
  Database
    ├─ users table
    └─ [userId, email, password_hash, ...]
    │
    ↓
  Response (JSON)


┌──────────────────────────────────────────┐
│  JwtUtil (Singleton Bean)                │
├──────────────────────────────────────────┤
│ @Component                               │
│                                          │
│ - jwtSecret = "...123..."                │
│ - jwtExpirationMs = 86400000             │
│                                          │
│ Methods:                                 │
│ + generateToken(email): String           │
│ + validateToken(token): boolean          │
│ + getEmailFromToken(token): String       │
│                                          │
│ Uses:                                    │
│ - Jwts (JJWT library)                   │
│ - Keys (HMAC key generation)            │
│ - SignatureAlgorithm.HS256               │
└──────────────────────────────────────────┘


┌──────────────────────────────────────────┐
│  JwtAuthenticationFilter (extends Filter)│
├──────────────────────────────────────────┤
│ @Component                               │
│                                          │
│ - jwtUtil: JwtUtil (autowired)           │
│                                          │
│ Methods:                                 │
│ # doFilterInternal(request, response,   │
│   chain): void                           │
│   - Extracts token                       │
│   - Validates token                      │
│   - Sets authentication                  │
│   - Calls filterChain.doFilter()        │
│                                          │
│ - getJwtFromRequest(request): String     │
│   - Gets "Authorization" header         │
│   - Extracts "Bearer <token>"            │
└──────────────────────────────────────────┘


┌──────────────────────────────────────────┐
│  SecurityConfig (@Configuration)         │
├──────────────────────────────────────────┤
│ Bean: SecurityFilterChain                │
│   - CSRF: disabled                       │
│   - Session: STATELESS                   │
│   - Filter: JwtAuthenticationFilter      │
│   - Public: /user/create, /user/login    │
│   - Protected: /user/get, etc            │
│                                          │
│ Bean: PasswordEncoder                    │
│   - BCryptPasswordEncoder                │
│                                          │
│ Bean: AuthenticationManager              │
│   - DaoAuthenticationProvider            │
│   - Uses: UserDetailsService             │
│   - Uses: PasswordEncoder                │
└──────────────────────────────────────────┘
```

---

## 6. Security Decision Tree

```
┌────────────────────────────────────────────────────────────┐
│            REQUEST AUTHORIZATION DECISION                  │
└────────────────────────────────────────────────────────────┘

           Incoming Request
                  │
                  ↓
          Is public endpoint?
          (permitAll list)
          ├─ /api/user/status
          ├─ /api/user/create
          └─ /api/user/login
                  │
          YES    │    NO
          │      │      │
          ↓      │      ↓
        ALLOW    │    Does request have
                 │    Authorization header?
                 │      │
                 │   YES│    NO
                 │   │  │      │
                 │   │  ↓      ↓
                 │   │ Extract │ Return
                 │   │ Token   │ 401
                 │   │   │     │
                 │   ↓   ↓     │
                 │ Is token valid?     │
                 │ (signature OK)      │
                 │      │              │
                 │   YES│    NO         │
                 │   │  │      │       │
                 │   │  ↓      ↓       │
                 │   │ Is token │ Return
                 │   │ expired? │ 401
                 │   │ │        │
                 │   YES│    NO │
                 │   │  │      │
                 │   │  ↓      ↓
                 │   │Return  Extract
                 │   │401     Email
                 │   │        │
                 │   │        ↓
                 │   │    Set
                 │   │    SecurityContext
                 │   │      │
                 │   │      ↓
                 │   │    ALLOW
                 │   │    (Continue to
                 │   │     Controller)
                 │   │
                 └───┴─→ Send Response
                        (200 or error)
```

---

## 7. Password Security Flow

```
┌──────────────────────────────────────────────────┐
│      PASSWORD HASHING & VALIDATION               │
└──────────────────────────────────────────────────┘

REGISTRATION (POST /api/user/create)
═══════════════════════════════════════════════════════════════

Client sends password:     "SecurePass123"
                                │
                                ↓
                    PasswordEncoder.encode()
                    (BCryptPasswordEncoder)
                                │
              ┌─────────────────┴──────────────────┐
              │  BCrypt Algorithm                  │
              ├──────────────────────────────────┐
              │ - Generate random salt           │
              │ - Apply salt + password          │
              │ - Hash iteratively (cost=10)     │
              │ - Create 60-char hash            │
              └──────────────────────────────────┘
                                │
                                ↓
Hashed password: "$2a$10$kX4Oi1fW7Z9L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z6A7B8C9D0E1F2"
                                │
                                ↓
                    Store in database
                    ├─ userId: 1
                    ├─ email: "john@example.com"
                    └─ password: "$2a$10$..." (hash only)


LOGIN (POST /api/user/login)
═══════════════════════════════════════════════════════════════

Client sends password:     "SecurePass123"
                                │
                                ↓
    Retrieve from DB:     "$2a$10$kX4Oi1fW7..." (hash)
                                │
              ┌─────────────────┴──────────────────┐
              │  PasswordEncoder.matches()         │
              ├──────────────────────────────────┐
              │ 1. Extract salt from stored hash │
              │ 2. Hash provided password        │
              │    with extracted salt           │
              │ 3. Compare with stored hash      │
              │ 4. Return true/false             │
              └──────────────────────────────────┘
                                │
                    Match?      │
                  /─────────────┘
                 /
          YES  /    NO
         /        \
        ↓          ↓
    Generate   Return 401
    JWT Token  "Invalid
        │      Password"
        ↓
    Return Token
    +
    {token, userId, email...}


SECURITY BENEFITS
═══════════════════════════════════════════════════════════════

✓ Passwords NEVER stored in plain text
✓ Each password has unique salt
✓ Iterative hashing (slow, prevents brute force)
✓ Even if DB breached, passwords safe
✓ Can't reverse hash (one-way function)
✓ Same password hashes differently each time
  (due to unique salt)
```

---

## 8. Microservice Integration

```
┌────────────────────────────────────────────────────────┐
│     MICROSERVICE COMMUNICATION WITH JWT                │
└────────────────────────────────────────────────────────┘

SCENARIO: Order Service creates order, validates user

Client
  │
  ├─→ POST /api/order/create
  │   Header: Authorization: Bearer <token>
  │   Body: {userId: 1, orderValue: 199.99}
  │
  ↓
Order Service (Port 8083)
  │
  ├─ JwtAuthenticationFilter validates token
  ├─ Extract email from token
  ├─ Set in SecurityContext
  │
  ├─ OrderController.createOrder()
  │
  ├─ OrderService.createOrder()
  │   ├─ Has userId: 1
  │   ├─ Need to verify user exists
  │   │
  │   ├─ Call UserClient (Feign)
  │   │ (Service-to-service, NO JWT needed)
  │   │
  │   ↓
  │ User Service (Port 8082)
  │   ├─ UserClient.getUserById(1)
  │   ├─ (Internal call, trusted)
  │   └─ Return UserDTO
  │   │
  │   ↓ (No auth needed)
  │
  ├─ Validate user exists
  ├─ Create order in DB
  ├─ Return OrderResponseDTO
  │
  ↓
Client receives order


IMPORTANT NOTES:
═══════════════════════════════════════════════════════════════

✓ Client-to-Order: JWT required
  (Authorization: Bearer <token>)

✓ Order-to-User: NO JWT required
  (Internal service-to-service call)
  (Happens on trusted network)
  (Uses Feign client directly)

✓ Each service validates JWT independently
  (If separate JWT secrets, will fail)
  → Solution: Share JWT secret or use OAuth2
```

---

This comprehensive visual guide covers all aspects of JWT authentication in your microservices!
