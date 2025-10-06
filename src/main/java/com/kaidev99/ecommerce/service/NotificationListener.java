package com.kaidev99.ecommerce.service;

import com.kaidev99.ecommerce.config.RabbitMQConfig;
import com.kaidev99.ecommerce.dto.NotificationPayload;
import com.kaidev99.ecommerce.entity.Notification;
import com.kaidev99.ecommerce.entity.User;
import com.kaidev99.ecommerce.repository.NotificationRepository;
import com.kaidev99.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @RabbitListener(bindings = @org.springframework.amqp.rabbit.annotation.QueueBinding(
            value = @org.springframework.amqp.rabbit.annotation.Queue,
            exchange = @org.springframework.amqp.rabbit.annotation.Exchange(value = RabbitMQConfig.NOTIFICATION_EXCHANGE, type = "topic"),
            key = "notification.admin.#"
    ))
    @Transactional
    public void handleAdminNotifications(String jsonPayload) {
        try {
            NotificationPayload payload = objectMapper.readValue(jsonPayload, NotificationPayload.class);
            log.info("Received admin notification: {}", payload.getMessage());

            // Gửi message đến WebSocket topic MỘT LẦN DUY NHẤT
            messagingTemplate.convertAndSend("/topic/notifications/admin", payload);
            log.info("Broadcasted notification to /topic/notifications/admin");

            // Tìm tất cả các admin
            List<User> admins = userRepository.findByRole(com.kaidev99.ecommerce.entity.Role.ADMIN);

            // Lưu một bản ghi thông báo cho MỖI admin
            for (User admin : admins) {
                Notification notification = new Notification();
                notification.setUser(admin);
                notification.setMessage(payload.getMessage());
                notification.setLink(payload.getLink());
                notification.setRead(false);
                notification.setType(payload.getType());
                notificationRepository.save(notification);
                log.info("Saved notification for admin: {}", admin.getUsername());
            }

        } catch (Exception e) {
            log.error("Failed to process admin notification payload: {}", jsonPayload, e);
        }
    }


    @RabbitListener(bindings = @org.springframework.amqp.rabbit.annotation.QueueBinding(
            value = @org.springframework.amqp.rabbit.annotation.Queue,
            exchange = @org.springframework.amqp.rabbit.annotation.Exchange(value = RabbitMQConfig.NOTIFICATION_EXCHANGE, type = "topic"),
            key = "notification.user.*"
    ))
    @Transactional
    public void handleUserNotifications(String jsonPayload, Message message) {
        try {
            NotificationPayload payload = objectMapper.readValue(jsonPayload, NotificationPayload.class);

            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            String username = routingKey.substring("notification.user.".length());

            log.info("Received notification for user '{}': {}", username, payload.getMessage());

            userRepository.findByUsername(username).ifPresent(user -> {
                // 1. Lưu thông báo vào DB
                Notification notification = new Notification();
                notification.setUser(user);
                notification.setMessage(payload.getMessage());
                notification.setLink(payload.getLink());
                notification.setType(payload.getType());
                notificationRepository.save(notification);
                log.info("Saved notification for user: {}", user.getUsername());

                // 2. Đẩy thông báo đến kênh cá nhân của user đó
                messagingTemplate.convertAndSendToUser(user.getUsername(), "/queue/notifications", payload);
                log.info("Sent notification to user-specific queue: {}", user.getUsername());
            });
        } catch (Exception e) {
            log.error("Failed to process user notification payload: {}", jsonPayload, e);
        }
    }
}
