package dev.abhishek.ecommerce.modules.product.service;

import dev.abhishek.ecommerce.modules.product.dto.CreateProductRequest;
import dev.abhishek.ecommerce.modules.product.dto.ProductDto;
import dev.abhishek.ecommerce.modules.product.dto.UpdateProductRequest;
import org.springframework.data.domain.Pageable;
import dev.abhishek.ecommerce.common.dto.PagedResponse;

import java.util.List;

public interface ProductService {
    ProductDto addProduct(CreateProductRequest product);

    PagedResponse<ProductDto> getAllProducts(Pageable pageable, Long id, String name, String search);

    ProductDto getProductById(Long id);

    void deleteProductById(Long id);

    ProductDto updateProductById(UpdateProductRequest productRequest, Long productId);

    PagedResponse<ProductDto> getProductsByCategory(Long categoryId, Pageable pageable);

    PagedResponse<ProductDto> getProductsByBrand(String brand, Pageable pageable);

    PagedResponse<ProductDto> getProductsByCategoryAndBrand(String category, String brand, Pageable pageable);

    PagedResponse<ProductDto> getProductByName(String name, Pageable pageable);

    PagedResponse<ProductDto> getProductsByBrandAndName(String brand, String name, Pageable pageable);

    Long countProductsByBrandAndName(String brand, String name);
}
