package com.e_commerce_backend.cart_service.mapper;

import com.e_commerce_backend.cart_service.dto.CartItemDTO;
import com.e_commerce_backend.cart_service.dto.CartResponseDTO;
import com.e_commerce_backend.cart_service.entity.Cart;
import com.e_commerce_backend.cart_service.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    
    @Mapping(target = "status", expression = "java(c.getStatus().toString())")
    @Mapping(target = "itemCount", expression = "java(c.getItemCount())")
    CartResponseDTO toResponseDTO(Cart c);
    
    @Mapping(target = "subtotal", expression = "java(ci.getSubtotal())")
    CartItemDTO toCartItemDTO(CartItem ci);
}
