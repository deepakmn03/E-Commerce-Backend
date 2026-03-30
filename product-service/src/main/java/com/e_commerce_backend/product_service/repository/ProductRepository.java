package com.e_commerce_backend.product_service.repository;

import com.e_commerce_backend.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findBySku(String sku);
    
    Page<Product> findByCategory(String category, Pageable pageable);
    
    Page<Product> findByIsActive(Boolean isActive, Pageable pageable);
    
    Page<Product> findByCategoryAndIsActive(String category, Boolean isActive, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND p.isActive = true")
    Page<Product> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 AND p.isActive = true")
    Page<Product> findAllInStock(Pageable pageable);
    
    List<Product> findByCategoryAndIsActive(String category, Boolean isActive);
}