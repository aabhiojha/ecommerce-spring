package dev.abhishek.ecommerce.modules.category.service;

import dev.abhishek.ecommerce.common.exceptions.CategoryNotFoundException;
import dev.abhishek.ecommerce.modules.category.dtos.CategoryDto;
import dev.abhishek.ecommerce.modules.category.dtos.CreateCategoryRequest;
import dev.abhishek.ecommerce.modules.category.entity.Category;
import dev.abhishek.ecommerce.modules.category.mapper.CategoryMapper;
import dev.abhishek.ecommerce.modules.category.repository.CategoryRepository;
import dev.abhishek.ecommerce.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void createCategory(CreateCategoryRequest createCategoryRequest) {
        Category entity = CategoryMapper.toEntity(createCategoryRequest);
        categoryRepository.save(entity);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> all = categoryRepository.findAll();
        return CategoryMapper.toDtoList(all);
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("The category not found with id: " + id));
        return CategoryMapper.toDto(category);
    }

    @Override
    public CategoryDto getCategoryByName(String name) {
        return null;
    }

    @Override
    public void updateCategoryById(Long id) {

    }

    public void deleteCategory(Long id) {
        productRepository.deleteById(id);
    }
}
