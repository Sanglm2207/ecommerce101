package com.kaidev99.ecommerce.controller;

import java.util.List;

import com.kaidev99.ecommerce.entity.Product;
import com.kaidev99.ecommerce.service.ProductService;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
import org.springframework.web.bind.annotation.RestController;

import com.kaidev99.ecommerce.dto.CategoryDTO;
import com.kaidev99.ecommerce.entity.Category;
import com.kaidev99.ecommerce.payload.ApiResponse;
import com.kaidev99.ecommerce.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        Category newCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.CREATED, "Category created successfully", newCategory),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        Category updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Category updated successfully", updatedCategory));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Category deleted successfully"));
    }

    /**
     * API để lấy danh sách sản phẩm thuộc về một category cụ thể.
     * Hỗ trợ đầy đủ lọc, sắp xếp, phân trang trên tập sản phẩm đó.
     */
    @GetMapping("/{id}/products")
    public ResponseEntity<ApiResponse<Page<Product>>> getProductsByCategoryId(
            @PathVariable Long id,
            @Filter(entityClass = Product.class) Specification<Product> spec,
            Pageable pageable) {

        // Tạo một specification để lọc theo categoryId
        Specification<Product> categorySpec = (root, query, builder) ->
                builder.equal(root.get("category").get("id"), id);

        // Kết hợp specification của category với specification từ filter của người dùng
        Specification<Product> finalSpec = categorySpec.and(spec);

        Page<Product> productPage = productService.findAll(finalSpec, pageable);

        return ResponseEntity.ok(ApiResponse.success(productPage));
    }
}
