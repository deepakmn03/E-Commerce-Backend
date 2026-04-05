package com.e_commerce_backend.order_service.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
