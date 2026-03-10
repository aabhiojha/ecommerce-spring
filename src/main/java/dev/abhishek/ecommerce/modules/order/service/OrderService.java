package dev.abhishek.ecommerce.modules.order.service;

import dev.abhishek.ecommerce.modules.order.dto.CreateOrderRequest;
import dev.abhishek.ecommerce.modules.order.dto.OrderDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderDto> getAllUserOrders();
    OrderDto getUserOrder(UUID orderId);
    OrderDto createOrder(CreateOrderRequest request);
    OrderDto cancelOrder(UUID orderId);

}
