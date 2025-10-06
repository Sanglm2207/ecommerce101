package com.kaidev99.ecommerce.service;

import com.kaidev99.ecommerce.dto.*;
import com.kaidev99.ecommerce.entity.Product;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    Page<ProductSummaryDTO> findAll(Specification<Product> spec, Pageable pageable);

    Product getProductById(Long id);

    Product createProduct(ProductRequestDTO productRequestDTO);

    Page<ProductSummaryDTO>  getLatestProducts(int limit);

    List<Product> getFeaturedProducts(int limit);

    ProductDetailDTO getProductDetailById(Long id);

    List<ProductSuggestionDTO> getSearchSuggestions(String keyword, int limit);

    Product updateProduct(Long id, ProductRequestDTO productRequestDTO);

    void deleteProduct(Long id);

    ProductImportResult importProducts(MultipartFile file) throws IOException, CsvValidationException;

}