package com.kaidev99.ecommerce.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequestDTO(
                @NotBlank(message = "Product name is required") @Size(min = 3, max = 200, message = "Product name must be between 3 and 200 characters") String name,

                String description,

                @NotNull(message = "Price is required") @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0") BigDecimal price,

                @NotNull(message = "Stock quantity is required") @Min(value = 0, message = "Stock quantity cannot be negative") int stockQuantity) {
}