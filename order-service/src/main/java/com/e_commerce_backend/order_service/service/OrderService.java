package com.e_commerce_backend.order_service.service;

import lombok.extern.log4j.Log4j2;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_commerce_backend.order_service.client.CartClient;
import com.e_commerce_backend.order_service.client.InventoryClient;
import com.e_commerce_backend.order_service.client.NotificationClient;
import com.e_commerce_backend.order_service.client.PaymentClient;
import com.e_commerce_backend.order_service.client.ProductClient;
import com.e_commerce_backend.order_service.client.UserClient;
import com.e_commerce_backend.order_service.dto.CartItemDTO;
import com.e_commerce_backend.order_service.dto.CartResponseDTO;
import com.e_commerce_backend.order_service.dto.NotificationRequestDTO;
import com.e_commerce_backend.order_service.dto.OrderRequestDTO;
import com.e_commerce_backend.order_service.dto.OrderResponseDTO;
import com.e_commerce_backend.order_service.dto.PaymentRequestDTO;
import com.e_commerce_backend.order_service.dto.PaymentResponseDTO;
import com.e_commerce_backend.order_service.dto.UserDTO;
import com.e_commerce_backend.order_service.entity.Order;
import com.e_commerce_backend.order_service.exception.OrderNotFoundException;
import com.e_commerce_backend.order_service.mapper.OrderMapper;
import com.e_commerce_backend.order_service.repository.OrderRepository;

import feign.FeignException;

@Log4j2
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private CartClient cartClient;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private InventoryClient inventoryClient;

    // Create a new order
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        Long userId = orderRequestDTO.getUserId();
        UserDTO user;
        
        // Validate user exists in user service
        try {
            user = userClient.getUserById(userId);
            if (user == null || user.getUserId() == null) {
                throw new RuntimeException("User with ID: " + userId + " does not exist");
            }
            log.info("User validation successful for userId: {}", userId);
        } catch (FeignException.NotFound e) {
            log.error("User not found with userId: {}", userId);
            throw new RuntimeException("User with ID: " + userId + " does not exist in the system", e);
        } catch (FeignException e) {
            log.error("Error communicating with User Service for userId: {}", userId);
            throw new RuntimeException("Unable to verify user. User service is unavailable", e);
        }
        
        CartResponseDTO cart = getActiveCart(userId);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create order from an empty cart");
        }

        Order order = orderMapper.toEntity(orderRequestDTO);
        order.setOrderValue(cart.getTotalPrice().doubleValue());
        order.setStatus("CREATED");
        Order savedOrder = orderRepository.save(order);

        PaymentResponseDTO payment = paymentClient.processPayment(new PaymentRequestDTO(
            savedOrder.getOrderId(),
            cart.getTotalPrice(),
            resolvePaymentMethod(orderRequestDTO.getPaymentMethod())
        ));

        if (payment == null || payment.getStatus() == null) {
            savedOrder.setStatus("PAYMENT_PENDING");
            return orderMapper.toDTO(orderRepository.save(savedOrder));
        }

        if (!"COMPLETED".equalsIgnoreCase(payment.getStatus())) {
            savedOrder.setStatus("PAYMENT_FAILED");
            savedOrder = orderRepository.save(savedOrder);
            log.warn("Payment failed for orderId: {}, transactionId: {}", savedOrder.getOrderId(), payment.getTransactionId());
            return orderMapper.toDTO(savedOrder);
        }

        finalizeReservedStock(cart);
        cartClient.checkout(userId);
        savedOrder.setStatus("CONFIRMED");
        savedOrder = orderRepository.save(savedOrder);
        sendOrderNotifications(savedOrder, user, payment);

        log.info("Order created successfully for userId: {}, orderId: {}", userId, savedOrder.getOrderId());
        return orderMapper.toDTO(savedOrder);
    }

    // Get order by order ID
    public OrderResponseDTO getOrderByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                      .orElseThrow(() -> new OrderNotFoundException(orderId));
        return orderMapper.toDTO(order);
    }

    // Get all orders
    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orderMapper.toDTOList(orders);
    }

    // Get all orders by user ID
    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
            .map(orderMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    // Delete order by ID
    public String deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException(orderId);
        }
        orderRepository.deleteById(orderId);
        log.warn("Order has been removed for order ID: {}", orderId);
        return "Order with order ID: " + orderId + " has been deleted.";
    }

    // Update order status
    public OrderResponseDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated for orderId: {}, newStatus: {}", orderId, status);
        return orderMapper.toDTO(updatedOrder);
    }

    private CartResponseDTO getActiveCart(Long userId) {
        try {
            return cartClient.getActiveCartForUser(userId);
        } catch (FeignException e) {
            log.error("Unable to fetch active cart for userId: {}", userId);
            throw new RuntimeException("Unable to fetch active cart for user: " + userId, e);
        }
    }

    private void finalizeReservedStock(CartResponseDTO cart) {
        for (CartItemDTO item : cart.getItems()) {
            Boolean productDeducted = productClient.deductStock(item.getProductId(), item.getQuantity().longValue());
            if (!Boolean.TRUE.equals(productDeducted)) {
                throw new IllegalStateException("Unable to deduct product stock for product: " + item.getProductId());
            }
            inventoryClient.deductStock(item.getProductId(), item.getQuantity());
        }
    }

    private void sendOrderNotifications(Order order, UserDTO user, PaymentResponseDTO payment) {
        String recipient = user.getEmail() != null ? user.getEmail() : "unknown@customer.local";
        try {
            notificationClient.sendNotification(new NotificationRequestDTO(
                order.getUserId(),
                "ORDER_PLACED",
                "EMAIL",
                recipient,
                "Order confirmed",
                String.format("Order %d has been placed successfully for amount %.2f.", order.getOrderId(), order.getOrderValue())
            ));
            notificationClient.sendNotification(new NotificationRequestDTO(
                order.getUserId(),
                "PAYMENT_CONFIRMED",
                "EMAIL",
                recipient,
                "Payment confirmed",
                String.format("Payment %s completed successfully for order %d.", payment.getTransactionId(), order.getOrderId())
            ));
        } catch (FeignException e) {
            log.warn("Notification dispatch failed for orderId: {}", order.getOrderId(), e);
        }
    }

    private String resolvePaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isBlank()) {
            return "COD";
        }
        return paymentMethod.trim().toUpperCase(Locale.ROOT);
    }
}
