package com.e_commerce_backend.order_service.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.e_commerce_backend.order_service.dto.OrderRequestDTO;
import com.e_commerce_backend.order_service.dto.OrderResponseDTO;
import com.e_commerce_backend.order_service.entity.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrderResponseDTO toDTO(Order order);
    
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toEntity(OrderRequestDTO dto);
    
    List<OrderResponseDTO> toDTOList(List<Order> orders);
}
