package dev.abhishek.ecommerce.modules.cart.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartSummaryDto {
    private Long cartId;
    private Long totalItems;
    private BigDecimal totalPrice;
}
