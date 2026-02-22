package dev.abhishek.ecommerce.modules.product.service;

import dev.abhishek.ecommerce.dtos.product.AddProductRequest;
import dev.abhishek.ecommerce.common.exceptions.ProductNotFoundException;
import dev.abhishek.ecommerce.modules.category.entity.Category;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.category.repository.CategoryRepository;
import dev.abhishek.ecommerce.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Product addProduct(AddProductRequest product) {
        Category category = product.getCategory();
        Optional<Category> byId = categoryRepository.findById(product.getCategory().getId());
        if (byId.isEmpty()) {
            categoryRepository.save(category);
        }
        Product product1 = createProduct(product, category);
        return productRepository.save(product1);
    }

    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category
        );
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    @Override
    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    @Override
    public void updateProduct(Product product, Long productId) {
//        Product fromDb = productRepository.findById(productId).orElse(null);
//        productRepository.save();
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findAllByCategory_Id(categoryId);
    }

    @Override
    public List<Product> getProductByBrand(String brand) {
        return productRepository.findAllByBrandIgnoreCase(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findAllByCategory_NameIgnoreCaseAndBrandIgnoreCase(category, brand);
    }

    @Override
    public List<Product> getProductByName(String name) {
        return productRepository.findByNameIgnoreCase(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findAllByBrandContainingIgnoreCaseAndNameContainingIgnoreCase(brand, name);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandIgnoreCaseAndNameIgnoreCase(brand, name);
    }
}
