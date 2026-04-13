# E-Commerce Spring Boot Backend

A robust, scalable, and production-ready **e-commerce REST API** built with **Spring Boot**. Supports multiple user roles (Customer, Seller, Admin), secure JWT authentication, shopping cart, order management, Stripe payments, and more.

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3+-6DB33F?logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?logo=json-web-tokens)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)

## ✨ Live Demo

**Swagger UI**: [https://ecommerceapi.abhishekojha.com.np/swagger-ui/index.html](https://ecommerceapi.abhishekojha.com.np/swagger-ui/index.html)

## 🚀 Features

- JWT Authentication with Refresh Tokens & Token Blacklisting
- Role-based Access Control (Customer, Seller, Admin)
- Complete Product, Category, Cart, and Order Management
- Stripe Payment Gateway Integration
- Optimistic Locking to Prevent Overselling
- Transactional Order Processing
- Product Image Upload (S3/MinIO compatible)
- Advanced Exception Handling & Global Error Responses
- OpenAPI (Swagger) Documentation
- Docker Support

## 🛠️ Tech Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3, Spring Security, Spring Data JPA
- **Database**: MySQL
- **Cache**: Redis
- **Authentication**: JWT
- **Payment**: Stripe
- **Documentation**: Springdoc OpenAPI
- **Build**: Maven
- **Container**: Docker & Docker Compose

## 🏃‍♂️ Quick Start

### Using Docker (Recommended)

```bash
docker-compose up --build
