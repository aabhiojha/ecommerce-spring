package dev.abhishek.ecommerce.modules.order.service;

import dev.abhishek.ecommerce.modules.order.dto.CreateOrderRequest;
import dev.abhishek.ecommerce.modules.order.dto.OrderDto;

import java.util.List;

public interface OrderService {
    List<OrderDto> getAllUserOrders();
    OrderDto createOrder(CreateOrderRequest request) throws Exception;

}
