package com.kaidev99.ecommerce.controller;

import com.kaidev99.ecommerce.dto.OrderRequestDTO;
import com.kaidev99.ecommerce.entity.Order;
import com.kaidev99.ecommerce.entity.User;
import com.kaidev99.ecommerce.payload.ApiResponse;
import com.kaidev99.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(
            @Valid @RequestBody OrderRequestDTO orderRequestDTO,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();

        try {
            Order newOrder = orderService.createOrder(currentUser, orderRequestDTO);
            ApiResponse<Order> response = ApiResponse.success(HttpStatus.CREATED, "Order created successfully", newOrder);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            // Xử lý các lỗi nghiệp vụ như hết hàng, giỏ hàng rỗng
            return new ResponseEntity<>(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<Page<Order>>> getMyOrders(
            Authentication authentication,
            Pageable pageable) {

        User currentUser = (User) authentication.getPrincipal();
        Page<Order> orders = orderService.getMyOrders(currentUser, pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getMyOrderDetail(
            @PathVariable Long id,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        Order order = orderService.getMyOrderDetail(currentUser, id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }
}
