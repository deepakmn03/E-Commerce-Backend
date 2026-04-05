package com.e_commerce_backend.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PutMapping("/api/inventory/internal/deduct/{productId}/{quantity}")
    Void deductStock(@PathVariable("productId") Long productId, @PathVariable("quantity") Integer quantity);
}
