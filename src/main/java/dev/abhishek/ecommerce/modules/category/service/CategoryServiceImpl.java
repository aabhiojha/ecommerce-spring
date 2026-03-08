package dev.abhishek.ecommerce.modules.category.service;

import dev.abhishek.ecommerce.common.exceptions.CategoryNotFoundException;
import dev.abhishek.ecommerce.modules.category.dtos.CategoryDto;
import dev.abhishek.ecommerce.modules.category.dtos.CreateCategoryRequest;
import dev.abhishek.ecommerce.modules.category.dtos.UpdateCategoryRequest;
import dev.abhishek.ecommerce.modules.category.entity.Category;
import dev.abhishek.ecommerce.modules.category.mapper.CategoryMapper;
import dev.abhishek.ecommerce.modules.category.repository.CategoryRepository;
import dev.abhishek.ecommerce.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    @Override
    public void createCategory(CreateCategoryRequest createCategoryRequest) {
        log.info("Creating category with name={}", createCategoryRequest.getName());
        Category entity = categoryMapper.toEntity(createCategoryRequest);
        Category savedCategory = categoryRepository.save(entity);
        log.info("Category created successfully with id={}", savedCategory.getId());
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        log.debug("Fetching all categories");
        List<Category> all = categoryRepository.findAll();
        log.info("Fetched {} categories", all.size());
        return categoryMapper.toDtoList(all);
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        log.debug("Fetching category by id={}", id);
        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Category not found for id={}", id);
            return new CategoryNotFoundException("The category not found with id: " + id);
        });
        log.info("Category found for id={}, name={}", id, category.getName());
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto getCategoryByName(String name) {
        log.warn("getCategoryByName is not implemented yet. requestedName={}", name);
        return null;
    }

    @Override
    @Transactional
    public void updateCategoryById(Long id, UpdateCategoryRequest updateCategoryRequest) {
        log.info("Updating category id={} with new name={}", id, updateCategoryRequest.getName());
        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Cannot update category. Category not found for id={}", id);
            return new CategoryNotFoundException("Category not found, id: " + id);
        });
        category.setName(updateCategoryRequest.getName());
        log.info("Category updated successfully for id={}", id);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category id={}", id);
        categoryRepository.deleteById(id);
        log.info("Category deleted for id={}", id);
    }
}
