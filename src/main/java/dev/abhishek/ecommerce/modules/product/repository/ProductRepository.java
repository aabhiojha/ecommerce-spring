package dev.abhishek.ecommerce.modules.product.repository;

import dev.abhishek.ecommerce.modules.category.entity.Category;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByCategory(Category category);

    List<Product> findAllByCategory(Category category);

    List<Product> findAllByCategory_Id(Long categoryId);

    List<Product> findAllByBrandIgnoreCase(String brand);

    List<Product> findAllByCategory_NameIgnoreCaseAndBrandIgnoreCase(String categoryName, String brand);

    List<Product> findByNameIgnoreCase(String name);

    List<Product> findAllByBrandContainingIgnoreCaseAndNameContainingIgnoreCase(String brand, String name);

    Long countByBrandIgnoreCaseAndNameIgnoreCase(String brand, String name);

    Page<Product> findByNameIsContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByName(String search, Pageable pageable);

    Optional<Product> findByIdAndSeller(Long id, User seller);
}
