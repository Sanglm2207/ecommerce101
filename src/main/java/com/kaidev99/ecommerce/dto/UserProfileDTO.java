package com.kaidev99.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserProfileDTO(
        @NotNull @Email(message = "Email format is invalid")
        String email,

        @Size(min = 2, message = "Full name must be at least 2 characters")
        String fullName,

        String phone,
        String address,
        String avatarUrl
) {}
