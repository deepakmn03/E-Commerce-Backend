package com.e_commerce_backend.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.e_commerce_backend.order_service.dto.CartResponseDTO;

@FeignClient(name = "cart-service")
public interface CartClient {

    @GetMapping("/api/cart/internal/users/{userId}/active")
    CartResponseDTO getActiveCartForUser(@PathVariable("userId") Long userId);

    @PostMapping("/api/cart/internal/users/{userId}/checkout")
    CartResponseDTO checkout(@PathVariable("userId") Long userId);
}
