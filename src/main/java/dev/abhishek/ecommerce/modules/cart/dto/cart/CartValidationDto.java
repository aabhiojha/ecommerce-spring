package dev.abhishek.ecommerce.modules.cart.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartValidationDto {
    private boolean valid;
    private List<CartValidationItemDto> issues;
}
