package dev.abhishek.ecommerce.modules.category.service;

import dev.abhishek.ecommerce.modules.category.dtos.CategoryDto;
import dev.abhishek.ecommerce.modules.category.dtos.CreateCategoryRequest;
import dev.abhishek.ecommerce.modules.category.dtos.UpdateCategoryRequest;

import java.util.List;

public interface CategoryService {

    void createCategory(CreateCategoryRequest createCategoryRequest);

    List<CategoryDto> getAllCategories();

    CategoryDto getCategoryById(Long id);

    CategoryDto getCategoryByName(String name);

    void updateCategoryById(Long id, UpdateCategoryRequest updateCategoryRequest);


}
