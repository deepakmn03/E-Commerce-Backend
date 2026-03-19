package com.e_commerce_backend.order_service.service;

import lombok.extern.log4j.Log4j2;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_commerce_backend.order_service.client.UserClient;
import com.e_commerce_backend.order_service.dto.OrderRequestDTO;
import com.e_commerce_backend.order_service.dto.OrderResponseDTO;
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

    // Create a new order
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        Long userId = orderRequestDTO.getUserId();
        
        // Validate user exists in user service
        try {
            UserDTO user = userClient.getUserById(userId);
            if (user == null) {
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
        
        // Create order after user validation
        Order order = orderMapper.toEntity(orderRequestDTO);
        Order savedOrder = orderRepository.save(order);
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
}