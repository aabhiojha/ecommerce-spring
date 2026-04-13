# Frontend API Guide

This guide documents frontend integration for the current backend codebase.
The canonical machine-readable contract is in [`docs/openapi.yaml`](docs/openapi.yaml).

## Base Setup

- Base URL: `http://localhost:8080` (local)
- API prefix: `/api`
- Swagger docs:
  - `GET /v3/api-docs`
  - `/swagger-ui/index.html`
- Auth: JWT bearer token
- Header: `Authorization: Bearer <token>`
- JSON content type: `application/json`
- Image upload content type: `multipart/form-data`

## Roles

- `ROLE_CUSTOMER`
- `ROLE_SELLER`
- `ROLE_ADMIN`

Login response includes roles:

```json
{
  "token": "jwt-token",
  "username": "alice",
  "roles": ["ROLE_CUSTOMER"]
}
```

## Access Rules (Important)

From `SecurityConfig`, only these are public:

- `POST /api/auth/**`
- `GET /api/products/**`
- `GET /api/categories/**`
- Swagger/OpenAPI endpoints

Everything else requires authentication at the filter level.
After that, some endpoints have additional method-level role checks (`@PreAuthorize`).

## Error Handling Contract

Typical `400` response body:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Requested quantity exceeds available stock",
  "path": "/api/carts/cart"
}
```

Known inconsistency for `404`: body is returned as a stringified map, not valid JSON.
Frontend should handle both JSON and plain text safely.

## Endpoints

### Auth

#### `POST /api/auth/register`

- Access: public
- Body:

```json
{
  "username": "alice",
  "password": "secret123",
  "email": "alice@example.com"
}
```

- Response: `200 OK` with empty body

#### `POST /api/auth/login`

- Access: public
- Body:

```json
{
  "username": "alice",
  "password": "secret123"
}
```

- Response:

```json
{
  "token": "jwt-token",
  "username": "alice",
  "roles": ["ROLE_CUSTOMER"]
}
```

#### `POST /api/auth/password-reset`

- Access: public
- Body:

```json
{
  "email": "alice@example.com"
}
```

- Response: `200 OK`

#### `POST /api/auth/password-reset-confirm`

- Access: public
- Body:

```json
{
  "token": 12345,
  "password": "newPassword123"
}
```

- Response: `200 OK`

### User

#### `GET /api/user/me`

- Access: authenticated
- Response (`UserDto`):

```json
{
  "id": 1,
  "userName": "alice",
  "email": "alice@example.com",
  "enabled": true,
  "roles": ["ROLE_CUSTOMER"],
  "createdAt": "2026-03-12T10:00:00",
  "updatedAt": "2026-03-12T10:00:00"
}
```

#### `PATCH /api/user/me`

- Access: authenticated
- Body (`UpdateUserDto`):

```json
{
  "userName": "alice2",
  "email": "alice2@example.com"
}
```

#### `GET /api/user`

- Access: admin only
- Response: `UserDto[]`

#### `GET /api/user/{userId}`

- Access: admin only
- Response: `UserDto`

#### `DELETE /api/user/{userId}`

- Access: admin only
- Response: `204 No Content`

### Categories

#### `GET /api/categories`

- Access: public
- Response: `CategoryDto[]`

#### `POST /api/categories`

- Access: admin only
- Body:

```json
{
  "name": "Electronics"
}
```

- Response: `201 Created`

#### `PUT /api/categories/{id}`

- Access: admin only
- Body:

```json
{
  "name": "Updated Category"
}
```

- Response: `200 OK`

#### `DELETE /api/categories/{id}`

- Access: admin only
- Response: `200 OK`

### Products

#### `GET /api/products`

- Access: public
- Query params:
  - `pageNo` (default `0`)
  - `pageSize` (default `20`)
  - `sortBy` (default `Id`)
  - `sortDir` (`ASC` or `DESC`)
  - `id`
  - `name`
  - `description`
  - `seller_id` (currently not applied in service filter)
  - `category_id` (currently not applied in service filter)
- Response: `ProductDto[]`

#### `GET /api/products/{productId}`

- Access: public
- Response: `ProductDto`

#### `POST /api/products`

- Access: seller or admin
- Body (`CreateProductRequest`):

```json
{
  "name": "USB-C Fast Charger",
  "brand": "VoltEdge",
  "price": 19.99,
  "inventory": 50,
  "description": "30W wall charger",
  "category_id": 1
}
```

- Response: `201 Created` with `ProductDto`

#### `PATCH /api/products/{productId}`

- Access: seller only
- Body (`UpdateProductRequest`, partial):

```json
{
  "name": "Updated name",
  "price": 24.99,
  "inventory": 60
}
```

- Response: `200 OK` with `ProductDto`

### Images

#### `POST /api/images/upload`

- Access: seller only
- Content type: `multipart/form-data`
- Form fields:
  - `file` (binary)
  - `productId` (number)
- Response body: `"Uploaded successfully"`

#### `POST /api/images/upload-multiple`

- Access: seller only
- Content type: `multipart/form-data`
- Form fields:
  - `files[]` or repeated `files` parts (binary)
  - `productId` (number)
- Response: `ImageDto[]`

#### `GET /api/images`

- Access: seller or admin
- Response: `ImageDto[]`

### Cart (Customer)

#### `GET /api/carts/cart`

- Access: customer only
- Response: `CartDto`

#### `GET /api/carts/cart/summary`

- Access: customer only
- Response: `CartSummaryDto`

#### `POST /api/carts/cart`

- Access: customer only
- Body:

```json
{
  "productId": 10,
  "quantity": 2
}
```

- Response: `CartItemDto`

#### `PUT /api/carts/cart/{cartItemId}`

- Access: customer only
- Body:

```json
{
  "quantity": 4
}
```

- Response: `200 OK`

#### `DELETE /api/carts/cart/{cartItemId}`

- Access: customer only
- Response: `200 OK`

#### `DELETE /api/carts/cart`

- Access: customer only
- Response: `204 No Content`

#### `POST /api/carts/cart/validate`

- Access: customer only
- Body: none
- Response: `CartValidationDto`

### Orders

#### `GET /api/orders`

- Access: customer only
- Response: `OrderDto[]`

#### `GET /api/orders/{orderId}`

- Access: customer only
- Response: `OrderDto`

#### `POST /api/orders`

- Access: customer only
- Body (`CreateOrderRequest`, optional):

```json
{
  "cartItemIds": [1, 2, 3]
}
```

- Response: `201 Created` with `OrderDto`

#### `PATCH /api/orders/{orderId}/cancel`

- Access: customer only
- Response: `OrderDto`

#### `PATCH /api/orders/updateStatus/{userId}/{orderId}?status=DELIVERED`

- Access: admin only
- Query param:
  - `status` enum: `PLACED`, `CONFIRMED`, `DELIVERED`, `CANCELLED`
- Response: `200 OK`

### Payments

#### `POST /api/payments/checkout`

- Access: customer only
- Body:

```json
{
  "currency": "usd",
  "cartItemIds": [1, 2]
}
```

- Notes:
  - Omit `cartItemIds` or send an empty array to charge the entire cart.
  - `amount` in the response is in the smallest currency unit. For USD, `1200` means `$12.00`.
  - Open `checkoutUrl` in a browser to pay on Stripe-hosted checkout.
- Response:

```json
{
  "paymentId": 1,
  "checkoutSessionId": "cs_test_123",
  "checkoutUrl": "https://checkout.stripe.com/c/pay/cs_test_123",
  "paymentIntentId": null,
  "clientSecret": null,
  "status": "unpaid",
  "amount": 1200,
  "currency": "usd",
  "orderId": null
}
```

#### `GET /api/payments/checkout/success?session_id={CHECKOUT_SESSION_ID}`

- Access: public
- Stripe redirects here after successful hosted checkout.
- Response:
  - Returns the updated payment payload.
  - `orderId` is populated once Stripe reports the checkout session as paid and the backend creates the order.

#### `GET /api/payments/checkout/cancel`

- Access: public
- Stripe redirects here if the customer cancels checkout.

#### `GET /api/payments/checkout/{checkoutSessionId}`

- Access: customer only
- Response:
  - Returns the current locally stored payment state for that checkout session.

### Reviews

#### `GET /api/reviews/{productId}`

- Access: authenticated (not public in current security config)
- Response: `ReviewDto[]`

#### `GET /api/reviews/user`

- Access: customer only
- Response: `ReviewDto[]`

#### `POST /api/reviews`

- Access: customer only
- Body:

```json
{
  "productId": 10,
  "orderId": "8308da3e-502b-4215-ac1d-39e12b391e43",
  "reviewMessage": "Fast delivery and product quality is good.",
  "rating": 5
}
```

- Response: `201 Created` with `ReviewDto`

#### `DELETE /api/reviews/{reviewId}`

- Access: customer only
- Response: currently `201 Created` (treat any `2xx` as success)

#### `DELETE /api/reviews/user/{reviewId}?user_id=1`

- Access: admin only
- Query param: `user_id`
- Response: `200 OK`

## Frontend Integration Notes

- Do not assume `register` returns token; always redirect to login.
- Centralize auth token injection and `401` handling.
- Use role-based route guards from `roles`.
- Treat empty `200` responses as successful operations.
- Parse `404` body defensively because it may be plain text.
- Re-fetch cart summary after cart mutations.

## Suggested API Client Pattern (Axios)

```ts
import axios from "axios";

export const api = axios.create({
  baseURL: "http://localhost:8080",
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
```
