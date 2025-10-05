package com.kaidev99.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Lob // Dành cho các trường văn bản dài
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> imageUrls;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY loading để tối ưu hiệu năng
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "is_featured", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isFeatured = false; // Mặc định là không nổi bật

    @CreationTimestamp // Tự động gán thời gian tạo
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
