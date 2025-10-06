package com.kaidev99.ecommerce.controller;

import com.kaidev99.ecommerce.entity.Notification;
import com.kaidev99.ecommerce.entity.User;
import com.kaidev99.ecommerce.payload.ApiResponse;
import com.kaidev99.ecommerce.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getMyUnreadNotifications(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<Notification> notifications = notificationService.getUnreadNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @PostMapping("/{id}/mark-as-read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        notificationService.markAsRead(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Notification marked as read"));
    }

    @PostMapping("/mark-all-as-read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "All notifications marked as read"));
    }
}
