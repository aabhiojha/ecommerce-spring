package dev.abhishek.ecommerce.modules.cart.dto.cart;

import dev.abhishek.ecommerce.modules.cart.dto.cartItem.CartItemDto;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private Long id;
    private List<CartItemDto> items;
    private Long totalItems;
    private BigDecimal totalPrice;
}
