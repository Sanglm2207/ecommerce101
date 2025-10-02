package com.kaidev99.ecommerce.service.Impl;

import com.kaidev99.ecommerce.dto.ProductRequestDTO;
import com.kaidev99.ecommerce.entity.Product;
import com.kaidev99.ecommerce.exception.ResourceNotFoundException;
import com.kaidev99.ecommerce.repository.ProductRepository;
import com.kaidev99.ecommerce.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public Product createProduct(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setStockQuantity(productRequestDTO.getStockQuantity());

        return productRepository.save(product);
    }
}
