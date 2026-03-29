package com.e_commerce_backend.product_service.service;

import com.e_commerce_backend.product_service.dto.ProductRequestDTO;
import com.e_commerce_backend.product_service.dto.ProductResponseDTO;
import com.e_commerce_backend.product_service.entity.Product;
import com.e_commerce_backend.product_service.exception.ProductNotFoundException;
import com.e_commerce_backend.product_service.mapper.ProductMapper;
import com.e_commerce_backend.product_service.repository.ProductRepository;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductMapper productMapper;
    
    /**
     * Create new product
     */
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        // Check if SKU already exists
        if (productRepository.findBySku(requestDTO.getSku()).isPresent()) {
            throw new IllegalArgumentException("Product with SKU: " + requestDTO.getSku() + " already exists");
        }
        
        Product product = productMapper.toEntity(requestDTO);
        Product savedProduct = productRepository.save(product);
        
        log.info("Product created successfully: {}", savedProduct.getProductId());
        return productMapper.toResponseDTO(savedProduct);
    }
    
    /**
     * Get product by ID
     */
    public ProductResponseDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        return productMapper.toResponseDTO(product);
    }
    
    /**
     * Get product by SKU
     */
    public ProductResponseDTO getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));
        return productMapper.toResponseDTO(product);
    }
    
    /**
     * Get all products with pagination
     */
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
            .map(productMapper::toResponseDTO);
    }
    
    /**
     * Get active products with pagination
     */
    public Page<ProductResponseDTO> getActiveProducts(Pageable pageable) {
        return productRepository.findByIsActive(true, pageable)
            .map(productMapper::toResponseDTO);
    }
    
    /**
     * Search products by name
     */
    public Page<ProductResponseDTO> searchProducts(String searchTerm, Pageable pageable) {
        return productRepository.searchByName(searchTerm, pageable)
            .map(productMapper::toResponseDTO);
    }
    
    /**
     * Get products by category
     */
    public Page<ProductResponseDTO> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryAndIsActive(category, true, pageable)
            .map(productMapper::toResponseDTO);
    }
    
    /**
     * Get in-stock products
     */
    public Page<ProductResponseDTO> getInStockProducts(Pageable pageable) {
        return productRepository.findAllInStock(pageable)
            .map(productMapper::toResponseDTO);
    }
    
    /**
     * Update product
     */
    @Transactional
    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO requestDTO) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        // Check if SKU is being changed and already exists
        if (!product.getSku().equals(requestDTO.getSku()) && 
            productRepository.findBySku(requestDTO.getSku()).isPresent()) {
            throw new IllegalArgumentException("Product with SKU: " + requestDTO.getSku() + " already exists");
        }
        
        productMapper.updateProductFromDTO(requestDTO, product);
        Product updatedProduct = productRepository.save(product);
        
        log.info("Product updated successfully: {}", productId);
        return productMapper.toResponseDTO(updatedProduct);
    }
    
    /**
     * Delete product
     */
    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product not found with id: " + productId);
        }
        productRepository.deleteById(productId);
        log.warn("Product deleted: {}", productId);
    }
    
    /**
     * Reserve stock (used by Cart Service)
     * Important for cart persistence
     */
    @Transactional
    public boolean reserveStock(Long productId, Long quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        if (product.getAvailableQuantity() < quantity) {
            log.warn("Insufficient stock for product: {}", productId);
            return false;
        }
        
        product.setReservedQuantity(product.getReservedQuantity() + quantity);
        productRepository.save(product);
        
        log.info("Stock reserved for product: {}, quantity: {}", productId, quantity);
        return true;
    }
    
    /**
     * Release reserved stock
     */
    @Transactional
    public void releaseStock(Long productId, Long quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        product.setReservedQuantity(Math.max(0, product.getReservedQuantity() - quantity));
        productRepository.save(product);
        
        log.info("Stock released for product: {}, quantity: {}", productId, quantity);
    }
    
    /**
     * Deduct stock (used when order is confirmed)
     */
    @Transactional
    public boolean deductStock(Long productId, Long quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        if (product.getAvailableQuantity() < quantity) {
            return false;
        }
        
        product.setStockQuantity(product.getStockQuantity() - quantity);
        product.setReservedQuantity(Math.max(0, product.getReservedQuantity() - quantity));
        productRepository.save(product);
        
        log.info("Stock deducted for product: {}, quantity: {}", productId, quantity);
        return true;
    }
    
    /**
     * Get categories
     */
    public List<String> getCategories() {
        return productRepository.findAll()
            .stream()
            .map(Product::getCategory)
            .distinct()
            .collect(Collectors.toList());
    }
}