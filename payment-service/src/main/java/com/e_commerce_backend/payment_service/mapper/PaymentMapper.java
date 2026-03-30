package com.e_commerce_backend.payment_service.mapper;

import com.e_commerce_backend.payment_service.dto.PaymentRequestDTO;
import com.e_commerce_backend.payment_service.dto.PaymentResponseDTO;
import com.e_commerce_backend.payment_service.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    
    Payment toEntity(PaymentRequestDTO dto);
    
    PaymentResponseDTO toResponseDTO(Payment entity);
}
