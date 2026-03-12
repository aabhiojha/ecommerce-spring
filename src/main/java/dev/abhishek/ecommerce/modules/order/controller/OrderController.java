package dev.abhishek.ecommerce.modules.order.controller;

import dev.abhishek.ecommerce.modules.order.dto.CreateOrderRequest;
import dev.abhishek.ecommerce.modules.order.dto.OrderDto;
import dev.abhishek.ecommerce.modules.order.misc.StatusChoice;
import dev.abhishek.ecommerce.modules.order.service.OrderServiceImpl;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.simpleframework.xml.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllUserOrders() {
        return ResponseEntity.ok(orderService.getAllUserOrders());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getUserOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getUserOrder(orderId));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody(required = false) CreateOrderRequest request) {
        return new ResponseEntity<>(orderService.createOrder(request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    //update order status
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/updateStatus/{userId}/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable UUID orderId, @RequestParam StatusChoice status, @PathVariable Long userId) {
        orderService.updateOrderStatus(orderId, status, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
