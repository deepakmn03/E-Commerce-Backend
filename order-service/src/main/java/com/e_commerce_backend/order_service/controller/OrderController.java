package com.e_commerce_backend.order_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_commerce_backend.order_service.dto.OrderRequestDTO;
import com.e_commerce_backend.order_service.dto.OrderResponseDTO;
import com.e_commerce_backend.order_service.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/status")
    public ResponseEntity<String> orderServiceStatus(){
        System.out.println("Order service is live now!!" + "\n" + "Current thread is: " + Thread.currentThread().getName());
        return ResponseEntity.ok("Order service is live now!!!");
    }

    @GetMapping("/get/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long orderId){
        OrderResponseDTO orderResponseDTO = orderService.getOrderByOrderId(orderId);
        return ResponseEntity.ok(orderResponseDTO);
    }

    @GetMapping("/get/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrdersByUserId(@PathVariable Long userId){
        List<OrderResponseDTO>orderResponseDTOs = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orderResponseDTOs);
    }

    @GetMapping("/get")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders(){
        List<OrderResponseDTO>orderResponseDTOs = orderService.getAllOrders();
        return ResponseEntity.ok(orderResponseDTOs);
    }

    @PostMapping("/create")
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO createOrderDTO){
        OrderResponseDTO orderResponseDTO = orderService.creatOrder(createOrderDTO.getOrderValue(), createOrderDTO.getUserId());
        return ResponseEntity.ok(orderResponseDTO);
    }

    @DeleteMapping("/remove/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId){
        String result = orderService.deleteOrder(orderId);
        return ResponseEntity.ok(result);
    }

    // @PatchMapping(value = "/update/{orderId}", consumes = "application/json", produces = "application/json")
    // public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable int orderId, @Valid @RequestBody OrderRequestDTO orderRequestDTO){
    //     OrderResponseDTO updatedOrder = orderService.updateOrder(orderId, orderRequestDTO);
    //     return ResponseEntity.ok(updatedOrder);
    // }
}

