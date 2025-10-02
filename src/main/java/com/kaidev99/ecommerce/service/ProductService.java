package com.kaidev99.ecommerce.service;

import com.kaidev99.ecommerce.dto.ProductRequestDTO;
import com.kaidev99.ecommerce.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();

    Product getProductById(Long id);

    Product createProduct(ProductRequestDTO productRequestDTO);
}