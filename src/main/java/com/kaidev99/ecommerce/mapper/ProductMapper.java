package com.kaidev99.ecommerce.mapper;

import com.kaidev99.ecommerce.dto.CategoryDTO;
import com.kaidev99.ecommerce.dto.ProductSummaryDTO;
import com.kaidev99.ecommerce.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductSummaryDTO toProductSummaryDTO(Product product) {
        if (product == null) {
            return null;
        }

        CategoryDTO categoryDTO = new CategoryDTO(
                product.getCategory().getId(),
                product.getCategory().getName()
        );

        return new ProductSummaryDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCreatedAt(),
                categoryDTO
        );
    }
}
