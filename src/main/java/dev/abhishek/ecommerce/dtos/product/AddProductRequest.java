package dev.abhishek.ecommerce.dtos.product;

import dev.abhishek.ecommerce.modules.category.entity.Category;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddProductRequest {
    private String name;

    private String brand;

    private BigDecimal price;

    private int inventory;

    private String description;

    private Category category;

}
