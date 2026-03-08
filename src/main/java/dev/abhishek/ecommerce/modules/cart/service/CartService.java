package dev.abhishek.ecommerce.modules.cart.service;

import dev.abhishek.ecommerce.modules.cart.dto.cart.CartDto;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.AddCartItemRequest;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.CartItemDto;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.UpdateCartItemRequest;

import java.util.List;
import java.util.Optional;

public interface CartService {

    List<CartDto> getAllCarts();

    Optional<CartDto> getUserCart();

    CartItemDto addCartItem(AddCartItemRequest addCartItemRequest);

    void updateCartItem(UpdateCartItemRequest updateCartItemRequest);

    void deleteCartItem(Long cartItemId);

}
