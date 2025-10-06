package com.kaidev99.ecommerce.service;

import com.kaidev99.ecommerce.config.RabbitMQConfig;
import com.kaidev99.ecommerce.dto.NotificationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void publishNotification(String routingKey, NotificationPayload payload) {
        // Kiểm tra xem có transaction nào đang active không
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            // Nếu có, đăng ký một callback để thực thi SAU KHI commit
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    log.info("Transaction committed. Publishing notification with key '{}'", routingKey);
                    sendToRabbitMQ(routingKey, payload);
                }
            });
        } else {
            // Nếu không có transaction, gửi ngay lập tức
            log.info("No active transaction. Publishing notification immediately with key '{}'", routingKey);
            sendToRabbitMQ(routingKey, payload);
        }
    }

    /**
     * Phương thức private để thực sự gửi message đến RabbitMQ.
     */
    private void sendToRabbitMQ(String routingKey, NotificationPayload payload) {
        try {
            // chuyển đổi object thành chuỗi JSON
            String jsonPayload = objectMapper.writeValueAsString(payload);

            // Gửi đi chuỗi JSON (là một String, hoàn toàn hợp lệ)
            rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, routingKey, jsonPayload);

            log.info("Successfully published JSON payload. RoutingKey: {}", routingKey);
        } catch (Exception e) {
            log.error("Failed to serialize and publish notification. RoutingKey: {}, Payload: {}", routingKey, payload, e);
        }
    }
}
