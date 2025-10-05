package com.kaidev99.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductSummaryDTO(
        Long id,
        String name,
        BigDecimal price,
        LocalDateTime createdAt,
        CategoryDTO category,
        String thumbnailUrl
) {}
