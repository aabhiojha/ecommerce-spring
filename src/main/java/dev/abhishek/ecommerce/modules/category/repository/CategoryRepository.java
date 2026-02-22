package dev.abhishek.ecommerce.modules.category.repository;

import dev.abhishek.ecommerce.modules.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
