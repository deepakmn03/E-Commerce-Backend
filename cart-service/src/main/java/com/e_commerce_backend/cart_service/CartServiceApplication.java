package com.e_commerce_backend.cart_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.e_commerce_backend.cart_service.controller",
    "com.e_commerce_backend.cart_service.service",
    "com.e_commerce_backend.cart_service.repository",
    "com.e_commerce_backend.cart_service.mapper",
    "com.e_commerce_backend.cart_service.config"
})
public class CartServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartServiceApplication.class, args);
	}

}
