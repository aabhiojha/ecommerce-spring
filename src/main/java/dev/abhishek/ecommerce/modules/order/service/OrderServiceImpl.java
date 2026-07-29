package dev.abhishek.ecommerce.modules.order.service;

import dev.abhishek.ecommerce.common.exceptions.InsufficientProductInventoryException;
import dev.abhishek.ecommerce.common.exceptions.ResourceNotFoundException;
import dev.abhishek.ecommerce.modules.image.entity.Image;
import dev.abhishek.ecommerce.modules.cart.entity.CartItem;
import dev.abhishek.ecommerce.modules.cart.repository.CartItemRepository;
import dev.abhishek.ecommerce.modules.order.dto.CreateOrderRequest;
import dev.abhishek.ecommerce.modules.order.dto.OrderDto;
import dev.abhishek.ecommerce.modules.order.entity.Order;
import dev.abhishek.ecommerce.modules.order.entity.OrderItem;
import dev.abhishek.ecommerce.modules.order.mapper.OrderMapper;
import dev.abhishek.ecommerce.modules.order.misc.StatusChoice;
import dev.abhishek.ecommerce.modules.order.repository.OrderRepository;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.user.model.User;
import dev.abhishek.ecommerce.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import dev.abhishek.ecommerce.common.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderDto> getAllUserOrders(Pageable pageable) {
        User user = getUser();
        Page<Order> page = orderRepository.findAllByUserOrderByCreatedAtDesc(user, pageable);
        return new PagedResponse<>(orderMapper.toOrderDtoList(page.getContent()), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderDto> getAllOrders(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        return new PagedResponse<>(orderMapper.toOrderDtoList(page.getContent()), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getUserOrder(UUID orderId) {
        return orderMapper.toOrderDto(getUserOrder(orderId, getUser()));
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderRequest request) {
        User user = getUser();
        List<CartItem> cartItems = getCartItemsForOrder(request, user);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("No cart items available to create an order");
        }

        Order order = Order.builder()
                .user(user)
                .status(StatusChoice.PLACED)
                .orderItems(new ArrayList<>())
                .totalPrice(BigDecimal.ZERO)
                .build();

        // Hibernate owns this collection, so it has to stay mutable.
        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> toOrderItem(order, cartItem))
                .collect(Collectors.toCollection(ArrayList::new));

        order.getOrderItems().addAll(orderItems);
        order.setTotalPrice(orderItems.stream()
                .map(item -> item.getPrice_at_purchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        Order savedOrder = orderRepository.save(order);
        // The ordered items leave the cart; a future is_ordered flag would let us keep them for recommendations.
        cartItemRepository.deleteAll(cartItems);

        log.info("Order {} created for user {}", savedOrder.getId(), user.getUsername());
        return orderMapper.toOrderDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto cancelOrder(UUID orderId) {
        User user = getUser();
        Order order = getUserOrder(orderId, user);

        if (order.getStatus() == StatusChoice.CANCELLED) {
            return orderMapper.toOrderDto(order);
        }

        if (order.getStatus() == StatusChoice.DELIVERED) {
            throw new IllegalArgumentException("A delivered order can no longer be cancelled");
        }

        // Put the reserved stock back.
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            if (product != null && product.getInventory() != null) {
                product.setInventory(product.getInventory() + item.getQuantity());
            }
        }

        order.setStatus(StatusChoice.CANCELLED);
        log.info("Order {} cancelled for user {}", order.getId(), user.getUsername());
        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(UUID orderId, StatusChoice status, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Order order = getUserOrder(orderId, user);
        order.setStatus(status);
        log.info("Order {} moved to status {}", orderId, status);
        return orderMapper.toOrderDto(order);
    }

    // helper functions
    private User getUser() {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        log.debug("User : {} fetched", user.getUsername());
        return user;
    }

    private List<CartItem> getCartItemsForOrder(CreateOrderRequest request, User user) {
        List<Long> cartItemIds = request == null ? null : request.getCartItemIds();

        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return cartItemRepository.findAllByCart_User(user);
        }

        return cartItemIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(cartItemId -> cartItemRepository.findByIdAndCart_User(cartItemId, user)
                        .orElseThrow(() -> new ResourceNotFoundException("Cart item not found in your cart: id: " + cartItemId)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private OrderItem toOrderItem(Order order, CartItem cartItem) {
        Product product = cartItem.getProduct();
        validateCartItem(cartItem, product);

        // Stock is always decremented; previously an order for the full remaining stock left inventory untouched.
        product.setInventory(product.getInventory() - cartItem.getQuantity());

        return OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(cartItem.getQuantity())
                .price_at_purchase(product.getPrice())
                .productName(product.getName())
                .productBrand(product.getBrand())
                .productDescription(product.getDescription())
                .productImage(getFirstProductImage(product))
                .build();
    }

    private String getFirstProductImage(Product product) {
        List<Image> images = product.getImages();
        if (images == null || images.isEmpty() || images.getFirst() == null) {
            return null;
        }

        return images.getFirst().getDownloadUrl();
    }

    private Order getUserOrder(UUID orderId, User user) {
        return orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for user: id: " + orderId));
    }

    private void validateCartItem(CartItem cartItem, Product product) {
        if (cartItem.getQuantity() == null || cartItem.getQuantity() <= 0) {
            throw new IllegalArgumentException("Cart item quantity must be greater than zero: id: " + cartItem.getId());
        }

        if (product == null) {
            throw new ResourceNotFoundException("Product not found for cart item: id: " + cartItem.getId());
        }

        if (product.getPrice() == null) {
            throw new IllegalArgumentException("Product price is missing for cart item: id: " + cartItem.getId());
        }

        if (product.getInventory() == null || product.getInventory() < cartItem.getQuantity()) {
            throw new InsufficientProductInventoryException(
                    "Requested quantity exceeds available inventory for product: " + product.getName());
        }
    }
}
