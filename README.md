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

- JWT Authentication
- Role-based Access Control (Customer, Seller, Admin)
- Complete Product, Category, Cart, and Order Management
- Stripe Payment Gateway Integration
- Transactional Order Processing with inventory checks
- Product Image Upload to RustFS (S3-compatible object storage)
- Password reset by emailed one-time code
- Global Error Responses
- OpenAPI (Swagger) Documentation
- Docker Support

## 🛠️ Tech Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3, Spring Security, Spring Data JPA
- **Database**: MySQL
- **Cache**: Redis
- **Object storage**: RustFS (S3-compatible, accessed with the AWS SDK v2)
- **Authentication**: JWT
- **Payment**: Stripe
- **Documentation**: Springdoc OpenAPI
- **Build**: Maven
- **Container**: Docker & Docker Compose

## 🏃‍♂️ Quick Start

### Using Docker (Recommended)

```bash
cp .env.example .env
# JWT_SECRET is required - generate one:
openssl rand -base64 48

docker compose up --build
```

Services started:

| Service | URL | Notes |
| --- | --- | --- |
| API | http://localhost:8180 | Swagger UI at `/swagger-ui.html` |
| RustFS S3 API | http://localhost:9000 | bucket `ecommerce`, created on startup |
| RustFS console | http://localhost:9001 | login with `RUSTFS_ACCESS_KEY` / `RUSTFS_SECRET_KEY` |
| MySQL | localhost:3307 | |
| Redis | localhost:6380 | |
| Mailpit (dev SMTP) | http://localhost:8025 | catches outgoing mail |

Set `SQL_INIT_MODE=always` in `.env` on the first run to load the demo catalogue in `data.sql`.

### Running locally

Every setting in `src/main/resources/application.properties` is overridable by an environment
variable; only `JWT_SECRET` has no default and must be set.

```bash
export JWT_SECRET="$(openssl rand -base64 48)"
./mvnw spring-boot:run
```

### Object storage

Images go to RustFS through the S3 API, so the client is configured with an endpoint override
and path-style addressing (`storage.*` properties). Objects are keyed `products/{productId}/{uuid}-{filename}`
and served from `RUSTFS_PUBLIC_URL`, which is the address the *browser* uses - inside compose the
app itself talks to `http://rustfs:9000`.
