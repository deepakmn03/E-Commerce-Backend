package com.e_commerce_backend.inventory_service.exception;

public class InventoryNotFoundException extends EntityNotFoundException {
    
    public InventoryNotFoundException(Long inventoryId) {
        super("Inventory not found with ID: " + inventoryId);
    }
    
    public InventoryNotFoundException(String message) {
        super(message);
    }
}
