package com.e_commerce_backend.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.e_commerce_backend.order_service.dto.NotificationRequestDTO;
import com.e_commerce_backend.order_service.dto.NotificationResponseDTO;

@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/api/notification/internal/send")
    NotificationResponseDTO sendNotification(@RequestBody NotificationRequestDTO request);
}
