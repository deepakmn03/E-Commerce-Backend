package com.e_commerce_backend.cart_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.e_commerce_backend.cart_service.dto.ProductDTO;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/product/internal/{productId}")
    ProductDTO getProductById(@PathVariable("productId") Long productId);

    @PostMapping("/api/product/internal/reserve/{productId}/{quantity}")
    Boolean reserveStock(@PathVariable("productId") Long productId, @PathVariable("quantity") Long quantity);

    @PostMapping("/api/product/internal/release/{productId}/{quantity}")
    Void releaseStock(@PathVariable("productId") Long productId, @PathVariable("quantity") Long quantity);
}
