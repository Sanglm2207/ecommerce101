package com.kaidev99.ecommerce.controller;

import com.kaidev99.ecommerce.dto.ProductRequestDTO;
import com.kaidev99.ecommerce.entity.Product;
import com.kaidev99.ecommerce.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // API để tạo sản phẩm mới
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        Product createdProduct = productService.createProduct(productRequestDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // API để lấy danh sách tất cả sản phẩm
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products); // Tương đương new ResponseEntity<>(products, HttpStatus.OK)
    }

    // API để lấy một sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
}