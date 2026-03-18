package com.e_commerce_backend.order_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class OrderNotFoundException extends EntityNotFoundException {

    public OrderNotFoundException(int id) {
        super("Order not found with Order ID: " + id);
    }

    @Override
    protected BindException getBindingResult() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBindingResult'");
    }
}

