package com.e_commerce_backend.notification_service.exception;

public class NotificationNotFoundException extends EntityNotFoundException {
    
    public NotificationNotFoundException(Long notificationId) {
        super("Notification not found with ID: " + notificationId);
    }
    
    public NotificationNotFoundException(String message) {
        super(message);
    }
}
