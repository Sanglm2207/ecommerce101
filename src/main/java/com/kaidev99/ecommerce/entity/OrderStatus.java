package com.kaidev99.ecommerce.entity;

public enum OrderStatus {
    PENDING,        // Chờ xác nhận
    PROCESSING,     // Đang xử lý
    SHIPPED,        // Đang giao hàng
    DELIVERED,      // Đã giao thành công
    CANCELED        // Đã hủy
}
