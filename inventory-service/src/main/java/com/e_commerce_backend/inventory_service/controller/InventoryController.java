package com.e_commerce_backend.inventory_service.controller;

import com.e_commerce_backend.inventory_service.dto.InventoryRequestDTO;
import com.e_commerce_backend.inventory_service.dto.InventoryResponseDTO;
import com.e_commerce_backend.inventory_service.service.InventoryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;
    
    @GetMapping("/status")
    public ResponseEntity<String> inventoryServiceStatus() {
        log.info("Inventory service health check");
        return ResponseEntity.ok("Inventory service is live now!!!");
    }
    
    @PostMapping("/create")
    public ResponseEntity<InventoryResponseDTO> createInventory(@Valid @RequestBody InventoryRequestDTO request) {
        log.info("Creating inventory for product: {}", request.getProductId());
        InventoryResponseDTO inventory = inventoryService.createInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventory);
    }
    
    @GetMapping("/get/{inventoryId}")
    public ResponseEntity<InventoryResponseDTO> getInventoryById(@PathVariable Long inventoryId) {
        log.info("Fetching inventory: {}", inventoryId);
        InventoryResponseDTO inventory = inventoryService.getInventoryById(inventoryId);
        return ResponseEntity.ok(inventory);
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponseDTO> getInventoryByProductId(@PathVariable Long productId) {
        log.info("Fetching inventory for product: {}", productId);
        InventoryResponseDTO inventory = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(inventory);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventory() {
        log.info("Fetching all inventory");
        List<InventoryResponseDTO> inventories = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventories);
    }
    
    @GetMapping("/check/{productId}/{quantity}")
    public ResponseEntity<Boolean> isStockAvailable(@PathVariable Long productId, @PathVariable Integer quantity) {
        log.info("Checking stock for product: {} quantity: {}", productId, quantity);
        boolean available = inventoryService.isStockAvailable(productId, quantity);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/internal/check/{productId}/{quantity}")
    public ResponseEntity<Boolean> isStockAvailableInternal(@PathVariable Long productId, @PathVariable Integer quantity) {
        return ResponseEntity.ok(inventoryService.isStockAvailable(productId, quantity));
    }

    @PostMapping("/internal/reserve/{productId}/{quantity}")
    public ResponseEntity<Boolean> reserveStockInternal(@PathVariable Long productId, @PathVariable Integer quantity) {
        return ResponseEntity.ok(inventoryService.reserveStock(productId, quantity));
    }

    @PostMapping("/internal/release/{productId}/{quantity}")
    public ResponseEntity<Void> releaseReservedStockInternal(@PathVariable Long productId, @PathVariable Integer quantity) {
        inventoryService.releaseReservedStock(productId, quantity);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/deduct/{productId}/{quantity}")
    public ResponseEntity<InventoryResponseDTO> deductStock(@PathVariable Long productId, @PathVariable Integer quantity) {
        log.info("Deducting stock for product: {}", productId);
        InventoryResponseDTO inventory = inventoryService.deductStock(productId, quantity);
        return ResponseEntity.ok(inventory);
    }

    @PutMapping("/internal/deduct/{productId}/{quantity}")
    public ResponseEntity<InventoryResponseDTO> deductStockInternal(@PathVariable Long productId, @PathVariable Integer quantity) {
        return ResponseEntity.ok(inventoryService.deductStock(productId, quantity));
    }
    
    @PutMapping("/add/{productId}/{quantity}")
    public ResponseEntity<InventoryResponseDTO> addStock(@PathVariable Long productId, @PathVariable Integer quantity) {
        log.info("Adding stock for product: {}", productId);
        InventoryResponseDTO inventory = inventoryService.addStock(productId, quantity);
        return ResponseEntity.ok(inventory);
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryResponseDTO>> getLowStockInventory() {
        log.info("Fetching low stock inventory");
        List<InventoryResponseDTO> lowStockItems = inventoryService.getLowStockInventory();
        return ResponseEntity.ok(lowStockItems);
    }
}
