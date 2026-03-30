package com.e_commerce_backend.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequestDTO {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Quantity available is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantityAvailable;
    
    @NotNull(message = "Reorder level is required")
    @Positive(message = "Reorder level must be positive")
    private Integer reorderLevel;
}
