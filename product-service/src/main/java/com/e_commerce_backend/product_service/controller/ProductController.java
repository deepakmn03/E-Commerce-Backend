package com.e_commerce_backend.product_service.controller;

import com.e_commerce_backend.product_service.dto.ProductRequestDTO;
import com.e_commerce_backend.product_service.dto.ProductResponseDTO;
import com.e_commerce_backend.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/product")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Product service is live!");
    }
    
    @PostMapping("/create")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO requestDTO) {
        log.info("Creating product with SKU: {}", requestDTO.getSku());
        ProductResponseDTO product = productService.createProduct(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    @GetMapping("/get/{productId}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable Long productId) {
        log.info("Fetching product with ID: {}", productId);
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/internal/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductInternal(@PathVariable Long productId) {
        log.info("Fetching product internally with ID: {}", productId);
        return ResponseEntity.ok(productService.getProductById(productId));
    }
    
    @GetMapping("/get/sku/{sku}")
    public ResponseEntity<ProductResponseDTO> getProductBySku(@PathVariable String sku) {
        log.info("Fetching product with SKU: {}", sku);
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }
    
    @GetMapping("/all")
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all products - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }
    
    @GetMapping("/active")
    public ResponseEntity<Page<ProductResponseDTO>> getActiveProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching active products - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getActiveProducts(pageable));
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDTO>> searchProducts(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching products with term: {}", searchTerm);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.searchProducts(searchTerm, pageable));
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ProductResponseDTO>> getByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching products for category: {}", category);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getProductsByCategory(category, pageable));
    }
    
    @GetMapping("/in-stock")
    public ResponseEntity<Page<ProductResponseDTO>> getInStockProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching in-stock products - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getInStockProducts(pageable));
    }
    
    @PatchMapping("/update/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequestDTO requestDTO) {
        log.info("Updating product: {}", productId);
        return ResponseEntity.ok(productService.updateProduct(productId, requestDTO));
    }
    
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        log.info("Deleting product: {}", productId);
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Product deleted successfully");
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        log.info("Fetching all categories");
        return ResponseEntity.ok(productService.getCategories());
    }
    
    // Internal APIs for other services (Cart, Order, etc.)
    @PostMapping("/reserve/{productId}/{quantity}")
    public ResponseEntity<Boolean> reserveStock(
            @PathVariable Long productId,
            @PathVariable Long quantity) {
        log.info("Reserving stock - Product: {}, Quantity: {}", productId, quantity);
        return ResponseEntity.ok(productService.reserveStock(productId, quantity));
    }

    @PostMapping("/internal/reserve/{productId}/{quantity}")
    public ResponseEntity<Boolean> reserveStockInternal(
            @PathVariable Long productId,
            @PathVariable Long quantity) {
        return ResponseEntity.ok(productService.reserveStock(productId, quantity));
    }
    
    @PostMapping("/release/{productId}/{quantity}")
    public ResponseEntity<Void> releaseStock(
            @PathVariable Long productId,
            @PathVariable Long quantity) {
        log.info("Releasing stock - Product: {}, Quantity: {}", productId, quantity);
        productService.releaseStock(productId, quantity);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/internal/release/{productId}/{quantity}")
    public ResponseEntity<Void> releaseStockInternal(
            @PathVariable Long productId,
            @PathVariable Long quantity) {
        productService.releaseStock(productId, quantity);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/deduct/{productId}/{quantity}")
    public ResponseEntity<Boolean> deductStock(
            @PathVariable Long productId,
            @PathVariable Long quantity) {
        log.info("Deducting stock - Product: {}, Quantity: {}", productId, quantity);
        return ResponseEntity.ok(productService.deductStock(productId, quantity));
    }

    @PostMapping("/internal/deduct/{productId}/{quantity}")
    public ResponseEntity<Boolean> deductStockInternal(
            @PathVariable Long productId,
            @PathVariable Long quantity) {
        return ResponseEntity.ok(productService.deductStock(productId, quantity));
    }
}
