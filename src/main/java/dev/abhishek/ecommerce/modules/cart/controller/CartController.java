package dev.abhishek.ecommerce.modules.cart.controller;

import dev.abhishek.ecommerce.modules.cart.dto.cart.CartDto;
import dev.abhishek.ecommerce.modules.cart.dto.cart.CartSummaryDto;
import dev.abhishek.ecommerce.modules.cart.dto.cart.CartValidationDto;
import dev.abhishek.ecommerce.modules.cart.dto.cart.CalculateCartRequest;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.AddCartItemRequest;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.CartItemDto;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.UpdateCartItemRequest;
import dev.abhishek.ecommerce.modules.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cart", description = "Endpoints for managing the user's shopping cart")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Get user cart", description = "Retrieves the current user's shopping cart")
    @GetMapping
    public ResponseEntity<CartDto> getUserCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @Operation(summary = "Get cart summary", description = "Retrieves a summary of the current user's shopping cart")
    @GetMapping("/summary")
    public ResponseEntity<CartSummaryDto> getUserCartSummary() {
        return ResponseEntity.ok(cartService.getCartSummary());
    }

    @Operation(summary = "Add item to cart", description = "Adds a product to the user's shopping cart")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<CartItemDto> addCartItem(@Valid @RequestBody AddCartItemRequest addCartItemRequest) {
        return new ResponseEntity<>(cartService.addCartItem(addCartItemRequest), HttpStatus.CREATED);
    }

    @Operation(summary = "Update cart item", description = "Updates the quantity of an item in the shopping cart")
    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItemDto> updateCartItem(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest updateCartItemRequest
    ) {
        return ResponseEntity.ok(cartService.updateCartItem(cartItemId, updateCartItemRequest));
    }

    @Operation(summary = "Delete cart item", description = "Removes an item from the shopping cart")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId) {
        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Clear cart", description = "Removes all items from the shopping cart")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Validate cart", description = "Validates the items in the shopping cart before checkout")
    @PostMapping("/validate")
    public ResponseEntity<CartValidationDto> validateCart() {
        return ResponseEntity.ok(cartService.validateCart());
    }

    @Operation(summary = "Calculate selected items", description = "Calculates total quantity and price for selected cart items")
    @PostMapping("/calculate")
    public ResponseEntity<CartSummaryDto> calculateSelectedCartItems(@Valid @RequestBody CalculateCartRequest request) {
        return ResponseEntity.ok(cartService.calculateSelectedCartItems(request));
    }
}
