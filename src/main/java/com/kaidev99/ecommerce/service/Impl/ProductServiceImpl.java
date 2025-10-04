package com.kaidev99.ecommerce.service.Impl;

import com.kaidev99.ecommerce.dto.ProductDetailDTO;
import com.kaidev99.ecommerce.dto.ProductRequestDTO;
import com.kaidev99.ecommerce.dto.ProductSuggestionDTO;
import com.kaidev99.ecommerce.dto.ProductSummaryDTO;
import com.kaidev99.ecommerce.entity.Category;
import com.kaidev99.ecommerce.entity.Product;
import com.kaidev99.ecommerce.exception.ResourceNotFoundException;
import com.kaidev99.ecommerce.mapper.ProductMapper;
import com.kaidev99.ecommerce.repository.ProductRepository;
import com.kaidev99.ecommerce.service.CategoryService;
import com.kaidev99.ecommerce.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;

    @Override
    public Page<Product> findAll(Specification<Product> spec, Pageable pageable) {
        return productRepository.findAll(spec, pageable);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public Product createProduct(ProductRequestDTO productRequestDTO) {
        Category category = categoryService.getCategoryById(productRequestDTO.categoryId());

        Product product = new Product();
        product.setName(productRequestDTO.name());
        product.setDescription(productRequestDTO.description());
        product.setPrice(productRequestDTO.price());
        product.setStockQuantity(productRequestDTO.stockQuantity());
        product.setCategory(category);

        return productRepository.save(product);
    }

    @Override
    public Page<ProductSummaryDTO> getLatestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductSummaryDTO> dtos = productPage.getContent().stream()
                .map(productMapper::toProductSummaryDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, productPage.getTotalElements());
    }

    @Override
    public List<Product> getFeaturedProducts(int limit) {
        return productRepository.findByIsFeaturedTrueOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }

    @Override
    public ProductDetailDTO getProductDetailById(Long id) {
        Product product = this.getProductById(id); // Tận dụng lại phương thức đã có
        List<Product> relatedProducts = productRepository.findTop4ByCategoryIdAndIdNot(
                product.getCategory().getId(),
                id
        );
        return new ProductDetailDTO(product, relatedProducts);
    }

    @Override
    public List<ProductSuggestionDTO> getSearchSuggestions(String keyword, int limit) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return productRepository.findSuggestions(keyword, PageRequest.of(0, limit));
    }
}
