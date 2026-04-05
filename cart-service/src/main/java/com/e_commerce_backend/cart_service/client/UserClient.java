package com.e_commerce_backend.cart_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.e_commerce_backend.cart_service.dto.UserDTO;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/user/internal/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long userId);
}
