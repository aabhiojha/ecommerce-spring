package dev.abhishek.ecommerce.modules.cart.service;

import dev.abhishek.ecommerce.modules.cart.dto.cart.CartDto;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.AddCartItemRequest;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.CartItemDto;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.UpdateCartItemRequest;
import dev.abhishek.ecommerce.modules.cart.entity.Cart;
import dev.abhishek.ecommerce.modules.cart.repository.CartItemRepository;
import dev.abhishek.ecommerce.modules.cart.repository.CartRepository;
import dev.abhishek.ecommerce.modules.product.repository.ProductRepository;
import dev.abhishek.ecommerce.modules.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Override
    public List<CartDto> getAllCarts() {
        return List.of();
    }

    @Override
    public Optional<CartDto> getUserCart() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // get the cart object of the user
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createCart(user));


        return null;

    }

    private Cart createCart(User user) {
        // create a cart with the associated user
        Cart cart = new Cart();
        cart.setUser(user);

        return cartRepository.save(cart);
    }

    @Override
    public CartItemDto addCartItem(AddCartItemRequest addCartItemRequest) {
        return null;
    }

    @Override
    public void updateCartItem(UpdateCartItemRequest updateCartItemRequest) {

    }

    @Override
    public void deleteCartItem(Long cartItemId) {

    }
}
