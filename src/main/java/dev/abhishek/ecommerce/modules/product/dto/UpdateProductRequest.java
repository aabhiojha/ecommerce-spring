package dev.abhishek.ecommerce.modules.product.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UpdateProductRequest {

    @NotNull
    private String name;

    private String brand;

    @NotNull
    @Positive
    @DecimalMax("99999999")
    private BigDecimal price;

    @NotNull
    private Integer inventory;

    @NotNull
    private String description;

    private Integer category_id;
}
