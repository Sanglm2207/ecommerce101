package com.kaidev99.ecommerce.repository;

import com.kaidev99.ecommerce.entity.Order;
import com.kaidev99.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Page<Order> findByUser(User user, Pageable pageable);

    Optional<Order> findByIdAndUser(Long id, User user);

    // Tính tổng doanh thu trong một khoảng thời gian
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'DELIVERED' AND o.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal findTotalRevenueBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Đếm tổng số đơn hàng trong một khoảng thời gian
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    long countOrdersBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Lấy doanh thu theo từng ngày
    @Query(value = "SELECT CAST(o.order_date AS DATE) as date, SUM(o.total_amount) as daily_revenue " +
            "FROM orders o WHERE o.status = 'DELIVERED' AND o.order_date BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(o.order_date AS DATE) ORDER BY date ASC", nativeQuery = true)
    List<Object[]> findRevenueOverTime(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Đếm số lượng đơn hàng theo từng trạng thái
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();

    // Lấy các đơn hàng gần đây nhất
    List<Order> findTop5ByOrderByOrderDateDesc();
}
