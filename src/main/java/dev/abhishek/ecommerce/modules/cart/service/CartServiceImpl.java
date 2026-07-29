package dev.abhishek.ecommerce.modules.cart.service;

import dev.abhishek.ecommerce.common.exceptions.InsufficientProductInventoryException;
import dev.abhishek.ecommerce.common.exceptions.ProductNotFoundException;
import dev.abhishek.ecommerce.common.exceptions.ResourceNotFoundException;
import dev.abhishek.ecommerce.modules.cart.dto.cart.CartDto;
import dev.abhishek.ecommerce.modules.cart.dto.cart.CartSummaryDto;
import dev.abhishek.ecommerce.modules.cart.dto.cart.CartValidationDto;
import dev.abhishek.ecommerce.modules.cart.dto.cart.CartValidationItemDto;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.AddCartItemRequest;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.CartItemDto;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.UpdateCartItemRequest;
import dev.abhishek.ecommerce.modules.cart.entity.Cart;
import dev.abhishek.ecommerce.modules.cart.entity.CartItem;
import dev.abhishek.ecommerce.modules.cart.mapper.CartMapper;
import dev.abhishek.ecommerce.modules.cart.repository.CartItemRepository;
import dev.abhishek.ecommerce.modules.cart.repository.CartRepository;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.product.repository.ProductRepository;
import dev.abhishek.ecommerce.modules.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartDto getCart() {
        User user = getUser();
        Cart cart = getUserCart(user);

        // cart converted to cartDto
        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartSummaryDto getCartSummary() {
        User user = getUser();
        Cart cart = getUserCart(user);
        CartDto cartDto = cartMapper.toDto(cart);

        return CartSummaryDto.builder()
                .cartId(cart.getId())
                .totalItems(cartDto.getTotalItems())
                .totalPrice(cartDto.getTotalPrice())
                .build();
    }

    @Override
    @Transactional
    public CartItemDto addCartItem(AddCartItemRequest addCartItemRequest) {
        User user = getUser();
        Cart cart = getUserCart(user);

        long requestedQuantity = requirePositiveQuantity(addCartItemRequest.getQuantity());

        Product product = productRepository.findById(addCartItemRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + addCartItemRequest.getProductId()));
        log.info("Retrieved product id={} name={}", product.getId(), product.getName());

        // build a new item if not found, otherwise top up the existing one
        CartItem cartItem = cartItemRepository.findByCartAndProduct_Id(cart, product.getId())
                .orElseGet(() -> CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(0L)
                        .build());

        long totalQuantity = cartItem.getQuantity() + requestedQuantity;
        requireInventory(product, totalQuantity);

        cartItem.setQuantity(totalQuantity);

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        // Keep the in-memory cart consistent so a later clearCart() in the same transaction sees it.
        if (!cart.getCartItems().contains(savedCartItem)) {
            cart.getCartItems().add(savedCartItem);
        }

        log.debug("Cart item saved for product id={} with quantity={}", product.getId(), totalQuantity);
        return cartMapper.toItemDto(savedCartItem);
    }

    @Override
    @Transactional
    public CartItemDto updateCartItem(Long cartItemId, UpdateCartItemRequest updateCartItemRequest) {
        User user = getUser();
        CartItem cartItem = getCartItem(user, cartItemId);

        long quantity = requirePositiveQuantity(updateCartItemRequest.getQuantity());
        // Previously an over-stock update was silently ignored and still answered 200.
        requireInventory(cartItem.getProduct(), quantity);

        cartItem.setQuantity(quantity);
        log.info("The quantity for cartItem {} is updated to {}", cartItem.getId(), quantity);
        return cartMapper.toItemDto(cartItem);
    }

    @Override
    @Transactional
    public void deleteCartItem(Long cartItemId) {
        // must let user delete their own cartItem only
        User user = getUser();
        CartItem cartItem = getCartItem(user, cartItemId);
        cartItem.getCart().getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        log.info("The cartItem with id {} is deleted", cartItem.getId());
    }

    @Override
    @Transactional
    public void clearCart() {
        User user = getUser();
        Cart cart = getUserCart(user);

        // Delete through the repository: the in-memory collection alone is not a reliable view.
        List<CartItem> items = cartItemRepository.findAllByCart_User(user);
        cartItemRepository.deleteAll(items);
        cart.getCartItems().clear();
        log.info("Cart cleared for user {}", user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public CartValidationDto validateCart() {
        User user = getUser();
        Cart cart = getUserCart(user);

        List<CartValidationItemDto> issues = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            if (hasInventoryIssue(cartItem) || hasPriceIssue(cartItem)) {
                issues.add(toValidationIssue(cartItem));
            }
        }

        log.info("Cart validation completed for user {} with {} issue(s)", user.getId(), issues.size());
        return CartValidationDto.builder()
                .valid(issues.isEmpty())
                .issues(issues)
                .build();
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

    private Cart getUserCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> createCart(user));
    }

    private CartItem getCartItem(User user, Long cartItemId) {
        return cartItemRepository.findByIdAndCart_User(cartItemId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart Item not found in your cart: id: " + cartItemId));
    }

    private Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        log.info("Cart created for user: {}", user.getUsername());
        return cartRepository.save(cart);
    }

    private long requirePositiveQuantity(Long quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        return quantity;
    }

    private void requireInventory(Product product, long requestedQuantity) {
        Long inventory = product.getInventory();
        if (inventory == null || inventory < requestedQuantity) {
            throw new InsufficientProductInventoryException(
                    "Requested quantity exceeds available stock for product: " + product.getName());
        }
    }

    private boolean hasInventoryIssue(CartItem cartItem) {
        Long inventory = cartItem.getProduct().getInventory();
        return inventory == null || inventory < cartItem.getQuantity();
    }

    private boolean hasPriceIssue(CartItem cartItem) {
        BigDecimal price = cartItem.getProduct().getPrice();
        return price == null || price.compareTo(BigDecimal.ZERO) <= 0;
    }

    private CartValidationItemDto toValidationIssue(CartItem cartItem) {
        String message = hasInventoryIssue(cartItem)
                ? "Requested quantity exceeds available inventory."
                : "Product price is invalid.";

        return CartValidationItemDto.builder()
                .cartItemId(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .requestedQuantity(cartItem.getQuantity())
                .availableInventory(cartItem.getProduct().getInventory())
                .message(message)
                .build();
    }
}
