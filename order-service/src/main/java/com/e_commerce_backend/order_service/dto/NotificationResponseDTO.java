package com.e_commerce_backend.order_service.dto;

import lombok.Data;

@Data
public class NotificationResponseDTO {
    private Long notificationId;
    private Boolean sent;
}
