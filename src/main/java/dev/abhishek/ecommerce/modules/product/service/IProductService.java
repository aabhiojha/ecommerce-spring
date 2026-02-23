package dev.abhishek.ecommerce.modules.product.service;

import dev.abhishek.ecommerce.modules.product.dto.CreateProductRequest;
import dev.abhishek.ecommerce.modules.product.dto.ProductDto;
import dev.abhishek.ecommerce.modules.product.entity.Product;

import java.util.List;

public interface IProductService {
    ProductDto addProduct(CreateProductRequest product);

    List<Product> getAllProducts();

    Product getProductById(Long id);

    void deleteProductById(Long id);

    void updateProduct(Product product, Long productId);

    List<Product> getProductsByCategory(Long categoryId);

    List<Product> getProductByBrand(String brand);

    List<Product> getProductsByCategoryAndBrand(String category, String brand);

    List<Product> getProductByName(String name);

    List<Product> getProductsByBrandAndName(String brand, String name);

    Long countProductsByBrandAndName(String brand, String name);
}
