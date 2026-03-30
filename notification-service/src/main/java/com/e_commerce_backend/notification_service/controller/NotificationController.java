package com.e_commerce_backend.notification_service.controller;

import com.e_commerce_backend.notification_service.dto.NotificationRequestDTO;
import com.e_commerce_backend.notification_service.dto.NotificationResponseDTO;
import com.e_commerce_backend.notification_service.service.NotificationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping("/status")
    public ResponseEntity<String> notificationServiceStatus() {
        log.info("Notification service health check");
        return ResponseEntity.ok("Notification service is live now!!!");
    }
    
    @PostMapping("/send")
    public ResponseEntity<NotificationResponseDTO> sendNotification(@Valid @RequestBody NotificationRequestDTO request) {
        log.info("Sending notification to user: {}", request.getUserId());
        NotificationResponseDTO notification = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }
    
    @GetMapping("/get/{notificationId}")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(@PathVariable Long notificationId) {
        log.info("Fetching notification: {}", notificationId);
        NotificationResponseDTO notification = notificationService.getNotificationById(notificationId);
        return ResponseEntity.ok(notification);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotifications() {
        log.info("Fetching all notifications");
        List<NotificationResponseDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByUserId(@PathVariable Long userId) {
        log.info("Fetching notifications for user: {}", userId);
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<NotificationResponseDTO>> getPendingNotifications() {
        log.info("Fetching pending notifications");
        List<NotificationResponseDTO> notifications = notificationService.getPendingNotifications();
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/user/{userId}/sent")
    public ResponseEntity<List<NotificationResponseDTO>> getSentNotificationsForUser(@PathVariable Long userId) {
        log.info("Fetching sent notifications for user: {}", userId);
        List<NotificationResponseDTO> notifications = notificationService.getSentNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }
}
