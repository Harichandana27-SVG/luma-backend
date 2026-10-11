package com.luma.controller;

import com.luma.dto.ApiResponse;
import com.luma.model.Notification;
import com.luma.model.User;
import com.luma.service.AuthService;
import com.luma.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        List<Notification> list = notificationService.getUserNotifications(currentUser);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        long count = notificationService.getUnreadCount(currentUser);
        return ResponseEntity.ok(ApiResponse.ok(count));
    }

    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> markAllRead() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        notificationService.markAllAsRead(currentUser);
        return ResponseEntity.ok(ApiResponse.ok("All marked as read", "done"));
    }
}
