package com.kaidev99.ecommerce.controller;

import com.kaidev99.ecommerce.entity.Order;
import com.kaidev99.ecommerce.entity.OrderStatus;
import com.kaidev99.ecommerce.payload.ApiResponse;
import com.kaidev99.ecommerce.service.OrderService;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Order>>> getAllOrders(
            @Filter(entityClass = Order.class) Specification<Order> spec,
            Pageable pageable) {

        Page<Order> orders = orderService.getAllOrders(spec, pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderDetail(@PathVariable Long id) {
        Order order = orderService.getOrderByIdForAdmin(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {

        String statusString = statusUpdate.get("status");
        try {
            OrderStatus status = OrderStatus.valueOf(statusString.toUpperCase());
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success(updatedOrder));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    ApiResponse.error(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid status value"),
                    org.springframework.http.HttpStatus.BAD_REQUEST
            );
        }
    }
}
