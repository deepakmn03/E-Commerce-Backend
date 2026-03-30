package com.e_commerce_backend.inventory_service.exception;

public class InsufficientStockException extends EntityNotFoundException {
    
    public InsufficientStockException(Long productId, Integer requested, Integer available) {
        super("Insufficient stock for product " + productId + ". Requested: " + requested + ", Available: " + available);
    }
    
    public InsufficientStockException(String message) {
        super(message);
    }
}
