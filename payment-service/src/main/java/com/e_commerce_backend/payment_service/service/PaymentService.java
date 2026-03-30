package com.e_commerce_backend.payment_service.service;

import com.e_commerce_backend.payment_service.dto.PaymentRequestDTO;
import com.e_commerce_backend.payment_service.dto.PaymentResponseDTO;
import com.e_commerce_backend.payment_service.entity.Payment;
import com.e_commerce_backend.payment_service.entity.PaymentStatus;
import com.e_commerce_backend.payment_service.exception.PaymentNotFoundException;
import com.e_commerce_backend.payment_service.mapper.PaymentMapper;
import com.e_commerce_backend.payment_service.repository.PaymentRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private PaymentMapper paymentMapper;
    
    /**
     * Process a new payment
     */
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        log.info("Processing payment for order: {}", request.getOrderId());
        
        Payment payment = paymentMapper.toEntity(request);
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setTransactionId(generateTransactionId());
        
        // Mock payment gateway processing
        processWithMockGateway(payment);
        
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment processed successfully. Transaction ID: {}", savedPayment.getTransactionId());
        
        return paymentMapper.toResponseDTO(savedPayment);
    }
    
    /**
     * Get payment by ID
     */
    public PaymentResponseDTO getPaymentById(Long paymentId) {
        log.info("Fetching payment: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        return paymentMapper.toResponseDTO(payment);
    }
    
    /**
     * Get payment by Order ID
     */
    public PaymentResponseDTO getPaymentByOrderId(Long orderId) {
        log.info("Fetching payment for order: {}", orderId);
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + orderId));
        return paymentMapper.toResponseDTO(payment);
    }
    
    /**
     * Get all payments
     */
    public List<PaymentResponseDTO> getAllPayments() {
        log.info("Fetching all payments");
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get payments by status
     */
    public List<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status) {
        log.info("Fetching payments with status: {}", status);
        return paymentRepository.findByStatus(status).stream()
                .map(paymentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Refund a payment
     */
    public PaymentResponseDTO refundPayment(Long paymentId) {
        log.info("Refunding payment: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        
        if (!payment.getStatus().equals(PaymentStatus.COMPLETED)) {
            throw new IllegalArgumentException("Only completed payments can be refunded");
        }
        
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRemarks("Refund processed successfully");
        
        Payment refundedPayment = paymentRepository.save(payment);
        log.info("Payment refunded: {}", paymentId);
        
        return paymentMapper.toResponseDTO(refundedPayment);
    }
    
    /**
     * Mock payment gateway processing
     */
    private void processWithMockGateway(Payment payment) {
        // Simulate 95% success rate
        boolean isSuccessful = Math.random() < 0.95;
        
        if (isSuccessful) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setRemarks("Payment completed successfully");
            log.info("Mock payment gateway: Payment successful for transaction {}", payment.getTransactionId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setRemarks("Payment declined by gateway");
            log.warn("Mock payment gateway: Payment failed for transaction {}", payment.getTransactionId());
        }
    }
    
    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
