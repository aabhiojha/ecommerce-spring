package dev.abhishek.ecommerce.modules.cart.service;

import dev.abhishek.ecommerce.modules.cart.dto.cart.CartDto;
import dev.abhishek.ecommerce.modules.cart.dto.cart.CartSummaryDto;
import dev.abhishek.ecommerce.modules.cart.dto.cart.CartValidationDto;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.AddCartItemRequest;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.CartItemDto;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.UpdateCartItemRequest;
import org.hibernate.query.Page;

public interface CartService {

    Page getAllCarts();

    CartDto getCart();

    CartSummaryDto getCartSummary();

    CartItemDto addCartItem(AddCartItemRequest addCartItemRequest);

    void updateCartItem(Long cartItemId, UpdateCartItemRequest updateCartItemRequest);

    void deleteCartItem(Long cartItemId);

    void clearCart();

    CartValidationDto validateCart();

}
