package com.kaidev99.ecommerce.dto;


import com.kaidev99.ecommerce.entity.Product;
import java.util.List;

public record ProductDetailDTO(
        Product product,
        List<Product> relatedProducts
) {}
