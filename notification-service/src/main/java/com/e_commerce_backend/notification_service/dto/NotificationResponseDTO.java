package com.e_commerce_backend.notification_service.dto;

import com.e_commerce_backend.notification_service.entity.NotificationChannel;
import com.e_commerce_backend.notification_service.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    
    private Long notificationId;
    
    private Long userId;
    
    private NotificationType type;
    
    private NotificationChannel channel;
    
    private String recipient;
    
    private String subject;
    
    private String message;
    
    private Boolean sent;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime sentAt;
    
    private String errorMessage;
}
