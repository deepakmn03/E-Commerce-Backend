package com.e_commerce_backend.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDTO {
    private Long userId;
    private String type;
    private String channel;
    private String recipient;
    private String subject;
    private String message;
}
