package com.e_commerce_backend.cart_service.service;

import com.e_commerce_backend.cart_service.client.InventoryClient;
import com.e_commerce_backend.cart_service.client.ProductClient;
import com.e_commerce_backend.cart_service.client.UserClient;
import com.e_commerce_backend.cart_service.dto.AddToCartRequestDTO;
import com.e_commerce_backend.cart_service.dto.CartResponseDTO;
import com.e_commerce_backend.cart_service.dto.ProductDTO;
import com.e_commerce_backend.cart_service.dto.UpdateCartItemRequestDTO;
import com.e_commerce_backend.cart_service.dto.UserDTO;
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

    @Autowired
    private UserClient userClient;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private InventoryClient inventoryClient;

    /**
     * Get or create user's active cart
     */
    public CartResponseDTO getCart(Long userId) {
        log.info("Fetching cart for user: {}", userId);
        validateUser(userId);
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
        validateUser(userId);
        ProductDTO product = getProductOrThrow(request.getProductId());
        if (!Boolean.TRUE.equals(product.getIsActive())) {
            throw new IllegalArgumentException("Product is not active: " + request.getProductId());
        }

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
            reserveStock(request.getProductId(), request.getQuantity());
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.setProductName(product.getName());
            item.setUnitPrice(product.getPrice());
            item.setUpdatedAt(LocalDateTime.now());
            cartItemRepository.save(item);
            log.info("Updated existing cart item: {}", item.getId());
        } else {
            reserveStock(request.getProductId(), request.getQuantity());
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductId(request.getProductId());
            newItem.setQuantity(request.getQuantity());
            newItem.setProductName(product.getName());
            newItem.setUnitPrice(product.getPrice());
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

        int quantityDifference = request.getQuantity() - item.getQuantity();
        if (quantityDifference > 0) {
            reserveStock(item.getProductId(), quantityDifference);
        } else if (quantityDifference < 0) {
            releaseStock(item.getProductId(), -quantityDifference);
        }

        ProductDTO product = getProductOrThrow(item.getProductId());
        item.setQuantity(request.getQuantity());
        item.setProductName(product.getName());
        item.setUnitPrice(product.getPrice());
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
        releaseStock(item.getProductId(), item.getQuantity());
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
        items.forEach(item -> releaseStock(item.getProductId(), item.getQuantity()));
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
        validateUser(userId);
        
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

    public CartResponseDTO getActiveCartForUser(Long userId) {
        log.info("Fetching active cart without creating one for user: {}", userId);
        validateUser(userId);
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Active cart not found for user: " + userId));
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

        if (cart.getStatus() == CartStatus.ACTIVE) {
            cart.getItems().forEach(item -> releaseStock(item.getProductId(), item.getQuantity()));
        }
        cartRepository.delete(cart);
        log.info("Cart deleted successfully");
    }

    private void validateUser(Long userId) {
        UserDTO user = userClient.getUserById(userId);
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
    }

    private ProductDTO getProductOrThrow(Long productId) {
        ProductDTO product = productClient.getProductById(productId);
        if (product == null || product.getProductId() == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
        return product;
    }

    private void reserveStock(Long productId, int quantity) {
        Boolean inventoryAvailable = inventoryClient.isStockAvailable(productId, quantity);
        if (!Boolean.TRUE.equals(inventoryAvailable)) {
            throw new IllegalArgumentException("Insufficient inventory for product: " + productId);
        }

        Boolean productReserved = productClient.reserveStock(productId, (long) quantity);
        if (!Boolean.TRUE.equals(productReserved)) {
            throw new IllegalArgumentException("Unable to reserve product stock for product: " + productId);
        }

        try {
            Boolean inventoryReserved = inventoryClient.reserveStock(productId, quantity);
            if (!Boolean.TRUE.equals(inventoryReserved)) {
                productClient.releaseStock(productId, (long) quantity);
                throw new IllegalArgumentException("Unable to reserve inventory for product: " + productId);
            }
        } catch (RuntimeException ex) {
            productClient.releaseStock(productId, (long) quantity);
            throw ex;
        }
    }

    private void releaseStock(Long productId, int quantity) {
        productClient.releaseStock(productId, (long) quantity);
        inventoryClient.releaseStock(productId, quantity);
    }
}
