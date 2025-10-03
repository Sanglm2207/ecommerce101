package com.kaidev99.ecommerce.repository;

import com.kaidev99.ecommerce.entity.Order;
import com.kaidev99.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    /**
     * Tìm tất cả đơn hàng của một người dùng, có phân trang.
     * @param user Người dùng
     * @param pageable Thông tin phân trang và sắp xếp
     * @return Một trang các đơn hàng
     */
    Page<Order> findByUser(User user, Pageable pageable);

    /**
     * Tìm một đơn hàng cụ thể theo ID và người dùng.
     * Dùng để đảm bảo người dùng chỉ có thể xem đơn hàng của chính họ.
     * @param id ID của đơn hàng
     * @param user Người dùng
     * @return Optional chứa đơn hàng nếu tìm thấy
     */
    Optional<Order> findByIdAndUser(Long id, User user);
}
