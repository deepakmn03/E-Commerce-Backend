#!/bin/bash

echo "####### Build images for E-commerce backend #########"

echo "Building user-service image"
docker build -t ecom-backend/user-service:v1.0.0 user-service/

echo "Building product-service image"
docker build -t ecom-backend/product-service:v1.0.0 product-service/

echo "Building payment-service image"
docker build -t ecom-backend/payment-service:v1.0.0 payment-service/

echo "Building order-service image"
docker build -t ecom-backend/order-service:v1.0.0 order-service/

echo "Building notification-service image"
docker build -t ecom-backend/notification-service:v1.0.0 notification-service/

echo "Building inventory-service image"
docker build -t ecom-backend/inventory-service:v1.0.0 inventory-service/

echo "Building eureka-service image"
docker build -t ecom-backend/eureka-server:v1.0.0 eureka-server/

echo "Building api-gateway-service image"
docker build -t ecom-backend/api-gateway:v1.0.0 api-gateway/


echo "Image build is successfull"





