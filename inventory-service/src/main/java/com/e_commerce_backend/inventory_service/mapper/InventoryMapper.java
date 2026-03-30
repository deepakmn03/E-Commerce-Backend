package com.e_commerce_backend.inventory_service.mapper;

import com.e_commerce_backend.inventory_service.dto.InventoryRequestDTO;
import com.e_commerce_backend.inventory_service.dto.InventoryResponseDTO;
import com.e_commerce_backend.inventory_service.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    
    Inventory toEntity(InventoryRequestDTO dto);
    
    @Mapping(target = "availableQuantity", expression = "java(entity.getAvailableQuantity())")
    InventoryResponseDTO toResponseDTO(Inventory entity);
}
