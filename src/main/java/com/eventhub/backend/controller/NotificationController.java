package com.eventhub.backend.controller;

import com.eventhub.backend.entity.Notification;
import com.eventhub.backend.entity.User;
import com.eventhub.backend.repository.NotificationRepository;
import com.eventhub.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationController(
            NotificationRepository notificationRepository,
            UserRepository userRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // GET NOTIFICATIONS FOR ONE STUDENT
    // =========================================================

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(userId)
        );
    }

    // =========================================================
    // CREATE NOTIFICATION FOR ONE STUDENT
    // =========================================================

    @PostMapping
    public ResponseEntity<?> createNotification(
            @RequestParam Long userId,
            @RequestParam String type,
            @RequestParam String title,
            @RequestParam String message
    ) {

        User user = userRepository
                .findById(userId)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("User not found");
        }

        Notification notification =
                new Notification(
                        user,
                        type,
                        title,
                        message
                );

        Notification savedNotification =
                notificationRepository.save(notification);

        return ResponseEntity.ok(savedNotification);
    }

    // =========================================================
    // SEND NOTIFICATION TO ALL STUDENTS
    // =========================================================

    @PostMapping("/broadcast")
    public ResponseEntity<?> broadcastNotification(
            @RequestParam String type,
            @RequestParam String title,
            @RequestParam String message
    ) {

        List<User> students =
                userRepository.findByRole("STUDENT");

        if (students.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("No students found");
        }

        List<Notification> notifications =
                new ArrayList<>();

        for (User student : students) {

            Notification notification =
                    new Notification(
                            student,
                            type,
                            title,
                            message
                    );

            notifications.add(notification);
        }

        List<Notification> savedNotifications =
                notificationRepository.saveAll(
                        notifications
                );

        return ResponseEntity.ok(savedNotifications);
    }

    // =========================================================
    // MARK ONE NOTIFICATION AS READ
    // =========================================================

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id
    ) {

        Notification notification =
                notificationRepository.findById(id)
                        .orElse(null);

        if (notification == null) {
            return ResponseEntity.notFound().build();
        }

        notification.setReadStatus(true);

        return ResponseEntity.ok(
                notificationRepository.save(notification)
        );
    }

    // =========================================================
    // MARK ALL NOTIFICATIONS AS READ
    // =========================================================

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<?> markAllAsRead(
            @PathVariable Long userId
    ) {

        List<Notification> notifications =
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(userId);

        for (Notification notification : notifications) {
            notification.setReadStatus(true);
        }

        notificationRepository.saveAll(notifications);

        return ResponseEntity.ok(
                "All notifications marked as read"
        );
    }

    // =========================================================
    // DELETE ALL NOTIFICATIONS FOR ONE STUDENT
    // =========================================================

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteUserNotifications(
            @PathVariable Long userId
    ) {

        notificationRepository.deleteByUserId(userId);

        return ResponseEntity.noContent().build();
    }
}