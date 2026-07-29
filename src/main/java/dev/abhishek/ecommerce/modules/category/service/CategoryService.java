package dev.abhishek.ecommerce.modules.category.service;

import dev.abhishek.ecommerce.modules.category.dtos.CategoryDto;
import dev.abhishek.ecommerce.modules.category.dtos.CreateCategoryRequest;
import dev.abhishek.ecommerce.modules.category.dtos.UpdateCategoryRequest;

import dev.abhishek.ecommerce.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CreateCategoryRequest createCategoryRequest);

    PagedResponse<CategoryDto> getAllCategories(Pageable pageable);

    CategoryDto getCategoryById(Long id);

    CategoryDto getCategoryByName(String name);

    CategoryDto updateCategoryById(Long id, UpdateCategoryRequest updateCategoryRequest);

    void deleteCategory(Long id);
}
