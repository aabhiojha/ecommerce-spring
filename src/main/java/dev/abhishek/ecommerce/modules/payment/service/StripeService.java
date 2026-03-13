package dev.abhishek.ecommerce.modules.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import dev.abhishek.ecommerce.common.exceptions.ResourceNotFoundException;
import dev.abhishek.ecommerce.modules.Image.entity.Image;
import dev.abhishek.ecommerce.modules.cart.entity.CartItem;
import dev.abhishek.ecommerce.modules.cart.repository.CartItemRepository;
import dev.abhishek.ecommerce.modules.order.entity.Order;
import dev.abhishek.ecommerce.modules.order.entity.OrderItem;
import dev.abhishek.ecommerce.modules.order.misc.StatusChoice;
import dev.abhishek.ecommerce.modules.order.repository.OrderRepository;
import dev.abhishek.ecommerce.modules.payment.dto.PaymentLineItemSnapshot;
import dev.abhishek.ecommerce.modules.payment.dto.PaymentRequest;
import dev.abhishek.ecommerce.modules.payment.dto.PaymentResponse;
import dev.abhishek.ecommerce.modules.payment.entity.Payment;
import dev.abhishek.ecommerce.modules.payment.repository.PaymentRepository;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.product.repository.ProductRepository;
import dev.abhishek.ecommerce.modules.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeService {
    private static final String CHECKOUT_PLACEHOLDER_PREFIX = "checkout_placeholder:";

    private final CartItemRepository cartItemRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @Value("${stripe.secret.key:}")
    private String stripeSecretKey;

    @Value("${stripe.checkout.success-url:http://localhost:8080/api/payments/checkout/success?session_id={CHECKOUT_SESSION_ID}}")
    private String checkoutSuccessUrl;

    @Value("${stripe.checkout.cancel-url:http://localhost:8080/api/payments/checkout/cancel}")
    private String checkoutCancelUrl;

    @Transactional
    public PaymentResponse createCheckoutSession(PaymentRequest request) {
        User user = getUser();
        List<CartItem> cartItems = getCartItemsForPayment(request, user);
        List<PaymentLineItemSnapshot> lineItems = cartItems.stream()
                .map(this::toSnapshot)
                .toList();

        String currency = request.getCurrency().toLowerCase(Locale.ROOT);
        long amount = calculateAmount(lineItems);
        Session session = createStripeCheckoutSession(lineItems, currency, user);
        String sessionPaymentIntentId = extractPaymentIntentId(session.getPaymentIntent());

        Payment payment = paymentRepository.save(Payment.builder()
                .checkoutSessionId(session.getId())
                .checkoutUrl(session.getUrl())
                .paymentIntentId(persistedValueOrPlaceholder(sessionPaymentIntentId, session.getId() + ":pi"))
                .clientSecret(persistedValueOrPlaceholder(null, session.getId() + ":secret"))
                .status(session.getPaymentStatus())
                .amount(amount)
                .currency(currency)
                .lineItemsJson(serializeLineItems(lineItems))
                .user(user)
                .build());

        log.info("Checkout session {} created for user {}", payment.getCheckoutSessionId(), user.getUsername());
        return toResponse(payment);
    }

    @Transactional
    public PaymentResponse confirmCheckoutSession(String checkoutSessionId) {
        Payment payment = getPaymentByCheckoutSessionId(checkoutSessionId);
        Session session = retrieveCheckoutSession(checkoutSessionId);
        syncPaymentFromSession(payment, session);

        if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
            return toResponse(payment);
        }

        if (payment.getOrder() == null) {
            Order order = createOrderFromPayment(payment, deserializeLineItems(payment.getLineItemsJson()), payment.getUser());
            payment.setOrder(order);
        }

        log.info("Checkout session {} confirmed", checkoutSessionId);
        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getCheckoutStatus(String checkoutSessionId) {
        User user = getUser();
        Payment payment = paymentRepository.findByCheckoutSessionIdAndUser(checkoutSessionId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for checkout session: " + checkoutSessionId));
        return toResponse(payment);
    }

    private Session createStripeCheckoutSession(List<PaymentLineItemSnapshot> lineItems, String currency, User user) {
        ensureStripeConfigured();

        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(checkoutSuccessUrl)
                .setCancelUrl(checkoutCancelUrl)
                .putMetadata("userId", String.valueOf(user.getId()));

        for (PaymentLineItemSnapshot lineItem : lineItems) {
            builder.addLineItem(SessionCreateParams.LineItem.builder()
                    .setQuantity(lineItem.getQuantity())
                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(currency)
                            .setUnitAmount(toMinorUnit(lineItem.getUnitPrice()))
                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(lineItem.getProductName())
                                    .setDescription(lineItem.getProductDescription())
                                    .build())
                            .build())
                    .build());
        }

        try {
            return Session.create(builder.build(), buildRequestOptions());
        } catch (StripeException ex) {
            throw new IllegalArgumentException("Failed to create Stripe checkout session: " + ex.getMessage(), ex);
        }
    }

    private Session retrieveCheckoutSession(String checkoutSessionId) {
        ensureStripeConfigured();

        try {
            return Session.retrieve(checkoutSessionId, buildRequestOptions());
        } catch (StripeException ex) {
            throw new IllegalArgumentException("Failed to retrieve Stripe checkout session: " + ex.getMessage(), ex);
        }
    }

    private PaymentIntent retrievePaymentIntent(String paymentIntentId) {
        if (paymentIntentId == null || paymentIntentId.isBlank()) {
            return null;
        }

        ensureStripeConfigured();

        try {
            return PaymentIntent.retrieve(paymentIntentId, buildRequestOptions());
        } catch (StripeException ex) {
            throw new IllegalArgumentException("Failed to retrieve Stripe payment intent: " + ex.getMessage(), ex);
        }
    }

    private RequestOptions buildRequestOptions() {
        return RequestOptions.builder()
                .setApiKey(stripeSecretKey)
                .build();
    }

    private void ensureStripeConfigured() {
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new IllegalStateException("Stripe secret key is not configured");
        }
    }

    private List<CartItem> getCartItemsForPayment(PaymentRequest request, User user) {
        List<Long> cartItemIds = request.getCartItemIds();

        if (cartItemIds == null || cartItemIds.isEmpty()) {
            List<CartItem> cartItems = cartItemRepository.findAllByCart_User(user);
            if (cartItems.isEmpty()) {
                throw new IllegalArgumentException("No cart items available for payment");
            }
            return cartItems;
        }

        LinkedHashSet<Long> uniqueIds = new LinkedHashSet<>(cartItemIds);
        List<CartItem> cartItems = uniqueIds.stream()
                .map(id -> cartItemRepository.findByIdAndCart_User(id, user)
                        .orElseThrow(() -> new ResourceNotFoundException("Cart item not found in your cart: id: " + id)))
                .toList();

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("No cart items available for payment");
        }

        return cartItems;
    }

    private PaymentLineItemSnapshot toSnapshot(CartItem cartItem) {
        Product product = cartItem.getProduct();
        validateCartItem(cartItem, product);

        return PaymentLineItemSnapshot.builder()
                .cartItemId(cartItem.getId())
                .productId(product.getId())
                .quantity(cartItem.getQuantity())
                .unitPrice(product.getPrice())
                .productName(product.getName())
                .productBrand(product.getBrand())
                .productDescription(product.getDescription())
                .productImage(getFirstProductImage(product))
                .build();
    }

    private long calculateAmount(List<PaymentLineItemSnapshot> lineItems) {
        return lineItems.stream()
                .map(this::calculateLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .movePointRight(2)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    private BigDecimal calculateLineTotal(PaymentLineItemSnapshot item) {
        return item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    private long toMinorUnit(BigDecimal amount) {
        return amount.movePointRight(2)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    private String serializeLineItems(List<PaymentLineItemSnapshot> lineItems) {
        try {
            return objectMapper.writeValueAsString(lineItems);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize payment line items", ex);
        }
    }

    private List<PaymentLineItemSnapshot> deserializeLineItems(String lineItemsJson) {
        try {
            return objectMapper.readValue(lineItemsJson, new TypeReference<>() {});
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to deserialize payment line items", ex);
        }
    }

    private Payment getPaymentByCheckoutSessionId(String checkoutSessionId) {
        return paymentRepository.findByCheckoutSessionId(checkoutSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for checkout session: " + checkoutSessionId));
    }

    private void syncPaymentFromSession(Payment payment, Session session) {
        payment.setCheckoutSessionId(session.getId());
        payment.setCheckoutUrl(session.getUrl());
        payment.setStatus(session.getPaymentStatus());

        String paymentIntentId = extractPaymentIntentId(session.getPaymentIntent());
        if (paymentIntentId != null) {
            payment.setPaymentIntentId(paymentIntentId);
            PaymentIntent paymentIntent = retrievePaymentIntent(paymentIntentId);
            if (paymentIntent != null) {
                payment.setClientSecret(persistedValueOrPlaceholder(paymentIntent.getClientSecret(), payment.getCheckoutSessionId() + ":secret"));
                payment.setAmount(paymentIntent.getAmount());
                payment.setCurrency(paymentIntent.getCurrency());
            }
        }
    }

    private String extractPaymentIntentId(Object paymentIntentValue) {
        if (paymentIntentValue == null) {
            return null;
        }

        if (paymentIntentValue instanceof String paymentIntentId) {
            return paymentIntentId;
        }

        if (paymentIntentValue instanceof PaymentIntent paymentIntent) {
            return paymentIntent.getId();
        }

        return paymentIntentValue.toString();
    }

    private Order createOrderFromPayment(Payment payment, List<PaymentLineItemSnapshot> lineItems, User user) {
        List<OrderItem> orderItems = new ArrayList<>();
        Order order = Order.builder()
                .user(user)
                .status(StatusChoice.PLACED)
                .orderItems(orderItems)
                .totalPrice(BigDecimal.ZERO)
                .build();

        for (PaymentLineItemSnapshot lineItem : lineItems) {
            Product product = productRepository.findById(lineItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: id: " + lineItem.getProductId()));
            validateSnapshotAgainstInventory(lineItem, product);

            product.setInventory(product.getInventory() - lineItem.getQuantity());

            orderItems.add(OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(lineItem.getQuantity())
                    .price_at_purchase(lineItem.getUnitPrice())
                    .productName(lineItem.getProductName())
                    .productBrand(lineItem.getProductBrand())
                    .productDescription(lineItem.getProductDescription())
                    .productImage(lineItem.getProductImage())
                    .build());
        }

        order.setTotalPrice(lineItems.stream()
                .map(this::calculateLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        Order savedOrder = orderRepository.save(order);
        removePurchasedCartItems(lineItems, user);
        return savedOrder;
    }

    private void removePurchasedCartItems(List<PaymentLineItemSnapshot> lineItems, User user) {
        List<CartItem> cartItemsToDelete = lineItems.stream()
                .map(PaymentLineItemSnapshot::getCartItemId)
                .map(cartItemId -> cartItemRepository.findByIdAndCart_User(cartItemId, user).orElse(null))
                .filter(cartItem -> cartItem != null)
                .toList();

        if (!cartItemsToDelete.isEmpty()) {
            cartItemRepository.deleteAll(cartItemsToDelete);
        }
    }

    private void validateCartItem(CartItem cartItem, Product product) {
        if (cartItem.getQuantity() == null || cartItem.getQuantity() <= 0) {
            throw new IllegalArgumentException("Cart item quantity must be greater than zero: id: " + cartItem.getId());
        }

        if (product == null) {
            throw new ResourceNotFoundException("Product not found for cart item: id: " + cartItem.getId());
        }

        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price is invalid for cart item: id: " + cartItem.getId());
        }

        if (product.getInventory() == null || product.getInventory() < cartItem.getQuantity()) {
            throw new IllegalArgumentException("Requested quantity exceeds available inventory for cart item: id: " + cartItem.getId());
        }
    }

    private void validateSnapshotAgainstInventory(PaymentLineItemSnapshot lineItem, Product product) {
        if (product.getInventory() == null || product.getInventory() < lineItem.getQuantity()) {
            throw new IllegalArgumentException("Product inventory changed before payment confirmation: product id: " + lineItem.getProductId());
        }
    }

    private String getFirstProductImage(Product product) {
        List<Image> images = product.getImages();
        if (images == null || images.isEmpty() || images.getFirst() == null) {
            return null;
        }
        return images.getFirst().getDownloadUrl();
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .checkoutSessionId(payment.getCheckoutSessionId())
                .checkoutUrl(payment.getCheckoutUrl())
                .paymentIntentId(externalValue(payment.getPaymentIntentId()))
                .clientSecret(externalValue(payment.getClientSecret()))
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .orderId(payment.getOrder() == null ? null : payment.getOrder().getId())
                .build();
    }

    private String persistedValueOrPlaceholder(String value, String key) {
        if (value != null && !value.isBlank()) {
            return value;
        }
        return CHECKOUT_PLACEHOLDER_PREFIX + key;
    }

    private String externalValue(String value) {
        if (value == null || value.startsWith(CHECKOUT_PLACEHOLDER_PREFIX)) {
            return null;
        }
        return value;
    }

    private User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
