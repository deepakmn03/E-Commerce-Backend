package com.e_commerce_backend.order_service.exception;

public class OrderNotFoundException extends EntityNotFoundException {

    public OrderNotFoundException(Long orderId) {
        super("Order not found with orderId: " + orderId);
    }

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

