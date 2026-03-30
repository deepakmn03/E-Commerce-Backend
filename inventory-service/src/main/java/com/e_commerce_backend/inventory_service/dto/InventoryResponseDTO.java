package com.e_commerce_backend.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseDTO {
    
    private Long inventoryId;
    
    private Long productId;
    
    private Integer quantityAvailable;
    
    private Integer quantityReserved;
    
    private Integer availableQuantity;
    
    private Integer reorderLevel;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
