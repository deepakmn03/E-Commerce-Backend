package com.e_commerce_backend.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    private String jwtSecret;
    private int jwtExpiration;
}