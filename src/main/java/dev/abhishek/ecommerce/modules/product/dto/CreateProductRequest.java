package dev.abhishek.ecommerce.modules.product.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateProductRequest {

    @NotBlank
    private String name;

    private String brand;

    @NotNull
    @Positive
    @DecimalMax("99999999")
    private BigDecimal price;

    @NotNull
    @PositiveOrZero
    private Long inventory;

    @NotBlank
    private String description;

    @NotNull
    private Long category_id;
}
