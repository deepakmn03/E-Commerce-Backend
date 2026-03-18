package com.e_commerce_backend.order_service.service;


import lombok.extern.log4j.Log4j2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_commerce_backend.order_service.dto.OrderResponseDTO;
import com.e_commerce_backend.order_service.entity.Order;
import com.e_commerce_backend.order_service.exception.OrderNotFoundException;
import com.e_commerce_backend.order_service.mapper.OrderMapper;
import com.e_commerce_backend.order_service.repository.OrderRepository;

@Log4j2
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;


    public OrderResponseDTO getOrderByOrderId(int orderId){
        com.e_commerce_backend.order_service.entity.Order order = orderRepository.findById(orderId)
                      .orElseThrow(() -> new OrderNotFoundException(orderId));
        return orderMapper.toDTO(order);
    }

    public List<OrderResponseDTO> getAllOrders(){
        List<Order> orders = orderRepository.findAll();
        return orderMapper.toDTOList(orders);
    }

    // public OrderResponseDTO creatOrder(Long orderValue, int userId){
    //     Optional<User> existingUser = userRepository.findById(userId);
    //     if(!existingUser.isPresent()){
    //         throw new RuntimeException("User with ID: " + userId + " not found");
    //     }
    //     Order order = new Order();
    //     order.setOrderValue(orderValue);
    //     User user = existingUser.get();
    //     order.setUser(user);
    //     user.addOrder(order);
    //     Order finalOrder = orderRepository.save(order);
    //     log.info("A new order has been created for user {}", user.getUsername());
    //     return orderMapper.toDTO(finalOrder);
    // }

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

    public String deleteOrder(int orderId){
        orderRepository.deleteById(orderId);
        log.warn("Order has been removed for order ID: {}", orderId);
        return "Order with order ID: " + orderId + " has been deleted.";
       }
    
}
