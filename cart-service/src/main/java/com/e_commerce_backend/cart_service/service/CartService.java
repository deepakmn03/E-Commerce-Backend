package com.e_commerce_backend.cart_service.service;

import com.e_commerce_backend.cart_service.dto.AddToCartRequestDTO;
import com.e_commerce_backend.cart_service.dto.CartResponseDTO;
import com.e_commerce_backend.cart_service.dto.UpdateCartItemRequestDTO;
import com.e_commerce_backend.cart_service.entity.Cart;
import com.e_commerce_backend.cart_service.entity.CartItem;
import com.e_commerce_backend.cart_service.entity.CartStatus;
import com.e_commerce_backend.cart_service.mapper.CartMapper;
import com.e_commerce_backend.cart_service.repository.CartItemRepository;
import com.e_commerce_backend.cart_service.repository.CartRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartMapper cartMapper;

    /**
     * Get or create user's active cart
     */
    public CartResponseDTO getCart(Long userId) {
        log.info("Fetching cart for user: {}", userId);
        Optional<Cart> cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE);
        
        if (cart.isEmpty()) {
            log.info("Creating new cart for user: {}", userId);
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setStatus(CartStatus.ACTIVE);
            newCart.setTotalPrice(BigDecimal.ZERO);
            Cart savedCart = cartRepository.save(newCart);
            return cartMapper.toResponseDTO(savedCart);
        }
        
        return cartMapper.toResponseDTO(cart.get());
    }

    /**
     * Add item to cart
     */
    public CartResponseDTO addToCart(Long userId, AddToCartRequestDTO request) {
        log.info("Adding product {} (quantity: {}) to cart for user: {}", 
                 request.getProductId(), request.getQuantity(), userId);
        
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setStatus(CartStatus.ACTIVE);
                    newCart.setTotalPrice(BigDecimal.ZERO);
                    return cartRepository.save(newCart);
                });

        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), request.getProductId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.setUpdatedAt(LocalDateTime.now());
            cartItemRepository.save(item);
            log.info("Updated existing cart item: {}", item.getId());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductId(request.getProductId());
            newItem.setQuantity(request.getQuantity());
            newItem.setProductName("Product " + request.getProductId()); // Will be fetched from Product Service
            newItem.setUnitPrice(BigDecimal.ZERO); // Will be fetched from Product Service
            newItem.setAddedAt(LocalDateTime.now());
            newItem.setUpdatedAt(LocalDateTime.now());
            cart.addItem(newItem);
            log.info("Added new item to cart");
        }

        cart = cartRepository.save(cart);
        return cartMapper.toResponseDTO(cart);
    }

    /**
     * Update cart item quantity
     */
    public CartResponseDTO updateCartItem(Long userId, Long itemId, UpdateCartItemRequestDTO request) {
        log.info("Updating cart item {} with quantity {} for user: {}", 
                 itemId, request.getQuantity(), userId);
        
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found: " + itemId));

        if (!item.getCart().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized: Item does not belong to user");
        }

        item.setQuantity(request.getQuantity());
        item.setUpdatedAt(LocalDateTime.now());
        cartItemRepository.save(item);

        Cart cart = item.getCart();
        cart.recalculateTotalPrice();
        cart = cartRepository.save(cart);

        return cartMapper.toResponseDTO(cart);
    }

    /**
     * Remove item from cart
     */
    public CartResponseDTO removeCartItem(Long userId, Long itemId) {
        log.info("Removing cart item {} for user: {}", itemId, userId);
        
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found: " + itemId));

        if (!item.getCart().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized: Item does not belong to user");
        }

        Cart cart = item.getCart();
        cart.removeItem(item);
        cartItemRepository.delete(item);

        cart = cartRepository.save(cart);
        return cartMapper.toResponseDTO(cart);
    }

    /**
     * Clear all items from cart
     */
    public CartResponseDTO clearCart(Long userId) {
        log.info("Clearing cart for user: {}", userId);
        
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user: " + userId));

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        cartItemRepository.deleteAll(items);
        
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cart = cartRepository.save(cart);

        log.info("Cart cleared for user: {}", userId);
        return cartMapper.toResponseDTO(cart);
    }

    /**
     * Checkout cart - mark as completed
     */
    public CartResponseDTO checkout(Long userId) {
        log.info("Checking out cart for user: {}", userId);
        
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot checkout empty cart");
        }

        cart.setStatus(CartStatus.COMPLETED);
        cart.setUpdatedAt(LocalDateTime.now());
        cart = cartRepository.save(cart);

        log.info("Cart checked out for user: {}", userId);
        return cartMapper.toResponseDTO(cart);
    }

    /**
     * Get cart by ID
     */
    public CartResponseDTO getCartById(Long cartId) {
        log.info("Fetching cart by ID: {}", cartId);
        
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found: " + cartId));

        return cartMapper.toResponseDTO(cart);
    }

    /**
     * Delete cart
     */
    public void deleteCart(Long userId, Long cartId) {
        log.info("Deleting cart {} for user: {}", cartId, userId);
        
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found: " + cartId));

        if (!cart.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized: Cart does not belong to user");
        }

        cartRepository.delete(cart);
        log.info("Cart deleted successfully");
    }
}
