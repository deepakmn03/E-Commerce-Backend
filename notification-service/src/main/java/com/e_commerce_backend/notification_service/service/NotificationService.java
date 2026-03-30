package com.e_commerce_backend.notification_service.service;

import com.e_commerce_backend.notification_service.dto.NotificationRequestDTO;
import com.e_commerce_backend.notification_service.dto.NotificationResponseDTO;
import com.e_commerce_backend.notification_service.entity.Notification;
import com.e_commerce_backend.notification_service.exception.NotificationNotFoundException;
import com.e_commerce_backend.notification_service.mapper.NotificationMapper;
import com.e_commerce_backend.notification_service.repository.NotificationRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    /**
     * Send a notification
     */
    public NotificationResponseDTO sendNotification(NotificationRequestDTO request) {
        log.info("Sending notification to user: {} via {}", request.getUserId(), request.getChannel());
        
        Notification notification = notificationMapper.toEntity(request);
        
        // Mock sending notification
        sendNotificationMock(notification);
        
        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification sent successfully. ID: {}", savedNotification.getNotificationId());
        
        return notificationMapper.toResponseDTO(savedNotification);
    }
    
    /**
     * Get notification by ID
     */
    public NotificationResponseDTO getNotificationById(Long notificationId) {
        log.info("Fetching notification: {}", notificationId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));
        return notificationMapper.toResponseDTO(notification);
    }
    
    /**
     * Get all notifications
     */
    public List<NotificationResponseDTO> getAllNotifications() {
        log.info("Fetching all notifications");
        return notificationRepository.findAll().stream()
                .map(notificationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get notifications by user ID
     */
    public List<NotificationResponseDTO> getNotificationsByUserId(Long userId) {
        log.info("Fetching notifications for user: {}", userId);
        return notificationRepository.findByUserId(userId).stream()
                .map(notificationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get pending notifications
     */
    public List<NotificationResponseDTO> getPendingNotifications() {
        log.info("Fetching pending notifications");
        return notificationRepository.findBySent(false).stream()
                .map(notificationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get sent notifications for user
     */
    public List<NotificationResponseDTO> getSentNotificationsForUser(Long userId) {
        log.info("Fetching sent notifications for user: {}", userId);
        return notificationRepository.findByUserIdAndSent(userId, true).stream()
                .map(notificationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Mock notification sending
     */
    private void sendNotificationMock(Notification notification) {
        log.info("Sending {} notification to {} via {}", 
                notification.getType(), notification.getRecipient(), notification.getChannel());
        
        // Simulate sending notification
        try {
            // Mock delay
            Thread.sleep(100);
            
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            log.info("Mock notification sent successfully to {}", notification.getRecipient());
        } catch (InterruptedException e) {
            notification.setSent(false);
            notification.setErrorMessage("Notification sending interrupted: " + e.getMessage());
            log.error("Error sending mock notification: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
