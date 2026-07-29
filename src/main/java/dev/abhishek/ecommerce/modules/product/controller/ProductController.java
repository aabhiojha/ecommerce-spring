package dev.abhishek.ecommerce.modules.product.controller;

import dev.abhishek.ecommerce.modules.product.dto.CreateProductRequest;
import dev.abhishek.ecommerce.modules.product.dto.ProductDto;
import dev.abhishek.ecommerce.modules.product.dto.UpdateProductRequest;
import dev.abhishek.ecommerce.modules.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import dev.abhishek.ecommerce.common.dto.PagedResponse;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Tag(name = "Products", description = "Endpoints for managing products")
@Slf4j
@Validated
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    /**
     * Sorting is restricted to real columns; an unknown value used to blow up with a 500.
     */
    private static final Set<String> SORTABLE_FIELDS =
            Set.of("id", "name", "brand", "price", "inventory", "createdAt", "updatedAt");

    private final ProductService productService;

    @Operation(summary = "Get product by ID", description = "Retrieves a single product by its ID")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @Operation(summary = "Get all products", description = "Retrieves a paginated list of all products with optional filtering and sorting")
    @GetMapping
    public ResponseEntity<PagedResponse<ProductDto>> getAllProducts(
            @RequestParam(required = false, defaultValue = "0") @Min(0) int pageNo,
            @RequestParam(required = false, defaultValue = "20") @Min(1) @Max(100) int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDir,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description
    ) {
        if (!SORTABLE_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Unsupported sort field: " + sortBy);
        }

        Sort sort = sortDir.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return ResponseEntity.ok(
                productService.getAllProducts(PageRequest.of(pageNo, pageSize, sort), id, name, description));
    }

    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Create a product", description = "Creates a new product (Seller or Admin only)")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest createProductRequest) {
        return new ResponseEntity<>(productService.addProduct(createProductRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Update a product", description = "Updates an existing product by its ID (Seller only)")
    @PatchMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(
            @Valid @RequestBody UpdateProductRequest updateProductRequest,
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(productService.updateProductById(updateProductRequest, productId));
    }

    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Delete a product", description = "Deletes an existing product by its ID (Seller only)")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProductById(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get products by category", description = "Retrieves all products belonging to a specific category")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PagedResponse<ProductDto>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, PageRequest.of(pageNo, pageSize)));
    }

    @Operation(summary = "Get products by brand", description = "Retrieves all products from a specific brand")
    @GetMapping("/brand/{brand}")
    public ResponseEntity<PagedResponse<ProductDto>> getProductsByBrand(
            @PathVariable String brand,
            @RequestParam(required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(productService.getProductsByBrand(brand, PageRequest.of(pageNo, pageSize)));
    }

    @Operation(summary = "Get products by brand and name", description = "Retrieves all products matching a brand and name")
    @GetMapping("/search/brand-name")
    public ResponseEntity<PagedResponse<ProductDto>> getProductsByBrandAndName(
            @RequestParam String brand,
            @RequestParam String name,
            @RequestParam(required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(productService.getProductsByBrandAndName(brand, name, PageRequest.of(pageNo, pageSize)));
    }
}
