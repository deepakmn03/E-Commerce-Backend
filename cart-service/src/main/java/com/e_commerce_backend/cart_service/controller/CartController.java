package com.e_commerce_backend.cart_service.controller;

import com.e_commerce_backend.cart_service.dto.AddToCartRequestDTO;
import com.e_commerce_backend.cart_service.dto.CartResponseDTO;
import com.e_commerce_backend.cart_service.dto.UpdateCartItemRequestDTO;
import com.e_commerce_backend.cart_service.service.CartService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Cart service is live!");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable Long userId) {
        log.info("Getting cart for user: {}", userId);
        CartResponseDTO cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/user/{userId}/add")
    public ResponseEntity<CartResponseDTO> addToCart(
            @PathVariable Long userId,
            @Valid @RequestBody AddToCartRequestDTO request) {
        log.info("Adding item to cart for user: {}", userId);
        CartResponseDTO cart = cartService.addToCart(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(cart);
    }

    @PatchMapping("/user/{userId}/item/{itemId}")
    public ResponseEntity<CartResponseDTO> updateCartItem(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequestDTO request) {
        log.info("Updating cart item {} for user: {}", itemId, userId);
        CartResponseDTO cart = cartService.updateCartItem(userId, itemId, request);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/user/{userId}/item/{itemId}")
    public ResponseEntity<CartResponseDTO> removeCartItem(
            @PathVariable Long userId,
            @PathVariable Long itemId) {
        log.info("Removing item {} from cart for user: {}", itemId, userId);
        CartResponseDTO cart = cartService.removeCartItem(userId, itemId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/user/{userId}/clear")
    public ResponseEntity<CartResponseDTO> clearCart(@PathVariable Long userId) {
        log.info("Clearing cart for user: {}", userId);
        CartResponseDTO cart = cartService.clearCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/user/{userId}/checkout")
    public ResponseEntity<CartResponseDTO> checkout(@PathVariable Long userId) {
        log.info("Checking out cart for user: {}", userId);
        CartResponseDTO cart = cartService.checkout(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartResponseDTO> getCartById(@PathVariable Long cartId) {
        log.info("Getting cart by ID: {}", cartId);
        CartResponseDTO cart = cartService.getCartById(cartId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/user/{userId}/delete/{cartId}")
    public ResponseEntity<String> deleteCart(
            @PathVariable Long userId,
            @PathVariable Long cartId) {
        log.info("Deleting cart {} for user: {}", cartId, userId);
        cartService.deleteCart(userId, cartId);
        return ResponseEntity.ok("Cart deleted successfully");
    }
}
