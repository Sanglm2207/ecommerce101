package com.kaidev99.ecommerce.controller;

import com.kaidev99.ecommerce.dto.*;
import com.kaidev99.ecommerce.entity.Product;
import com.kaidev99.ecommerce.payload.ApiResponse;
import com.kaidev99.ecommerce.service.ProductService;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // --- API CHO ADMIN: Tạo sản phẩm ---
    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        Product createdProduct = productService.createProduct(productRequestDTO);
        ApiResponse<Product> response = ApiResponse.success(HttpStatus.CREATED, "Product created successfully",
                createdProduct);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // --- API CHO ADMIN: Cập nhật sản phẩm ---
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        Product updatedProduct = productService.updateProduct(id, productRequestDTO); // Giả sử bạn có service này
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK,"Product updated successfully", updatedProduct));
    }

    // --- API CHO ADMIN: Xóa sản phẩm ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id); // Giả sử bạn có service này
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Product deleted successfully"));
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductImportResult>> importProducts(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.error(HttpStatus.BAD_REQUEST, "Please upload a file."), HttpStatus.BAD_REQUEST);
        }
        try {
            ProductImportResult result = productService.importProducts(file);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK,"Import process completed.", result));
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to import products: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/import/template")
    public ResponseEntity<Resource> downloadTemplate(@RequestParam("type") String type) {
        String filename;
        if ("excel".equalsIgnoreCase(type)) {
            filename = "product_template.xlsx";
        } else if ("csv".equalsIgnoreCase(type)) {
            filename = "product_template.csv";
        } else {
            return ResponseEntity.badRequest().build();
        }

        Resource resource = new ClassPathResource("templates/" + filename);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // --- CÁC API PUBLIC (GET) ---

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductSummaryDTO>>> getProducts(
            @Filter(entityClass = Product.class) Specification<Product> spec,
            Pageable pageable) {

        Specification<Product> finalSpec = (spec == null) ? Specification.where((Specification<Product>) null) : spec;

        Page<ProductSummaryDTO> productDtoPage = productService.findAll(finalSpec, pageable);
        return ResponseEntity.ok(ApiResponse.success(productDtoPage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> getProductById(@PathVariable Long id) {
        ProductDetailDTO productDetail = productService.getProductDetailById(id);
        return ResponseEntity.ok(ApiResponse.success(productDetail));
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<Page<ProductSummaryDTO>>> getLatestProducts(@RequestParam(defaultValue = "8") int limit) {
        Page<ProductSummaryDTO> productDtos = productService.getLatestProducts(limit);
        return ResponseEntity.ok(ApiResponse.success(productDtos));
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