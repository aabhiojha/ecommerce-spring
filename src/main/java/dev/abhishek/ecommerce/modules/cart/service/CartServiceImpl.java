package dev.abhishek.ecommerce.modules.cart.service;

import dev.abhishek.ecommerce.common.exceptions.ProductNotFoundException;
import dev.abhishek.ecommerce.common.exceptions.ResourceNotFoundException;
import dev.abhishek.ecommerce.modules.cart.dto.cart.CartDto;
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

import java.util.List;
import java.util.Optional;

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
    @Transactional
    public CartItemDto addCartItem(AddCartItemRequest addCartItemRequest) {
        User user = getUser();
        Cart cart = getUserCart(user);

        Product product = productRepository.findById(addCartItemRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + addCartItemRequest.getProductId()));
        log.info("Retrieved product id={} name={}", product.getId(), product.getName());

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(addCartItemRequest.getQuantity())
                .build();

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        log.debug("Cart item is added successfully {}", cartItem.getProduct().getName());
        return cartMapper.toItemDto(savedCartItem);
    }

    @Override
    @Transactional
    public void updateCartItem(Long cartItemId, UpdateCartItemRequest updateCartItemRequest) {
//        get cart item
        User user = getUser();
        CartItem cartItem = getCartItem(user, cartItemId);
        // set the quantity from request to cartItem object
        cartItem.setQuantity(updateCartItemRequest.getQuantity());
        log.info("The quantity for cartItem: {} is updated", cartItem.getProduct().getName());
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
        log.info("CartItem {} fetched for user: {}",cartItem.getProduct().getName(), user.getUsername());
        return cartItem;
    }

    private Cart createCart(User user) {
        // create a cart with the associated user
        Cart cart = new Cart();
        cart.setUser(user);
        log.info("Cart created for user: {}", user.getUsername());
        return cartRepository.save(cart);
    }
}
