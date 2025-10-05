package com.kaidev99.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "coupons", indexes = {
        @Index(name = "idx_coupon_code", columnList = "code", unique = true)
})
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // Mã giảm giá, ví dụ: "SALE50K", "FREESHIP"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType; // Loại giảm giá: PERCENTAGE hoặc FIXED_AMOUNT

    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue; // Giá trị giảm (ví dụ: 10 cho 10%, 50000 cho 50K)

    @Column(name = "max_usage", nullable = false)
    private int maxUsage; // Tổng số lượt có thể sử dụng

    @Column(name = "usage_count", nullable = false)
    private int usageCount = 0; // Số lượt đã sử dụng

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate; // Ngày hết hạn

    @Column(nullable = false)
    private boolean isActive = true; // Trạng thái: true = có thể sử dụng, false = bị vô hiệu hóa

    // Enum cho loại giảm giá
    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT
    }
}