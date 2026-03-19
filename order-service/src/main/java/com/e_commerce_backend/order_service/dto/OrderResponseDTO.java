package com.e_commerce_backend.order_service.dto;

import java.time.LocalDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private Long orderId;
    private Long userId;
    private Double orderValue;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}