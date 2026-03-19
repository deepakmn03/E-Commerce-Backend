package com.e_commerce_backend.order_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private int orderId;
    private Long orderValue;
    
}