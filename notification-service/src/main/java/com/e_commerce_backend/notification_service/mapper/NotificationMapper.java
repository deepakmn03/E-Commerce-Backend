package com.e_commerce_backend.notification_service.mapper;

import com.e_commerce_backend.notification_service.dto.NotificationRequestDTO;
import com.e_commerce_backend.notification_service.dto.NotificationResponseDTO;
import com.e_commerce_backend.notification_service.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    
    Notification toEntity(NotificationRequestDTO dto);
    
    NotificationResponseDTO toResponseDTO(Notification entity);
}
