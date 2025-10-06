package com.kaidev99.ecommerce.service;

import com.kaidev99.ecommerce.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getUnreadNotifications(Long userId);

    void markAsRead(Long userId, Long notificationId);

    void markAllAsRead(Long userId);
}
