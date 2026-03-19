package com.e_commerce_backend.user_service.exception;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(int userId) {
        super("User not found with userId: " + userId);
    }

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}

