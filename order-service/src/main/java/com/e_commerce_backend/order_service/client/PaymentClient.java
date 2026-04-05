package com.e_commerce_backend.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.e_commerce_backend.order_service.dto.PaymentRequestDTO;
import com.e_commerce_backend.order_service.dto.PaymentResponseDTO;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/api/payment/internal/process")
    PaymentResponseDTO processPayment(@RequestBody PaymentRequestDTO request);
}
