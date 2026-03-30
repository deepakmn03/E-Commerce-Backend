package com.e_commerce_backend.notification_service.repository;

import com.e_commerce_backend.notification_service.entity.Notification;
import com.e_commerce_backend.notification_service.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUserId(Long userId);
    
    List<Notification> findBySent(Boolean sent);
    
    List<Notification> findByType(NotificationType type);
    
    List<Notification> findByUserIdAndSent(Long userId, Boolean sent);
}
