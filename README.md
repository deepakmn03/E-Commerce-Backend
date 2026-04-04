# E-Commerce Backend - Microservices Platform

A production-ready, fully-integrated microservices E-Commerce platform built with Spring Boot 4.0.3, Spring Cloud 2025.1.0, and Java 21.

## 🚀 Quick Start

```bash
./start.sh
# or
docker-compose up -d
```

Access:
- API Gateway: http://localhost:8080
- Eureka Dashboard: http://localhost:8761

## 📋 System Overview

| Component | Port | Purpose |
|-----------|------|---------|
| API Gateway | 8080 | Central routing, auth, rate limiting |
| Eureka Server | 8761 | Service discovery |
| User Service | 8082 | Authentication, user management |
| Product Service | 8084 | Product catalog, search |
| Order Service | 8083 | Order management, tracking |
| Cart Service | 8004 | Shopping cart operations |
| Payment Service | 8085 | Payment processing |
| Notification Service | 8086 | Multi-channel notifications |
| Inventory Service | 8087 | Stock management |

## 🏗️ Architecture

Client → API Gateway (8080) → Eureka Server (8761) → Microservices Cluster

## 💾 Technology Stack

- Framework: Spring Boot 4.0.3
- Cloud: Spring Cloud 2025.1.0
- Language: Java 21
- Database: PostgreSQL 16
- Container: Docker & Docker Compose
- Security: JWT (JJWT 0.12.3)

## 📚 Documentation

**[SETUP_GUIDE.md](SETUP_GUIDE.md)** - Complete setup, deployment, troubleshooting

## 🔧 Common Commands

```bash
docker-compose up -d           # Start
docker-compose down            # Stop
docker-compose ps             # Status
docker-compose logs -f        # Logs
```

## 🔐 Authentication

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -d '{"email":"user@example.com","password":"password123"}'
```

## 🛠️ Development

Prerequisites: Docker, Java 21+, Maven 3.9+

```bash
mvn clean install -DskipTests
```

## 📊 Monitoring

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8761/eureka/apps
```

## 🐛 Troubleshooting

See SETUP_GUIDE.md for detailed help.

## 📈 Features

- ✅ Microservices Architecture
- ✅ Service Discovery (Eureka)
- ✅ API Gateway
- ✅ JWT Authentication
- ✅ Docker Containerization
- ✅ Circuit Breaker Patterns
- ✅ Rate Limiting

## 📁 Project Structure

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

## 📄 License

Proprietary - E-Commerce Backend System

Version: 1.0.0 | Status: ✅ Phase 1 Complete | Date: April 5, 2026
