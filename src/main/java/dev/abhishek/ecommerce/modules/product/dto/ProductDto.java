package dev.abhishek.ecommerce.modules.product.dto;

import dev.abhishek.ecommerce.modules.Image.entity.Image;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductDto {

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

//    private List<Image> imageList;
}
