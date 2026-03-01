package dev.abhishek.ecommerce.modules.product.dto;

import dev.abhishek.ecommerce.modules.Image.dtos.ImageDto;
import dev.abhishek.ecommerce.modules.Image.entity.Image;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class ProductDto {
    private Long id;

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

    private List<ImageDto> imageList;
}
