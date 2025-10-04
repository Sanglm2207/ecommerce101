package com.kaidev99.ecommerce.dto;

import java.math.BigDecimal;

public record ProductSuggestionDTO(
        Long id,
        String name,
        BigDecimal price
        // Có thể thêm ảnh thumbnail nếu cần
) {
}
