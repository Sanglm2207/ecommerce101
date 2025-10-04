package com.kaidev99.ecommerce.service;

import com.kaidev99.ecommerce.dto.ProductDetailDTO;
import com.kaidev99.ecommerce.dto.ProductRequestDTO;
import com.kaidev99.ecommerce.dto.ProductSuggestionDTO;
import com.kaidev99.ecommerce.dto.ProductSummaryDTO;
import com.kaidev99.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ProductService {
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    Product getProductById(Long id);

    Product createProduct(ProductRequestDTO productRequestDTO);

    Page<ProductSummaryDTO>  getLatestProducts(int limit);

    List<Product> getFeaturedProducts(int limit);

    ProductDetailDTO getProductDetailById(Long id);

    List<ProductSuggestionDTO> getSearchSuggestions(String keyword, int limit);

}