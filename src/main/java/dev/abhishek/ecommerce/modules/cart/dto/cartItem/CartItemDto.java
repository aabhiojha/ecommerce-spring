package dev.abhishek.ecommerce.modules.cart.dto.cartItem;

import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CartItemDto {

    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Long quantity;
    private BigDecimal subtotal;
}
