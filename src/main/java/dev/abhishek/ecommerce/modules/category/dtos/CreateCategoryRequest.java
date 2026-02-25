package dev.abhishek.ecommerce.modules.category.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCategoryRequest {

    @NotNull
    private String name;

}
