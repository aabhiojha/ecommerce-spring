package dev.abhishek.ecommerce.modules.category.mapper;

import dev.abhishek.ecommerce.modules.category.dtos.CategoryDto;
import dev.abhishek.ecommerce.modules.category.dtos.CreateCategoryRequest;
import dev.abhishek.ecommerce.modules.category.entity.Category;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.product.mapper.ProductMapper;

import java.util.List;

public class CategoryMapper {

    private CategoryMapper() {
        // prevent instantiation
    }

    public static Category toEntity(CreateCategoryRequest request) {
        if (request == null) {
            return null;
        }

        Category category = new Category();
        category.setName(request.getName());
        return category;
    }

    public static CategoryDto toDto(Category category) {
        if (category == null) {
            return null;
        }

        List<Product> products = category.getProducts();

        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .products(ProductMapper.toDtoList(products))
                .build();
    }

    public static List<CategoryDto> toDtoList(List<Category> categories) {
        if (categories == null) {
            return List.of();
        }

        return categories.stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    public static void updateEntity(Category category, CreateCategoryRequest request) {
        if (category == null || request == null) {
            return;
        }

        category.setName(request.getName());
    }
}