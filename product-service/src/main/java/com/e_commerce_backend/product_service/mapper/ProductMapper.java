package com.e_commerce_backend.product_service.mapper;

import com.e_commerce_backend.product_service.dto.ProductRequestDTO;
import com.e_commerce_backend.product_service.dto.ProductResponseDTO;
import com.e_commerce_backend.product_service.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "reservedQuantity", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    Product toEntity(ProductRequestDTO dto);
    
    @Mapping(target = "availableQuantity", expression = "java(p.getAvailableQuantity())")
    ProductResponseDTO toResponseDTO(Product p);
    
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "reservedQuantity", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    void updateProductFromDTO(ProductRequestDTO dto, @MappingTarget Product product);
}