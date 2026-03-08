package dev.abhishek.ecommerce.modules.cart.dto.cartItem;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateCartItemRequest {
    private Long quantity;
}
