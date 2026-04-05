package com.e_commerce_backend.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "product-service")
public interface ProductClient {

    @PostMapping("/api/product/internal/deduct/{productId}/{quantity}")
    Boolean deductStock(@PathVariable("productId") Long productId, @PathVariable("quantity") Long quantity);
}
