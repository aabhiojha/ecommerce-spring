package dev.abhishek.ecommerce.modules.cart.service;

import dev.abhishek.ecommerce.common.exceptions.ProductNotFoundException;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapperImpl cartMapper;

    @Override
    public List<CartDto> getAllCarts() {
        return List.of();
    }

    @Override
    public CartDto getUserCart() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // get the cart object of the user
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createCart(user));
        return cartMapper.toDto(cart);
    }

    private Cart createCart(User user) {
        // create a cart with the associated user
        Cart cart = new Cart();
        cart.setUser(user);

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartItemDto addCartItem(AddCartItemRequest addCartItemRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cart cart = cartRepository.findByUser(user).orElseGet(()->
                    createCart(user)
                );
        Product product = productRepository.findById(addCartItemRequest.getProductId()).orElseThrow(()-> new ProductNotFoundException("Product not found with id: "+ addCartItemRequest.getProductId()));

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(addCartItemRequest.getQuantity())
                .build();

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        return cartMapper.toItemDto(savedCartItem);
    }

    @Override
    public void updateCartItem(UpdateCartItemRequest updateCartItemRequest) {

    }

    @Override
    public void deleteCartItem(Long cartItemId) {

    }
}
