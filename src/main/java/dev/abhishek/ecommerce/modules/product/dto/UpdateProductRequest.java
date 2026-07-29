package dev.abhishek.ecommerce.modules.product.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
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
public class UpdateProductRequest {

    private String name;

    private String brand;

    @Positive
    @DecimalMax("99999999")
    private BigDecimal price;

    @PositiveOrZero
    private Long inventory;

    private String description;

    private Long category_id;
}
