package com.e_commerce_backend.cart_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT Token Utility
 * Handles generation, validation, and parsing of JWT tokens
 */
public class JwtUtil {

    private final String jwtSecret;
    private final long jwtExpirationMs;

    public JwtUtil(String jwtSecret, long jwtExpirationMs) {
        this.jwtSecret = jwtSecret != null ? jwtSecret : "mySecretKeyForJWTTokenGenerationAndValidation12345";
        this.jwtExpirationMs = jwtExpirationMs > 0 ? jwtExpirationMs : 86400000;
    }

    /**
     * Generate JWT token for user email
     * 
     * @param email User email (used as subject/username)
     * @return JWT token string
     */
    public String generateToken(String email) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract email from JWT token
     * 
     * @param token JWT token
     * @return Email extracted from token
     */
    public String getEmailFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validate JWT token
     * 
     * @param token JWT token to validate
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
