package dev.abhishek.ecommerce.modules.product.mapper;

import dev.abhishek.ecommerce.modules.category.entity.Category;
import dev.abhishek.ecommerce.modules.product.dto.CreateProductRequest;
import dev.abhishek.ecommerce.modules.product.dto.ProductDto;
import dev.abhishek.ecommerce.modules.product.dto.UpdateProductRequest;
import dev.abhishek.ecommerce.modules.product.entity.Product;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mapper utilities for Product <-> DTO conversions.
 * All methods are null-safe and treat missing category as null when converting to DTO.
 */
public final class ProductMapper {

    private ProductMapper() {
        // utility
    }

    public static ProductDto toDto(Product product) {
        if (product == null) return null;

        Integer categoryId = null;
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            categoryId = product.getCategory().getId().intValue();
        }

        Integer inventory = null;
        try {
            inventory = product.getInventory();
        } catch (Exception ignored) {
        }

        return ProductDto.builder()
                .name(product.getName())
                .brand(product.getBrand())
                .price(product.getPrice())
                .inventory(inventory)
                .description(product.getDescription())
                .category_id(categoryId)
                .build();
    }

    public static List<ProductDto> toDtoList(List<Product> products) {
        if (products == null) return null;
        return products.stream()
                .filter(Objects::nonNull)
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Product toEntity(CreateProductRequest request, Category category) {
        if (request == null) return null;
        Product product = new Product();
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setInventory(request.getInventory() != null ? request.getInventory() : 0);
        product.setDescription(request.getDescription());
        product.setCategory(category);
        return product;
    }

    public static void updateEntityFromRequest(UpdateProductRequest request, Product product, Category category) {
        if (request == null || product == null) return;
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        if (request.getInventory() != null) {
            product.setInventory(request.getInventory());
        }
        product.setDescription(request.getDescription());
        if (category != null) {
            product.setCategory(category);
        }
    }
}
