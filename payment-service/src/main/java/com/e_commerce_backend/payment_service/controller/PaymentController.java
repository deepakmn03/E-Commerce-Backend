package com.e_commerce_backend.payment_service.controller;

import com.e_commerce_backend.payment_service.dto.PaymentRequestDTO;
import com.e_commerce_backend.payment_service.dto.PaymentResponseDTO;
import com.e_commerce_backend.payment_service.entity.PaymentStatus;
import com.e_commerce_backend.payment_service.service.PaymentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @GetMapping("/status")
    public ResponseEntity<String> paymentServiceStatus() {
        log.info("Payment service health check");
        return ResponseEntity.ok("Payment service is live now!!!");
    }
    
    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDTO> processPayment(@Valid @RequestBody PaymentRequestDTO request) {
        log.info("Processing payment for order: {}", request.getOrderId());
        PaymentResponseDTO payment = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }
    
    @GetMapping("/get/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long paymentId) {
        log.info("Fetching payment: {}", paymentId);
        PaymentResponseDTO payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByOrderId(@PathVariable Long orderId) {
        log.info("Fetching payment for order: {}", orderId);
        PaymentResponseDTO payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
        log.info("Fetching all payments");
        List<PaymentResponseDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        log.info("Fetching payments with status: {}", status);
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }
    
    @PostMapping("/refund/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> refundPayment(@PathVariable Long paymentId) {
        log.info("Refunding payment: {}", paymentId);
        PaymentResponseDTO refundedPayment = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(refundedPayment);
    }
}
