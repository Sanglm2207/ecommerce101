package com.kaidev99.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(
        @NotBlank(message = "Username cannot be blank") String username,

        @NotBlank(message = "Password cannot be blank") String password) {
}