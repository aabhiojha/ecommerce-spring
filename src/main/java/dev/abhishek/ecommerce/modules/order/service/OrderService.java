package dev.abhishek.ecommerce.modules.order.service;

import dev.abhishek.ecommerce.modules.order.dto.CreateOrderRequest;
import dev.abhishek.ecommerce.modules.order.dto.OrderDto;
import dev.abhishek.ecommerce.modules.order.misc.StatusChoice;

import dev.abhishek.ecommerce.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface OrderService {

    PagedResponse<OrderDto> getAllUserOrders(Pageable pageable);

    PagedResponse<OrderDto> getAllOrders(Pageable pageable);

    OrderDto getUserOrder(UUID orderId);

    OrderDto createOrder(CreateOrderRequest request);

    OrderDto cancelOrder(UUID orderId);

    OrderDto updateOrderStatus(UUID orderId, StatusChoice status, Long userId);
}
