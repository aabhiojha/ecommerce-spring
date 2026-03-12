package dev.abhishek.ecommerce.modules.product.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class UpdateProductRequest {

    private String name;

    private String brand;

    @Positive
    @DecimalMax("99999999")
    private BigDecimal price;

    private Integer inventory;

    private String description;

    private Long category_id;
}
