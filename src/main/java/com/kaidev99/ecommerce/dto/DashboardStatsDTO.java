package com.kaidev99.ecommerce.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardStatsDTO {
    // --- Các chỉ số tổng quan (KPI Cards) ---
    private BigDecimal totalRevenue; // Tổng doanh thu
    private long totalOrders; // Tổng số đơn hàng
    private long newCustomers; // Lượng khách hàng mới
    private double conversionRate; // Tỷ lệ chuyển đổi

    // --- Dữ liệu cho biểu đồ (Charts) ---
    // Doanh thu theo thời gian (ví dụ: 7 ngày qua)
    // Key: Ngày (String "YYYY-MM-DD"), Value: Doanh thu (BigDecimal)
    private Map<String, BigDecimal> revenueOverTime;

    // Top sản phẩm bán chạy
    private List<TopProductDTO> topSellingProducts;

    // Trạng thái đơn hàng
    // Key: Trạng thái (String "PENDING", "SHIPPED"), Value: Số lượng (Long)
    private Map<String, Long> orderStatusDistribution;

    // --- Dữ liệu cho các danh sách (Lists) ---
    private List<RecentOrderDTO> recentOrders; // Các đơn hàng gần đây

    // --- DTOs con ---
    @Data
    @Builder
    public static class TopProductDTO {
        private Long productId;
        private String productName;
        private long totalSold;
    }

    @Data
    @Builder
    public static class RecentOrderDTO {
        private Long orderId;
        private String customerName;
        private BigDecimal totalAmount;
        private String status;
    }
}