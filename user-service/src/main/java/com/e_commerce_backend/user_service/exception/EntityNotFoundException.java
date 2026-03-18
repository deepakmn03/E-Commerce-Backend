package com.e_commerce_backend.user_service.exception;

import org.springframework.validation.BindException;

public abstract class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message){
        super(message);
    }

    protected abstract BindException getBindingResult();   
}
