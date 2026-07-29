package dev.abhishek.ecommerce.modules.category.service;

import dev.abhishek.ecommerce.common.exceptions.CategoryNotFoundException;
import dev.abhishek.ecommerce.modules.category.dtos.CategoryDto;
import dev.abhishek.ecommerce.modules.category.dtos.CreateCategoryRequest;
import dev.abhishek.ecommerce.modules.category.dtos.UpdateCategoryRequest;
import dev.abhishek.ecommerce.modules.category.entity.Category;
import dev.abhishek.ecommerce.modules.category.mapper.CategoryMapper;
import dev.abhishek.ecommerce.modules.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.abhishek.ecommerce.common.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest createCategoryRequest) {
        log.info("Creating category with name={}", createCategoryRequest.getName());
        Category savedCategory = categoryRepository.save(categoryMapper.toEntity(createCategoryRequest));
        log.info("Category created successfully with id={}", savedCategory.getId());
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CategoryDto> getAllCategories(Pageable pageable) {
        Page<Category> page = categoryRepository.findAll(pageable);
        return new PagedResponse<>(categoryMapper.toDtoList(page.getContent()), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        return categoryMapper.toDto(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryByName(String name) {
        Category category = categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with name: " + name));
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategoryById(Long id, UpdateCategoryRequest updateCategoryRequest) {
        Category category = findById(id);
        category.setName(updateCategoryRequest.getName());
        log.info("Category updated successfully for id={}", id);
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = findById(id);
        categoryRepository.delete(category);
        log.info("Category deleted for id={}", id);
    }

    private Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Category not found for id={}", id);
            return new CategoryNotFoundException("The category not found with id: " + id);
        });
    }
}
