package dev.abhishek.ecommerce.modules.product.service;

import dev.abhishek.ecommerce.common.exceptions.ProductNotFoundException;
import dev.abhishek.ecommerce.modules.category.entity.Category;
import dev.abhishek.ecommerce.modules.product.dto.CreateProductRequest;
import dev.abhishek.ecommerce.modules.product.dto.ProductDto;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.category.repository.CategoryRepository;
import dev.abhishek.ecommerce.modules.product.mapper.ProductMapper;
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
    public ProductDto addProduct(CreateProductRequest product) {
        System.out.println("The create request object :"+ product);
        int categoryId = product.getCategory_id();
        Optional<Category> byId = categoryRepository.findById((long) categoryId);
        System.out.println("The category object: " + byId.get());
        Product entity = ProductMapper.toEntity(product, byId.get());
        // create product entry
        Product save = productRepository.save(entity);
        System.out.println("The product object: "+ save);
        return ProductMapper.toDto(save);
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
