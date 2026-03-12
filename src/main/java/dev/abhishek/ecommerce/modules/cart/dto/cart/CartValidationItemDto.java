package dev.abhishek.ecommerce.modules.cart.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartValidationItemDto {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private Long requestedQuantity;
    private Long availableInventory;
    private String message;
}
