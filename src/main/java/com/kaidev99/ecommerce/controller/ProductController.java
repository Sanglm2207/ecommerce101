package com.kaidev99.ecommerce.controller;

import com.kaidev99.ecommerce.dto.ProductDetailDTO;
import com.kaidev99.ecommerce.dto.ProductRequestDTO;
import com.kaidev99.ecommerce.dto.ProductSuggestionDTO;
import com.kaidev99.ecommerce.entity.Product;
import com.kaidev99.ecommerce.payload.ApiResponse;
import com.kaidev99.ecommerce.service.ProductService;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // API để tạo sản phẩm mới
    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        Product createdProduct = productService.createProduct(productRequestDTO);
        ApiResponse<Product> response = ApiResponse.success(HttpStatus.CREATED, "Product created successfully",
                createdProduct);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<Product>>> getProducts(
            @Filter Specification<Product> spec,
            Pageable pageable
    ) {

        Page<Product> productPage = productService.findAll(spec, pageable);
        System.out.println("Product Page: " + productPage);
        return ResponseEntity.ok(ApiResponse.success(productPage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> getProductById(@PathVariable Long id) {
        ProductDetailDTO productDetail = productService.getProductDetailById(id);
        return ResponseEntity.ok(ApiResponse.success(productDetail));
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<List<Product>>> getLatestProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<Product> products = productService.getLatestProducts(limit);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<Product>>> getFeaturedProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<Product> products = productService.getFeaturedProducts(limit);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<ProductSuggestionDTO>>> getSearchSuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "5") int limit) {
        List<ProductSuggestionDTO> suggestions = productService.getSearchSuggestions(keyword, limit);
        return ResponseEntity.ok(ApiResponse.success(suggestions));
    }
}