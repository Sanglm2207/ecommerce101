package com.kaidev99.ecommerce.dto;

import com.kaidev99.ecommerce.entity.Coupon.DiscountType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCouponDTO(
        @Size(min = 5, max = 20, message = "Code prefix must be between 5 and 20 characters if provided")
        String codePrefix, // Tiền tố cho mã, ví dụ: "SALE2024"

        @NotNull(message = "Discount type is required")
        DiscountType discountType,

        @NotNull(message = "Discount value is required")
        @Min(value = 0, message = "Discount value cannot be negative")
        BigDecimal discountValue,

        @NotNull(message = "Max usage is required")
        @Min(value = 1, message = "Max usage must be at least 1")
        int maxUsage,

        @NotNull(message = "Expiry date is required")
        @Future(message = "Expiry date must be in the future")
        LocalDate expiryDate,

        @NotNull(message = "Quantity to generate is required")
        @Min(value = 1, message = "Must generate at least 1 coupon")
        int quantity // Số lượng mã cần tạo
) {}