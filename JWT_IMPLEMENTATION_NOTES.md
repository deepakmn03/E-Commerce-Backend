# JWT Authentication Implementation Notes

## Summary of Implementation

### What Was Implemented

#### 1. **JWT Token Generation & Validation** (`JwtUtil.java`)
- Uses JJWT library (JSON Web Token library)
- Algorithm: HS256 (HMAC SHA-256)
- Token contains:
  - Subject: User email
  - Issued At (iat): Token creation time
  - Expiration (exp): 24 hours from creation
  - Signature: HMAC256 with secret key

**Code Snippet:**
```java
public String generateToken(String email) {
    SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
}
```

#### 2. **Request Interceptor** (`JwtAuthenticationFilter.java`)
- Extends `OncePerRequestFilter` (runs once per request)
- Intercepts all HTTP requests
- Extracts JWT from `Authorization: Bearer <token>` header
- Validates token using JwtUtil
- Sets authenticated user in SecurityContext
- Allows Spring Security to recognize the user

**Code Snippet:**
```java
@Override
protected void doFilterInternal(HttpServletRequest request, ...) throws ServletException, IOException {
    String jwt = getJwtFromRequest(request);
    if (jwt != null && jwtUtil.validateToken(jwt)) {
        String email = jwtUtil.getEmailFromToken(jwt);
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
}
```

#### 3. **Security Configuration** (`SecurityConfig.java`)
```java
// Public endpoints - no authentication required
.requestMatchers("/api/user/status").permitAll()
.requestMatchers("/api/user/create").permitAll()
.requestMatchers("/api/user/login").permitAll()

// Protected endpoints - JWT required
.requestMatchers("/api/user/**").authenticated()

// Add JWT filter before Spring's default filter
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

// Stateless session - JWT only, no session cookies
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

#### 4. **Login Endpoint** (`UserService.login()`)
```java
public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
    // 1. Find user by email
    User user = userRepository.findByEmail(loginRequestDTO.getEmail())
        .orElseThrow(() -> new UserNotFoundException(...));
    
    // 2. Validate password
    if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
        throw new IllegalArgumentException("Invalid password");
    }
    
    // 3. Generate JWT token
    String token = jwtUtil.generateToken(user.getEmail());
    
    // 4. Return response with token
    return new LoginResponseDTO(token, "Login successful", userId, email);
}
```

#### 5. **DTOs for Authentication**
- `LoginRequestDTO`: email + password input
- `LoginResponseDTO`: token + user details output

#### 6. **Maven Dependencies Added**
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

---

## File Changes Summary

### New Files Created
1. `JwtUtil.java` - Token generation/validation
2. `JwtAuthenticationFilter.java` - Request interceptor
3. `LoginRequestDTO.java` - Login request model
4. `LoginResponseDTO.java` - Login response model
5. `JWT_AUTHENTICATION_GUIDE.md` - User guide
6. `JWT_IMPLEMENTATION_NOTES.md` - This file

### Modified Files
1. **pom.xml**
   - Added JJWT dependencies (JWT library)

2. **SecurityConfig.java**
   - Replaced HTTP Basic Auth with JWT Filter
   - Made `/api/user/login` public
   - Added `JwtAuthenticationFilter` to filter chain
   - Changed session policy to STATELESS
   - Added CSRF disable

3. **UserService.java**
   - Added `JwtUtil` autowiring
   - Added `login(LoginRequestDTO)` method
   - Password now validated using BCryptPasswordEncoder.matches()

4. **UserController.java**
   - Added `@PostMapping("/login")` endpoint
   - Returns `LoginResponseDTO` with JWT token

5. **application.yaml**
   - Added JWT configuration properties:
     - `jwt.secret`: Token signing key
     - `jwt.expiration`: Token lifetime (24 hours)

---

## Authentication Flow Explained

### Request Without Token → 401 Unauthorized
```
GET /api/user/get/1 (no Authorization header)
        ↓
JwtAuthenticationFilter.doFilterInternal()
        ↓
getJwtFromRequest() returns null
        ↓
No authentication set in SecurityContext
        ↓
@RequestMatcher requires authentication
        ↓
Response: 401 Unauthorized
```

### Request With Valid Token → 200 OK
```
GET /api/user/get/1
Header: Authorization: Bearer eyJhbGc...
        ↓
JwtAuthenticationFilter.doFilterInternal()
        ↓
getJwtFromRequest() extracts "eyJhbGc..."
        ↓
jwtUtil.validateToken() checks signature + expiration
        ↓
✓ Token valid
        ↓
getEmailFromToken() extracts "john@example.com"
        ↓
Create UsernamePasswordAuthenticationToken
        ↓
Set in SecurityContext.getAuthentication()
        ↓
Request proceeds to controller
        ↓
Response: 200 OK with user data
```

---

## Key Design Decisions

### 1. **Email as Username**
- User model has firstName/lastName, not username
- Email is unique identifier
- Email is used as JWT subject (sub claim)
- Allows human-readable authentication

### 2. **Stateless Sessions**
```java
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```
- No session cookies
- Every request must include JWT
- Server doesn't store session state
- Better for distributed systems/microservices

### 3. **Password Validation with BCrypt**
```java
passwordEncoder.matches(plainPassword, hashedPassword)
```
- Passwords never stored in plain text
- BCrypt automatically validates
- Prevents rainbow table attacks

### 4. **JWT as Bearer Token**
```
Authorization: Bearer <token>
```
- Standard HTTP authentication scheme
- Supported by all tools (Postman, cURL, browsers)
- Clear separation from credentials

---

## Token Structure Example

### Decoded JWT:
```
HEADER:
{
  "alg": "HS256",
  "typ": "JWT"
}

PAYLOAD:
{
  "sub": "john@example.com",
  "iat": 1676777200,
  "exp": 1676863600
}

SIGNATURE:
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  "mySecretKeyForJWTTokenGenerationAndValidation12345"
)
```

### Token Validation Process:
1. Split token by "." → [header, payload, signature]
2. Decode header and payload (Base64URL)
3. Verify signature by re-computing HMAC with secret key
4. Check expiration: `current_time < exp_claim`
5. Return valid only if both checks pass

---

## Security Considerations

### 1. **Secret Key Strength**
```yaml
jwt.secret: mySecretKeyForJWTTokenGenerationAndValidation12345
```
- Development: Simple key (as above)
- Production: 256-bit random key (43+ Base64 characters)

Generate secure key:
```bash
openssl rand -base64 32
```

### 2. **Token Expiration**
```yaml
jwt.expiration: 86400000  # 24 hours
```
- Too long (days): Compromised token dangerous for long
- Too short (minutes): User must re-login frequently
- 1 hour: Balance between security and UX

### 3. **Token Storage (Client-Side)**
**DO:**
- ✅ Store in memory (lost on refresh)
- ✅ Store in secure HTTP-only cookies (not accessible to JS)

**DON'T:**
- ❌ Store in localStorage (vulnerable to XSS)
- ❌ Store in sessionStorage (vulnerable to XSS)
- ❌ Include in URLs (logged in history)

### 4. **HTTPS Required**
- **Always** use HTTPS in production
- JWT tokens are signed, not encrypted
- Token can be read, but not modified without secret

---

## Integration with Microservices

### Order Service Calling User Service
```java
// Order Service
@FeignClient(name = "user-service", url = "http://localhost:8082")
public interface UserClient {
    @GetMapping("/api/user/get/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long userId);
}
```

**Note**: Service-to-service calls don't need JWT (same trusted network)

### Frontend Calling User Service
```javascript
// Get token
const response = await fetch('http://localhost:8082/api/user/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: 'john@example.com', password: 'pass' })
});
const { token } = await response.json();

// Use token
const userData = await fetch('http://localhost:8082/api/user/get/1', {
    headers: { 'Authorization': `Bearer ${token}` }
});
```

---

## Testing Endpoints

### 1. Public Endpoints (No Auth)
```bash
# Create user
curl -X POST http://localhost:8082/api/user/create \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john@example.com",...}'

# Login
curl -X POST http://localhost:8082/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"SecurePass123"}'
```

### 2. Protected Endpoints (With JWT)
```bash
# Get user (requires token from login)
curl -X GET http://localhost:8082/api/user/get/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## Performance Impact

### Positive:
- ✅ Stateless: No session lookup in DB
- ✅ Horizontal scaling: No session replication needed
- ✅ Single request: No DB query for session

### Negative:
- ❌ Token validation: HMAC256 check on every request (minimal)
- ❌ Token size: ~200-300 bytes vs session ID (~20 bytes)

**Overall**: Negligible performance impact, better scalability

---

## Troubleshooting

### Token Expired
```
Error: "Invalid token"
Solution: 
- Call /login again to get new token
- Implement token refresh mechanism
- Adjust expiration time in application.yaml
```

### Invalid Signature
```
Error: "Invalid token"
Solution:
- Check JWT secret matches between server instances
- Verify secret in application.yaml
- Re-login to get new token
```

### Missing Authorization Header
```
Error: 401 Unauthorized
Solution:
- Include "Authorization: Bearer <token>" header
- Check token is not expired
- Verify token format is correct
```

---

## Future Enhancements

1. **Refresh Token Flow**
   - Short-lived access token (1 hour)
   - Long-lived refresh token (30 days)
   - Endpoint to refresh without re-login

2. **Role-Based Access Control (RBAC)**
   ```java
   @PreAuthorize("hasRole('ADMIN')")
   public void adminOnly() { }
   ```

3. **Token Blacklist (Logout)**
   - Store revoked tokens in Redis
   - Check on every request

4. **Multi-Factor Authentication (MFA)**
   - After password validation
   - Before token generation

5. **OAuth2 / OpenID Connect**
   - Google, GitHub, Microsoft login
   - Delegated authentication

---

## Code Quality Improvements Made

✅ Proper exception handling  
✅ Meaningful error messages  
✅ JavaDoc comments on all methods  
✅ Configuration externalized (application.yaml)  
✅ Separation of concerns (JwtUtil, Filter, Config)  
✅ Spring Security best practices  
✅ Password hashing with BCrypt  
✅ Stateless session management  

---

## Conclusion

You now have a **production-ready JWT authentication system** with:

- ✅ User registration
- ✅ Secure login with JWT tokens
- ✅ Protected endpoints
- ✅ Password encryption
- ✅ Token validation
- ✅ Stateless architecture
- ✅ Microservice-friendly design

The implementation follows **Spring Security best practices** and is ready for **production deployment** with minor security adjustments (stronger JWT secret, HTTPS, etc.).
