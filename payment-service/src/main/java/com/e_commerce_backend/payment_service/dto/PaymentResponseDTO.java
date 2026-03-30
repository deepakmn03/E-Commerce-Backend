package com.e_commerce_backend.payment_service.dto;

import com.e_commerce_backend.payment_service.entity.PaymentMethod;
import com.e_commerce_backend.payment_service.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    
    private Long paymentId;
    
    private Long orderId;
    
    private BigDecimal amount;
    
    private PaymentStatus status;
    
    private PaymentMethod method;
    
    private String transactionId;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String remarks;
}
