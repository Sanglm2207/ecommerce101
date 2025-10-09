package com.kaidev99.ecommerce.repository;

import com.kaidev99.ecommerce.dto.DashboardStatsDTO;
import com.kaidev99.ecommerce.entity.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Lấy top sản phẩm bán chạy
    @Query("SELECT new com.kaidev99.ecommerce.dto.DashboardStatsDTO$TopProductDTO(oi.product.id, oi.product.name, SUM(oi.quantity)) " +
            "FROM OrderItem oi WHERE oi.order.status = 'DELIVERED' AND oi.order.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY oi.product.id, oi.product.name ORDER BY SUM(oi.quantity) DESC")
    List<DashboardStatsDTO.TopProductDTO> findTopSellingProducts(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
}
