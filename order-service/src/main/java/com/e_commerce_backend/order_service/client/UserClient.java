package com.e_commerce_backend.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.e_commerce_backend.order_service.dto.UserDTO;

@FeignClient(name = "user-service", url = "http://localhost:8082")
public interface UserClient {
    
    @GetMapping("/api/user/get/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long userId);
}
