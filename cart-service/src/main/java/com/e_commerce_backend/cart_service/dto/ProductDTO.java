package com.e_commerce_backend.cart_service.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductDTO {
    private Long productId;
    private String name;
    private BigDecimal price;
    private Boolean isActive;
    private Long availableQuantity;
}
