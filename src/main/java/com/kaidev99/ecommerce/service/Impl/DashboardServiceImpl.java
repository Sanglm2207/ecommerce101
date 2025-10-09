package com.kaidev99.ecommerce.service.Impl;

import com.kaidev99.ecommerce.dto.DashboardStatsDTO;
import com.kaidev99.ecommerce.repository.OrderItemRepository;
import com.kaidev99.ecommerce.repository.OrderRepository;
import com.kaidev99.ecommerce.repository.UserRepository;
import com.kaidev99.ecommerce.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public DashboardStatsDTO getDashboardStats(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // --- Lấy dữ liệu ---
        BigDecimal totalRevenue = orderRepository.findTotalRevenueBetween(startDateTime, endDateTime);
        long totalOrders = orderRepository.countOrdersBetween(startDateTime, endDateTime);
        long newCustomers = userRepository.countNewUsersBetween(startDateTime, endDateTime);

        List<Object[]> revenueOverTimeData = orderRepository.findRevenueOverTime(startDateTime, endDateTime);
        Map<String, BigDecimal> revenueOverTime = revenueOverTimeData.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(), // date
                        row -> new BigDecimal(row[1].toString()) // daily_revenue
                ));

        List<Object[]> orderStatusData = orderRepository.countOrdersByStatus();
        Map<String, Long> orderStatusDistribution = orderStatusData.stream()
                .collect(Collectors.toMap(
                        row -> ((Enum<?>) row[0]).name(),
                        row -> (Long) row[1]
                ));

        List<DashboardStatsDTO.TopProductDTO> topProducts = orderItemRepository.findTopSellingProducts(startDateTime, endDateTime, PageRequest.of(0, 5));

        List<DashboardStatsDTO.RecentOrderDTO> recentOrders = orderRepository.findTop5ByOrderByOrderDateDesc().stream()
                .map(order -> DashboardStatsDTO.RecentOrderDTO.builder()
                        .orderId(order.getId())
                        .customerName(order.getCustomerName())
                        .totalAmount(order.getTotalAmount())
                        .status(order.getStatus().name())
                        .build())
                .collect(Collectors.toList());

        // --- Xây dựng DTO trả về ---
        return DashboardStatsDTO.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .newCustomers(newCustomers)
                .conversionRate(0.0) // Tỷ lệ chuyển đổi cần logic phức tạp hơn (tracking user visits)
                .revenueOverTime(revenueOverTime)
                .topSellingProducts(topProducts)
                .orderStatusDistribution(orderStatusDistribution)
                .recentOrders(recentOrders)
                .build();
    }
}
