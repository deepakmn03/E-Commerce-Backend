package com.e_commerce_backend.cart_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/api/inventory/internal/check/{productId}/{quantity}")
    Boolean isStockAvailable(@PathVariable("productId") Long productId, @PathVariable("quantity") Integer quantity);

    @PostMapping("/api/inventory/internal/reserve/{productId}/{quantity}")
    Boolean reserveStock(@PathVariable("productId") Long productId, @PathVariable("quantity") Integer quantity);

    @PostMapping("/api/inventory/internal/release/{productId}/{quantity}")
    Void releaseStock(@PathVariable("productId") Long productId, @PathVariable("quantity") Integer quantity);
}
