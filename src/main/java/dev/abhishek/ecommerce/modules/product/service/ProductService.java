package dev.abhishek.ecommerce.modules.product.service;

import dev.abhishek.ecommerce.modules.product.dto.CreateProductRequest;
import dev.abhishek.ecommerce.modules.product.dto.ProductDto;
import dev.abhishek.ecommerce.modules.product.dto.UpdateProductRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductDto addProduct(CreateProductRequest product);

    List<ProductDto> getAllProducts(Pageable pageable, Long id, String name, String search);

    ProductDto getProductById(Long id);

    void deleteProductById(Long id);

    ProductDto updateProductById(UpdateProductRequest productRequest, Long productId);

    List<ProductDto> getProductsByCategory(Long categoryId);

    List<ProductDto> getProductsByBrand(String brand);

    List<ProductDto> getProductsByCategoryAndBrand(String category, String brand);

    List<ProductDto> getProductByName(String name);

    List<ProductDto> getProductsByBrandAndName(String brand, String name);

    Long countProductsByBrandAndName(String brand, String name);
}
