package dev.abhishek.ecommerce.modules.category.service;

import dev.abhishek.ecommerce.modules.category.dtos.CategoryDto;
import dev.abhishek.ecommerce.modules.category.dtos.CreateCategoryRequest;
import dev.abhishek.ecommerce.modules.product.dto.CreateProductRequest;

import java.util.List;

public interface ICategoryService {

    void createCategory(CreateCategoryRequest createCategoryRequest);

    List<CategoryDto> getAllCategories();

    CategoryDto getCategoryById(Long id);

    CategoryDto getCategoryByName(String name);

    void updateCategoryById(Long id);


}
