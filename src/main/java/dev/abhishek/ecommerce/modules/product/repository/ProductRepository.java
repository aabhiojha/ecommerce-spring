package dev.abhishek.ecommerce.modules.product.repository;

import dev.abhishek.ecommerce.modules.category.entity.Category;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByCategory(Category category);

    List<Product> findAllByCategory(Category category);

    List<Product> findAllByCategory_Id(Long categoryId);

    List<Product> findAllByBrandIgnoreCase(String brand);

    List<Product> findAllByCategory_NameIgnoreCaseAndBrandIgnoreCase(String categoryName, String brand);

    List<Product> findByNameIgnoreCase(String name);

    List<Product> findAllByBrandContainingIgnoreCaseAndNameContainingIgnoreCase(String brand, String name);

    Long countByBrandIgnoreCaseAndNameIgnoreCase(String brand, String name);
}
