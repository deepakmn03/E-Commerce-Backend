package com.e_commerce_backend.cart_service.repository;

import com.e_commerce_backend.cart_service.entity.Cart;
import com.e_commerce_backend.cart_service.entity.CartStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
    Page<Cart> findByUserId(Long userId, Pageable pageable);
    Page<Cart> findByStatus(CartStatus status, Pageable pageable);
}
