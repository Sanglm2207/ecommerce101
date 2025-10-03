package com.kaidev99.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderRequestDTO(
        @NotBlank(message = "Customer name is required")
        String customerName,

        @NotBlank(message = "Customer phone is required")
        String customerPhone,

        @NotBlank(message = "Shipping address is required")
        String shippingAddress,

        @NotBlank(message = "Payment method is required")
        String paymentMethod
) {}