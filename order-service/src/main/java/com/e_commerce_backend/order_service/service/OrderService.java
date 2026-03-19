package com.e_commerce_backend.order_service.service;


import lombok.extern.log4j.Log4j2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_commerce_backend.order_service.client.UserClient;
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


    public OrderResponseDTO getOrderByOrderId(Long orderId){
        com.e_commerce_backend.order_service.entity.Order order = orderRepository.findById(orderId)
                      .orElseThrow(() -> new OrderNotFoundException(orderId));
        return orderMapper.toDTO(order);
    }

    public List<OrderResponseDTO> getAllOrders(){
        List<Order> orders = orderRepository.findAll();
        return orderMapper.toDTOList(orders);
    }

    public OrderResponseDTO creatOrder(Double orderValue, Long userId) {
        log.info("Initiating order creation. Verifying user ID: {} with user-service...", userId);

        try {
            // 1. Make the synchronous HTTP call over the network
            UserDTO user = userClient.getUserById(userId);
            log.info("Success! Verified user exists with ID: {}", user.getId());
            
        } catch (FeignException.NotFound e) {
            // 2. If user-service returns a 404 Not Found
            log.warn("Order rejected: User ID {} does not exist in user-service.", userId);
            throw new RuntimeException("Cannot create order: User not found.");
            
        } catch (FeignException e) {
            // 3. If user-service is completely down or returns a 500 error
            log.error("Communication with user-service failed!", e);
            throw new RuntimeException("Service unavailable. Please try again later.");
        }

        // 4. If everything is fine, save the order to the order-service database
        log.info("Saving order to database...");
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderValue(orderValue);
        Order savedOrder = orderRepository.save(order);
        log.info("Order saved successfully with order ID: {}", savedOrder.getOrderId());
        return orderMapper.toDTO(savedOrder);
    }
    // public OrderResponseDTO updateOrder(int orderId, OrderRequestDTO orderDetails){
    //    Order order = orderRepository.findById(orderId)
    //                  .orElseThrow(() -> new RuntimeException("Order with ID: " + orderId + " not found"));
        
    //     // Update order value
    //     order.setOrderValue(orderDetails.getOrderValue());
        
    //     // Update user if userId is provided
    //     if(orderDetails.getUserId() > 0){
    //         User user = userRepository.findById(orderDetails.getUserId())
    //                     .orElseThrow(() -> new RuntimeException("User with ID: " + orderDetails.getUserId() + " not found"));
    //         order.setUser(user);
    //     }
        
    //     Order updatedOrder = orderRepository.save(order);
    //     log.warn("Order has been updated with order ID: {}", orderId);
    //    return orderMapper.toDTO(updatedOrder);
    // }

    public String deleteOrder(Long orderId){
        orderRepository.deleteById(orderId);
        log.warn("Order has been removed for order ID: {}", orderId);
        return "Order with order ID: " + orderId + " has been deleted.";
    }
    
    public List<OrderResponseDTO> getOrdersByUserId(Long userId){
        List<Order> orders = orderRepository.findByUserId(userId);
        return orderMapper.toDTOList(orders);
    }
    
}
