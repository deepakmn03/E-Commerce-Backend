package com.e_commerce_backend.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    
    private Long productId;
    private String sku;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private Long stockQuantity;
    private Long reservedQuantity;
    private Long availableQuantity;
    private Boolean isActive;
    private String imageUrl;
    private Double rating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}