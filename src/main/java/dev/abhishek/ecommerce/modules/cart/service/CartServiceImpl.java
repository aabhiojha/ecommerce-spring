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
import dev.abhishek.ecommerce.modules.cart.mapper.CartMapperImpl;
import dev.abhishek.ecommerce.modules.cart.repository.CartItemRepository;
import dev.abhishek.ecommerce.modules.cart.repository.CartRepository;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.product.repository.ProductRepository;
import dev.abhishek.ecommerce.modules.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Page;
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
    private final CartMapperImpl cartMapper;

    @Override
    public Page getAllCarts() {
        List<Cart> allCarts = cartRepository.findAll();
        return null;
    }


    @Override
    public CartDto getCart() {
        User user = getUser();
        Cart cart = getUserCart(user);

        // cart converted to cartDto
        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional(readOnly = true)
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

        Product product = productRepository.findById(addCartItemRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + addCartItemRequest.getProductId()));
        log.info("Retrieved product id={} name={}", product.getId(), product.getName());

        // build a new if not found
        // but use the existing cartitem if found
        CartItem cartItem = cartItemRepository.findByCartAndProduct_Id(cart, product.getId())
                .orElseGet(() -> CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(0L)
                        .build());

        // update the cartitem quantity by request quantity
        long requestedQuantity = addCartItemRequest.getQuantity();
        long totalQuantity = cartItem.getQuantity() + requestedQuantity;

        if (product.getInventory() < totalQuantity) {
            throw new InsufficientProductInventoryException(
                    "Requested quantity exceeds available stock"
            );
        }

        cartItem.setQuantity(totalQuantity);

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        log.debug("Cart item saved for product id={} with quantity={}", product.getId(), totalQuantity);
        return cartMapper.toItemDto(savedCartItem);
    }

    @Override
    @Transactional
    public void updateCartItem(Long cartItemId, UpdateCartItemRequest updateCartItemRequest) {
//        get cart item
        User user = getUser();
        CartItem cartItem = getCartItem(user, cartItemId);
        // set the quantity from request to cartItem object
        // after checking for existing product units
        if (cartItem.getProduct().getInventory() > updateCartItemRequest.getQuantity()) {
            cartItem.setQuantity(updateCartItemRequest.getQuantity());
            log.info("The quantity for cartItem: {} is updated", cartItem.getProduct().getName());
        } else {
            log.info("The request quantity is greater stock");
        }
    }


    @Override
    @Transactional
    public void deleteCartItem(Long cartItemId) {
        // must let user delete their own cartItem only
        User user = getUser();
        CartItem cartItem = getCartItem(user, cartItemId);
        cartItemRepository.delete(cartItem);
        log.info("The cartItem with id {} is deleted", cartItem.getId());
    }

    @Override
    @Transactional
    public void clearCart() {
        User user = getUser();
        Cart cart = getUserCart(user);
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
                CartValidationItemDto validationIssue = toValidationIssue(cartItem);
                issues.add(validationIssue);
            }
        }

        log.info("Cart validation completed for user {} with {} issue(s)", user.getId(), issues.size());
        return CartValidationDto.builder()
                .valid(issues.isEmpty())
                .issues(issues)
                .build();
    }

    // helper functions
    // get user function
    private User getUser() {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        log.info("User : {} fetched", user.getUsername());
        return user;
    }

    // get cart of the user
    private Cart getUserCart(User user) {
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createCart(user));
        log.info("Cart fetched for user {}", user.getId());
        return cart;
    }

    private CartItem getCartItem(User user, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findByIdAndCart_User(cartItemId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart Item not found in your cart: id: " + cartItemId));
        log.info("CartItem {} fetched for user: {}", cartItem.getProduct().getName(), user.getUsername());
        return cartItem;
    }

    private Cart createCart(User user) {
        // create a cart with the associated user
        Cart cart = new Cart();
        cart.setUser(user);
        log.info("Cart created for user: {}", user.getUsername());
        return cartRepository.save(cart);
    }

    private boolean hasInventoryIssue(CartItem cartItem) {
        return cartItem.getProduct().getInventory() < cartItem.getQuantity();
    }

    private boolean hasPriceIssue(CartItem cartItem) {
        return cartItem.getProduct().getPrice() == null || cartItem.getProduct().getPrice().compareTo(BigDecimal.ZERO) < 0;
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
