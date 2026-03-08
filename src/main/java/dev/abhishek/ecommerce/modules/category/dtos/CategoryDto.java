package dev.abhishek.ecommerce.modules.category.dtos;

import dev.abhishek.ecommerce.modules.product.dto.ProductDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CategoryDto {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    private List<ProductDto> products;
}
