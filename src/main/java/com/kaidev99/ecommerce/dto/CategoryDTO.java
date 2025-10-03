package com.kaidev99.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryDTO(
        @NotBlank(message = "Category name cannot be blank") @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters") String name) {

}
