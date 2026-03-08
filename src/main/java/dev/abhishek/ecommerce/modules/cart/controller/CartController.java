package dev.abhishek.ecommerce.modules.cart.controller;

import dev.abhishek.ecommerce.modules.cart.dto.cart.CartDto;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.AddCartItemRequest;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.CartItemDto;
import dev.abhishek.ecommerce.modules.cart.entity.CartItem;
import dev.abhishek.ecommerce.modules.cart.repository.CartRepository;
import dev.abhishek.ecommerce.modules.cart.service.CartServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartServiceImpl cartService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/cart")
    public ResponseEntity<CartDto> getUserCart(){
        CartDto userCart = cartService.getUserCart();
        return ResponseEntity.ok(userCart);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/cart")
    public ResponseEntity<CartItemDto> addCartItem(@RequestBody AddCartItemRequest addCartItemRequest){
        CartItemDto cartItemDto = cartService.addCartItem(addCartItemRequest);
        return new ResponseEntity<>(cartItemDto, HttpStatus.OK);
    }
}
