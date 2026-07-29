package dev.abhishek.ecommerce.modules.category.controller;

import dev.abhishek.ecommerce.modules.category.dtos.CategoryDto;
import dev.abhishek.ecommerce.modules.category.dtos.CreateCategoryRequest;
import dev.abhishek.ecommerce.modules.category.dtos.UpdateCategoryRequest;
import dev.abhishek.ecommerce.modules.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.PageRequest;
import dev.abhishek.ecommerce.common.dto.PagedResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Categories", description = "Endpoints for managing product categories")
@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories", description = "Retrieves a list of all categories")
    @GetMapping
    public ResponseEntity<PagedResponse<CategoryDto>> getAllCategories(
            @RequestParam(required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(PageRequest.of(pageNo, pageSize)));
    }

    @Operation(summary = "Get category by ID", description = "Retrieves a single category by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Operation(summary = "Get category by name", description = "Retrieves a single category by its name")
    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryDto> getCategoryByName(@PathVariable String name) {
        return ResponseEntity.ok(categoryService.getCategoryByName(name));
    }

    // only admins can Create Update and Delete category
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create category", description = "Creates a new category (Admin only)")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequest categoryRequest) {
        return new ResponseEntity<>(categoryService.createCategory(categoryRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete category", description = "Deletes a category by its ID (Admin only)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") Long id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a category", description = "Updates an existing category by its ID (Admin only)")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest
    ) {
        return ResponseEntity.ok(categoryService.updateCategoryById(id, updateCategoryRequest));
    }
}
