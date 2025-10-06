package com.kaidev99.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPayload {
    private String type; // Ví dụ: "NEW_ORDER", "LOW_STOCK"
    private String message; // Nội dung thông báo
    private String link; // Đường dẫn để click vào, ví dụ: "/admin/orders/123"
    private LocalDateTime timestamp;
}
