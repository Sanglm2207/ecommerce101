package com.kaidev99.ecommerce.controller;

import com.kaidev99.ecommerce.dto.CapturePayPalOrderRequestDTO;
import com.kaidev99.ecommerce.dto.CreatePayPalOrderRequestDTO;
import com.kaidev99.ecommerce.entity.Order;
import com.kaidev99.ecommerce.entity.User;
import com.kaidev99.ecommerce.payload.ApiResponse;
import com.kaidev99.ecommerce.service.OrderService;
import com.kaidev99.ecommerce.service.PayPalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PayPalService payPalService;
    private final OrderService orderService;

    @PostMapping("/paypal/create-order")
    public ResponseEntity<ApiResponse<Map<String, String>>> createPayPalOrder(
            @RequestBody CreatePayPalOrderRequestDTO request,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        Order order = orderService.getMyOrderDetail(currentUser, request.orderId());

        try {
            // Giả sử tiền tệ là USD, bạn có thể thay đổi
            JsonNode payPalOrder = payPalService.createOrder(order.getTotalAmount().doubleValue(), "USD");
            String payPalOrderId = payPalOrder.get("id").asText();

            return ResponseEntity.ok(ApiResponse.success(Map.of("payPalOrderId", payPalOrderId)));
        } catch (Exception e) {
            return new ResponseEntity<>(
                    ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating PayPal order", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/paypal/capture-order")
    public ResponseEntity<ApiResponse<String>> capturePayPalOrder(
            @RequestBody CapturePayPalOrderRequestDTO request) {

        try {
            JsonNode captureData = payPalService.captureOrder(request.payPalOrderId());
            // Kiểm tra trạng thái thanh toán
            if ("COMPLETED".equals(captureData.get("status").asText())) {

                // TODO: Lấy orderId của hệ thống bạn từ captureData (nếu bạn đã thêm vào lúc tạo)
                // và cập nhật trạng thái đơn hàng trong DB
                // Ví dụ: orderService.updateOrderStatus(myOrderId, OrderStatus.PROCESSING);

                return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Payment successful!"));
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                    ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Error capturing PayPal order", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(
                ApiResponse.error(HttpStatus.BAD_REQUEST, "Payment not completed"),
                HttpStatus.BAD_REQUEST);
    }
}
