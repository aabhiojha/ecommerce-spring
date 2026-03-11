package dev.abhishek.ecommerce.modules.order.service;

import dev.abhishek.ecommerce.common.exceptions.ResourceNotFoundException;
import dev.abhishek.ecommerce.modules.Image.entity.Image;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Override
    public List<OrderDto> getAllUserOrders() {
        User user = getUser();
        List<Order> allByUserOrderByCreatedAtDesc = orderRepository.findAllByUserOrderByCreatedAtDesc(user);
        return orderMapper.toOrderDtoList(allByUserOrderByCreatedAtDesc);
    }

    @Override
    public OrderDto getUserOrder(UUID orderId) {
        User user = getUser();
        return orderMapper.toOrderDto(getUserOrder(orderId, user));
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
                .build();

        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> toOrderItem(order, cartItem))
                .toList();

        order.setOrderItems(orderItems);
        order.setTotalPrice(orderItems.stream()
                .map(item -> item.getPrice_at_purchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // we need to update product inventory as we place the orders
        

        Order savedOrder = orderRepository.save(order);
        // this is fine for now, later I should add a field called is_ordered field in cart items
        // fetching for cart items from user side should be done by using this isEnabled flag
        // for now passed cart items are straight up deleted from the cart
        // later for recommendation system, I could do use the items in cart to recommend similar type of products to the user
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

        order.setStatus(StatusChoice.CANCELLED);
        log.info("Order {} cancelled for user {}", order.getId(), user.getUsername());
        return orderMapper.toOrderDto(order);
    }

    // helper functions
    private User getUser() {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        log.info("User : {} fetched", user.getUsername());
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

        if (product.getInventory() > cartItem.getQuantity()){
            product.setInventory(product.getInventory()-cartItem.getQuantity());
        }

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
    }
}
