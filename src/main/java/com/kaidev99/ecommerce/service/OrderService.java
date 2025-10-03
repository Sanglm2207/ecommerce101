package com.kaidev99.ecommerce.service;

import com.kaidev99.ecommerce.dto.OrderRequestDTO;
import com.kaidev99.ecommerce.entity.Order;
import com.kaidev99.ecommerce.entity.OrderStatus;
import com.kaidev99.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface OrderService {
    Order createOrder(User user, OrderRequestDTO orderRequestDTO);

    Page<Order> getMyOrders(User user, Pageable pageable);

    Order getMyOrderDetail(User user, Long orderId);

    Page<Order> getAllOrders(Specification<Order> spec, Pageable pageable);

    Order getOrderByIdForAdmin(Long orderId);

    Order updateOrderStatus(Long orderId, OrderStatus status);
}
