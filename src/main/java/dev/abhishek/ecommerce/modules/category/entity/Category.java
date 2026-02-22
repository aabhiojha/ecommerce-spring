package dev.abhishek.ecommerce.modules.category.entity;

import dev.abhishek.ecommerce.modules.product.entity.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Category {

    @Id
    private Long id;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Product> products;
}
