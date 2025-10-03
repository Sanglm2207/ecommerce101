package com.kaidev99.ecommerce.dto;

import com.kaidev99.ecommerce.entity.Product;

public record CartItemDTO(
        Product product,
        int quantity
) {
}
