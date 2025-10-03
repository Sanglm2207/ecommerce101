package com.kaidev99.ecommerce.service.Impl;

import com.kaidev99.ecommerce.dto.ProductDetailDTO;
import com.kaidev99.ecommerce.dto.ProductRequestDTO;
import com.kaidev99.ecommerce.dto.ProductSuggestionDTO;
import com.kaidev99.ecommerce.entity.Category;
import com.kaidev99.ecommerce.entity.Product;
import com.kaidev99.ecommerce.exception.ResourceNotFoundException;
import com.kaidev99.ecommerce.repository.ProductRepository;
import com.kaidev99.ecommerce.service.CategoryService;
import com.kaidev99.ecommerce.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

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
    public List<Product> getLatestProducts(int limit) {
        // Tạo một PageRequest chỉ để lấy trang đầu tiên với số lượng 'limit'
        return productRepository.findByOrderByCreatedAtDesc(PageRequest.of(0, limit));
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
