package com.kaidev99.ecommerce.service.Impl;

import com.kaidev99.ecommerce.dto.CartItemDTO;
import com.kaidev99.ecommerce.dto.OrderRequestDTO;
import com.kaidev99.ecommerce.entity.*;
import com.kaidev99.ecommerce.exception.ResourceNotFoundException;
import com.kaidev99.ecommerce.repository.OrderRepository;
import com.kaidev99.ecommerce.repository.ProductRepository;
import com.kaidev99.ecommerce.service.CartService;
import com.kaidev99.ecommerce.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Override
    @Transactional // Đảm bảo tất cả thao tác thành công hoặc rollback
    public Order createOrder(User user, OrderRequestDTO orderRequestDTO) {
        // 1. Lấy giỏ hàng từ Redis
        List<CartItemDTO> cartItems = cartService.getCart(user.getUsername());
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot create order from an empty cart.");
        }

        // 2. Tạo đối tượng Order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCustomerName(orderRequestDTO.customerName());
        order.setCustomerPhone(orderRequestDTO.customerPhone());
        order.setShippingAddress(orderRequestDTO.shippingAddress());
        order.setPaymentMethod(orderRequestDTO.paymentMethod());

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 3. Xử lý từng item trong giỏ hàng
        for (CartItemDTO cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.product().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + cartItem.product().getId()));

            // 4. Kiểm tra và giảm số lượng tồn kho
            if (product.getStockQuantity() < cartItem.quantity()) {
                throw new IllegalStateException("Not enough stock for product: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - cartItem.quantity());
            productRepository.save(product); // Cập nhật lại sản phẩm

            // 5. Tạo OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.quantity());
            orderItem.setPriceAtPurchase(product.getPrice()); // Lưu giá tại thời điểm mua
            order.addOrderItem(orderItem);

            // 6. Tính tổng tiền
            totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(cartItem.quantity())));
        }

        order.setTotalAmount(totalAmount);

        // 7. Lưu đơn hàng (bao gồm cả OrderItems nhờ cascade = CascadeType.ALL)
        Order savedOrder = orderRepository.save(order);

        // 8. Xóa giỏ hàng khỏi Redis
        cartService.clearCart(user.getUsername());

        return savedOrder;
    }

    @Override
    public Page<Order> getMyOrders(User user, Pageable pageable) {
        return orderRepository.findByUser(user, pageable);
    }

    @Override
    public Order getMyOrderDetail(User user, Long orderId) {
        return orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId + " or you don't have permission to view it."));
    }

    @Override
    public Page<Order> getAllOrders(Specification<Order> spec, Pageable pageable) {
        return orderRepository.findAll(spec, pageable);
    }

    @Override
    public Order getOrderByIdForAdmin(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderByIdForAdmin(orderId);
        // Có thể thêm logic kiểm tra việc chuyển trạng thái có hợp lệ không ở đây
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
