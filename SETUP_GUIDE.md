# E-Commerce Backend - Setup & Deployment Guide

## Quick Start - 3 Steps

```bash
cd "/Users/deepakmn/Desktop/myWork/projects/E-Commerce backend"
./start.sh
docker-compose ps
curl http://localhost:8761
```

## Services

| Service | Port | Purpose |
|---------|------|---------|
| Eureka Server | 8761 | Service discovery |
| API Gateway | 8080 | Central routing & auth |
| User Service | 8082 | Authentication |
| Product Service | 8084 | Product catalog |
| Order Service | 8083 | Order management |
| Cart Service | 8004 | Shopping cart |
| Payment Service | 8085 | Payment processing |
| Notification Service | 8086 | Notifications |
| Inventory Service | 8087 | Stock management |

## Docker Compose

```bash
# Start
docker-compose up -d

# Stop
docker-compose down

# Logs
docker-compose logs -f

# Status
docker-compose ps
```

## Local Development

```bash
# Build
mvn clean install -DskipTests

# Terminal 1: Eureka
cd eureka-server && mvn spring-boot:run

# Terminal 2: Gateway
cd api-gateway && mvn spring-boot:run

# Terminal 3+: Services
cd user-service && mvn spring-boot:run
```

## Ports

| Service | Port |
|---------|------|
| Eureka | 8761 |
| API Gateway | 8080 |
| User | 8082 |
| Product | 8084 |
| Order | 8083 |
| Cart | 8004 |
| Payment | 8085 |
| Notification | 8086 |
| Inventory | 8087 |

## API Gateway Routes

```
POST   /api/v1/auth/login      → User Service
GET    /api/v1/products        → Product Service
POST   /api/v1/orders          → Order Service
GET    /api/v1/carts/{id}      → Cart Service
POST   /api/v1/payments        → Payment Service
POST   /api/v1/notifications   → Notification Service
GET    /api/v1/inventory/stock → Inventory Service
```

## Authentication

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'

# Use token
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/v1/products
```

## Database

Automatic 7 PostgreSQL instances via Docker Compose:

- user_db (5432)
- product_db (5433)
- order_db (5434)
- cart_db (5435)
- payment_db (5436)
- notification_db (5437)
- inventory_db (5438)

## Health Checks

```bash
curl http://localhost:8761  # Eureka
curl http://localhost:8080/actuator/health  # Gateway
curl http://localhost:8761/eureka/apps  # Services
```

## Troubleshooting

### Services won't start
```bash
docker-compose logs
docker ps -a
lsof -i :8080
```

### Database connection fails
```bash
docker-compose logs postgres
docker-compose down -v
docker-compose up -d
```

### JWT authentication fails
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -d '{"email":"user@example.com","password":"password123"}'
```

### Eureka not showing services
```bash
docker-compose logs eureka-server
curl http://localhost:8761/eureka/apps
docker-compose restart
```

## Postman Testing

Import these collections:
- User_Service_Bulk_Upload.postman_collection.json
- PRODUCT_SERVICE_API.postman_collection.json
- CART_SERVICE_API.postman_collection.json
- Order_Service_Bulk_Upload_20Orders.postman_collection.json
- PAYMENT_SERVICE_Postman_Collection.json
- NOTIFICATION_SERVICE_Postman_Collection.json
- INVENTORY_SERVICE_Postman_Collection.json

## Useful Commands

```bash
# Logs
docker-compose logs -f [service]

# Database access
psql -h localhost -p 5432 -U postgres -d user_db

# View services
curl http://localhost:8761/eureka/apps

# Metrics
curl http://localhost:8080/actuator/metrics

# Stop specific service
docker-compose stop user-service

# Rebuild service
docker-compose build user-service
docker-compose up -d user-service
```

## Environment Variables

Create .env:
```env
JWT_SECRET=your-secret-key-min-32-chars
JWT_EXPIRATION=86400000
DB_USER=postgres
DB_PASSWORD=postgres
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
```

## Project Structure

```
├── eureka-server/
├── api-gateway/
├── common-config/
├── user-service/
├── product-service/
├── order-service/
├── cart-service/
├── payment-service/
├── notification-service/
├── inventory-service/
├── docker-compose.yml
├── start.sh
├── README.md
└── SETUP_GUIDE.md
```

**Version:** 1.0.0 | **Date:** April 5, 2026
