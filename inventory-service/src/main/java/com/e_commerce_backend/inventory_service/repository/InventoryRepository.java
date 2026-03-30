package com.e_commerce_backend.inventory_service.repository;

import com.e_commerce_backend.inventory_service.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    Optional<Inventory> findByProductId(Long productId);
    
    List<Inventory> findByQuantityAvailableLessThan(Integer quantity);
    
    List<Inventory> findByIsActive(Boolean isActive);
}
