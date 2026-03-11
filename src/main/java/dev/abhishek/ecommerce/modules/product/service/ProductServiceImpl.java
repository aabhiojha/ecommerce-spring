package dev.abhishek.ecommerce.modules.product.service;

import dev.abhishek.ecommerce.common.exceptions.CategoryNotFoundException;
import dev.abhishek.ecommerce.common.exceptions.ProductNotFoundException;
import dev.abhishek.ecommerce.modules.category.entity.Category;
import dev.abhishek.ecommerce.modules.product.dto.CreateProductRequest;
import dev.abhishek.ecommerce.modules.product.dto.ProductDto;
import dev.abhishek.ecommerce.modules.product.dto.UpdateProductRequest;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.category.repository.CategoryRepository;
import dev.abhishek.ecommerce.modules.product.mapper.ProductMapper;
import dev.abhishek.ecommerce.modules.product.repository.ProductRepository;
import dev.abhishek.ecommerce.modules.product.specification.ProductSpecification;
import dev.abhishek.ecommerce.modules.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductDto addProduct(CreateProductRequest createProductRequest) {
        log.info("The provided createProductRequest object: {}", createProductRequest);

        // resolving category
        int categoryId = createProductRequest.getCategory_id();
        Category category = categoryRepository.findById((long) categoryId)
                .orElseThrow(
                        () -> {
                            log.warn("Category not found with id: {}", createProductRequest.getCategory_id());
                            return new CategoryNotFoundException("Category not found");
                        });
        log.debug("Category resolved: {}", category.getName());

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Product productEntity = productMapper.toEntity(createProductRequest, category);
        productEntity.setSeller(user);
        log.debug("The product entity is: {}", productEntity);

        // create product entry
        Product saved = productRepository.save(productEntity);
        log.debug("Product created successfully with id: {}", saved.getId());
        return productMapper.toDto(saved);
    }

    @Override
    public List<ProductDto> getAllProducts(Pageable pageable, Long id, String name, String description) {
        Specification<Product> specification = ProductSpecification.getSpecification(id, name, description);
        return productMapper.toDtoList(productRepository.findAll(specification, pageable).getContent());

    }

    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        log.info("Retrieved product id={} name={}", product.getId(), product.getName());
        return productMapper.toDto(product);
    }

    @Override
    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public void updateProductById(UpdateProductRequest productRequest, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        Category category = categoryRepository.findById(productRequest.getCategory_id())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + productRequest.getCategory_id()));

        // this will update the productEntity to reflect the changes in the request
        productMapper.updateEntityFromRequest(productRequest, product, category);
        // redundant save as @Transactional flushes changes at
        // transaction commit for a managed entity.
        productRepository.save(product);
        log.info("Product with id {} updated successfully", productId);

    }


    @Override
    public List<ProductDto> getProductsByCategory(Long categoryId) {
        List<Product> allByCategoryId = productRepository.findAllByCategory_Id(categoryId);
        return productMapper.toDtoList(allByCategoryId);
    }

    @Override
    public List<ProductDto> getProductsByBrand(String brand) {
        List<Product> allByBrandIgnoreCase = productRepository.findAllByBrandIgnoreCase(brand);
        return productMapper.toDtoList(allByBrandIgnoreCase);
    }

    @Override
    public List<ProductDto> getProductsByCategoryAndBrand(String category, String brand) {
        List<Product> products = productRepository.findAllByCategory_NameIgnoreCaseAndBrandIgnoreCase(category, brand);
        return productMapper.toDtoList(products);
    }

    @Override
    public List<ProductDto> getProductByName(String name) {
        List<Product> products = productRepository.findByNameIgnoreCase(name);
        return productMapper.toDtoList(products);
    }

    @Override
    public List<ProductDto> getProductsByBrandAndName(String brand, String name) {
        List<Product> products = productRepository.findAllByBrandContainingIgnoreCaseAndNameContainingIgnoreCase(brand, name);
        return productMapper.toDtoList(products);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandIgnoreCaseAndNameIgnoreCase(brand, name);
    }
}
