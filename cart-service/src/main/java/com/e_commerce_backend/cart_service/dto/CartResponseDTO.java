package com.e_commerce_backend.cart_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private Long cartId;
    private Long userId;
    private String status;
    private BigDecimal totalPrice;
    private List<CartItemDTO> items;
    private int itemCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
