package dev.abhishek.ecommerce.modules.order.controller;

import dev.abhishek.ecommerce.modules.order.dto.CreateOrderRequest;
import dev.abhishek.ecommerce.modules.order.dto.OrderDto;
import dev.abhishek.ecommerce.modules.order.misc.StatusChoice;
import dev.abhishek.ecommerce.modules.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import dev.abhishek.ecommerce.common.dto.PagedResponse;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@Tag(name = "Orders", description = "Endpoints for managing orders")

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get all user orders", description = "Retrieves all orders placed by the current user")
    @GetMapping
    public ResponseEntity<PagedResponse<OrderDto>> getAllUserOrders(
            @RequestParam(required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(orderService.getAllUserOrders(PageRequest.of(pageNo, pageSize)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all orders (Admin)", description = "Retrieves all orders in the system (Admin only)")
    @GetMapping("/all")
    public ResponseEntity<PagedResponse<OrderDto>> getAllOrders(
            @RequestParam(required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(orderService.getAllOrders(PageRequest.of(pageNo, pageSize)));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get user order", description = "Retrieves a specific order placed by the current user")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getUserOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getUserOrder(orderId));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create order", description = "Creates a new order for the current user")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody(required = true) CreateOrderRequest request) {
        return new ResponseEntity<>(orderService.createOrder(request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Cancel order", description = "Cancels a specific order placed by the current user")
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    //update order status
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status", description = "Updates the status of an order (Admin only)")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam StatusChoice status,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status, userId));
    }
}
