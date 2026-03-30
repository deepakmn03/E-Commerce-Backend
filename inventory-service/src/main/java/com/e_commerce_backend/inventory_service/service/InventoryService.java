package com.e_commerce_backend.inventory_service.service;

import com.e_commerce_backend.inventory_service.dto.InventoryRequestDTO;
import com.e_commerce_backend.inventory_service.dto.InventoryResponseDTO;
import com.e_commerce_backend.inventory_service.entity.Inventory;
import com.e_commerce_backend.inventory_service.exception.InventoryNotFoundException;
import com.e_commerce_backend.inventory_service.exception.InsufficientStockException;
import com.e_commerce_backend.inventory_service.mapper.InventoryMapper;
import com.e_commerce_backend.inventory_service.repository.InventoryRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
public class InventoryService {
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private InventoryMapper inventoryMapper;
    
    /**
     * Create new inventory
     */
    public InventoryResponseDTO createInventory(InventoryRequestDTO request) {
        log.info("Creating inventory for product: {}", request.getProductId());
        
        if (inventoryRepository.findByProductId(request.getProductId()).isPresent()) {
            throw new IllegalArgumentException("Inventory already exists for product: " + request.getProductId());
        }
        
        Inventory inventory = inventoryMapper.toEntity(request);
        Inventory savedInventory = inventoryRepository.save(inventory);
        
        log.info("Inventory created successfully for product: {}", savedInventory.getProductId());
        return inventoryMapper.toResponseDTO(savedInventory);
    }
    
    /**
     * Get inventory by ID
     */
    public InventoryResponseDTO getInventoryById(Long inventoryId) {
        log.info("Fetching inventory: {}", inventoryId);
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryId));
        return inventoryMapper.toResponseDTO(inventory);
    }
    
    /**
     * Get inventory by product ID
     */
    public InventoryResponseDTO getInventoryByProductId(Long productId) {
        log.info("Fetching inventory for product: {}", productId);
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product: " + productId));
        return inventoryMapper.toResponseDTO(inventory);
    }
    
    /**
     * Get all inventory
     */
    public List<InventoryResponseDTO> getAllInventory() {
        log.info("Fetching all inventory");
        return inventoryRepository.findAll().stream()
                .map(inventoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Check stock availability
     */
    public boolean isStockAvailable(Long productId, Integer quantity) {
        log.info("Checking stock availability for product: {} quantity: {}", productId, quantity);
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Product not found: " + productId));
        
        return inventory.getAvailableQuantity() >= quantity;
    }
    
    /**
     * Deduct stock
     */
    public InventoryResponseDTO deductStock(Long productId, Integer quantity) {
        log.info("Deducting {} units of stock for product: {}", quantity, productId);
        
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Product not found: " + productId));
        
        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException(productId, quantity, inventory.getAvailableQuantity());
        }
        
        inventory.setQuantityAvailable(inventory.getQuantityAvailable() - quantity);
        Inventory updated = inventoryRepository.save(inventory);
        
        log.info("Stock deducted successfully for product: {}", productId);
        return inventoryMapper.toResponseDTO(updated);
    }
    
    /**
     * Add stock
     */
    public InventoryResponseDTO addStock(Long productId, Integer quantity) {
        log.info("Adding {} units of stock for product: {}", quantity, productId);
        
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Product not found: " + productId));
        
        inventory.setQuantityAvailable(inventory.getQuantityAvailable() + quantity);
        Inventory updated = inventoryRepository.save(inventory);
        
        log.info("Stock added successfully for product: {}", productId);
        return inventoryMapper.toResponseDTO(updated);
    }
    
    /**
     * Get low stock inventory
     */
    public List<InventoryResponseDTO> getLowStockInventory() {
        log.info("Fetching low stock inventory");
        return inventoryRepository.findAll().stream()
                .filter(inv -> inv.getAvailableQuantity() <= inv.getReorderLevel())
                .map(inventoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
