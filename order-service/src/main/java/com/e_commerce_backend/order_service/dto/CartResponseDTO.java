package com.e_commerce_backend.order_service.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class CartResponseDTO {
    private Long id;
    private Long userId;
    private String status;
    private BigDecimal totalPrice;
    private List<CartItemDTO> items;
    private int itemCount;
}
