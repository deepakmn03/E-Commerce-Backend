package com.e_commerce_backend.cart_service.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
}
