package com.e_commerce_backend.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login Response DTO
 * Returns JWT token and user information after successful authentication
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    
    private String token;
    private String message;
    private Long userId;
    private String email;
}
