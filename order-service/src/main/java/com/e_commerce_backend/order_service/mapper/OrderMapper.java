package com.e_commerce_backend.order_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.e_commerce_backend.order_service.dto.OrderResponseDTO;
import com.e_commerce_backend.order_service.entity.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponseDTO toDTO(Order order);
    List<OrderResponseDTO> toDTOList(List<Order> order);
}
