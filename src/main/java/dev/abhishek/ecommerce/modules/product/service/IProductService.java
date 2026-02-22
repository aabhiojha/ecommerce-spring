package dev.abhishek.ecommerce.modules.product.service;

import dev.abhishek.ecommerce.dtos.product.AddProductRequest;
import dev.abhishek.ecommerce.modules.product.entity.Product;

import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest product);

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
