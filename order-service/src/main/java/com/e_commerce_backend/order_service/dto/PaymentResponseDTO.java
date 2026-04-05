package com.e_commerce_backend.order_service.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PaymentResponseDTO {
    private Long paymentId;
    private Long orderId;
    private BigDecimal amount;
    private String status;
    private String method;
    private String transactionId;
    private String remarks;
}
