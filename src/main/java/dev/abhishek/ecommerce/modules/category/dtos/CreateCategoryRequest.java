package dev.abhishek.ecommerce.modules.category.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateCategoryRequest {

    @NotNull
    private String name;

}
