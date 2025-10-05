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
    Page<ProductSummaryDTO> findAll(Specification<Product> spec, Pageable pageable);

    Product getProductById(Long id);

    Product createProduct(ProductRequestDTO productRequestDTO);

    Page<ProductSummaryDTO>  getLatestProducts(int limit);

    List<Product> getFeaturedProducts(int limit);

    ProductDetailDTO getProductDetailById(Long id);

    List<ProductSuggestionDTO> getSearchSuggestions(String keyword, int limit);

    /**
     * Cập nhật thông tin một sản phẩm.
     * @param id ID của sản phẩm cần cập nhật
     * @param productRequestDTO DTO chứa thông tin mới
     * @return Sản phẩm sau khi đã được cập nhật
     */
    Product updateProduct(Long id, ProductRequestDTO productRequestDTO);

    /**
     * Xóa một sản phẩm.
     * @param id ID của sản phẩm cần xóa
     */
    void deleteProduct(Long id);
}