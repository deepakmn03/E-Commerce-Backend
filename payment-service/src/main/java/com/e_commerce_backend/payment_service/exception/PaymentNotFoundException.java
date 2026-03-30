package com.e_commerce_backend.payment_service.exception;

public class PaymentNotFoundException extends EntityNotFoundException {
    
    public PaymentNotFoundException(Long paymentId) {
        super("Payment not found with ID: " + paymentId);
    }
    
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
