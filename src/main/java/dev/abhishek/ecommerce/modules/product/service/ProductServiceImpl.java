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
import dev.abhishek.ecommerce.common.dto.PagedResponse;
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

        // resolving category
        Category category = categoryRepository.findById(createProductRequest.getCategory_id())
                .orElseThrow(
                        () -> {
                            log.warn("Category not found with id: {}", createProductRequest.getCategory_id());
                            return new CategoryNotFoundException("Category not found");
                        });
        log.debug("Category resolved: {}", category.getName());

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Product productEntity = productMapper.toEntity(createProductRequest, category);
        productEntity.setSeller(user);

        // create product entry
        Product saved = productRepository.save(productEntity);
        log.debug("Product created successfully with id: {}", saved.getId());
        return productMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductDto> getAllProducts(Pageable pageable, Long id, String name, String description) {
        Specification<Product> specification = ProductSpecification.getSpecification(id, name, description);
        Page<Product> page = productRepository.findAll(specification, pageable);
        return new PagedResponse<>(productMapper.toDtoList(page.getContent()), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        log.info("Retrieved product id={} name={}", product.getId(), product.getName());
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public void deleteProductById(Long id) {
        // A seller may only delete their own products.
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Product product = productRepository.findByIdAndSeller(id, user)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
        log.info("Product with id {} deleted", id);
    }

    @Override
    @Transactional
    public ProductDto updateProductById(UpdateProductRequest updateRequest, Long productId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Product product = productRepository.findByIdAndSeller(productId, user)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        Category category = null;
        if (updateRequest.getCategory_id() != null) {
            category = categoryRepository.findById(updateRequest.getCategory_id())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + updateRequest.getCategory_id()));
        }
        // this will update the productEntity to reflect the changes in the request
        productMapper.updateEntityFromRequest(updateRequest, product, category);
        log.info("Product mapped from request entity");
        Product savedProduct = productRepository.save(product);
        log.info("Product with id {} updated successfully", productId);
        return productMapper.toDto(savedProduct);
    }


    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductDto> getProductsByCategory(Long categoryId, Pageable pageable) {
        Page<Product> page = productRepository.findAllByCategory_Id(categoryId, pageable);
        return new PagedResponse<>(productMapper.toDtoList(page.getContent()), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductDto> getProductsByBrand(String brand, Pageable pageable) {
        Page<Product> page = productRepository.findAllByBrandIgnoreCase(brand, pageable);
        return new PagedResponse<>(productMapper.toDtoList(page.getContent()), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductDto> getProductsByCategoryAndBrand(String category, String brand, Pageable pageable) {
        Page<Product> page = productRepository.findAllByCategory_NameIgnoreCaseAndBrandIgnoreCase(category, brand, pageable);
        return new PagedResponse<>(productMapper.toDtoList(page.getContent()), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductDto> getProductByName(String name, Pageable pageable) {
        Page<Product> page = productRepository.findByNameIgnoreCase(name, pageable);
        return new PagedResponse<>(productMapper.toDtoList(page.getContent()), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductDto> getProductsByBrandAndName(String brand, String name, Pageable pageable) {
        Page<Product> page = productRepository.findAllByBrandContainingIgnoreCaseAndNameContainingIgnoreCase(brand, name, pageable);
        return new PagedResponse<>(productMapper.toDtoList(page.getContent()), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandIgnoreCaseAndNameIgnoreCase(brand, name);
    }
}
