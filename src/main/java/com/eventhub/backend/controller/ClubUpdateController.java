package com.eventhub.backend.controller;

import com.eventhub.backend.entity.Club;
import com.eventhub.backend.entity.ClubUpdate;
import com.eventhub.backend.entity.Notification;
import com.eventhub.backend.entity.User;

import com.eventhub.backend.repository.ClubRepository;
import com.eventhub.backend.repository.ClubUpdateRepository;
import com.eventhub.backend.repository.NotificationRepository;
import com.eventhub.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/club-updates")
public class ClubUpdateController {

    private final ClubUpdateRepository updateRepository;
    private final ClubRepository clubRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public ClubUpdateController(
            ClubUpdateRepository updateRepository,
            ClubRepository clubRepository,
            NotificationRepository notificationRepository,
            UserRepository userRepository
    ) {
        this.updateRepository = updateRepository;
        this.clubRepository = clubRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // GET ALL UPDATES FOR A CLUB
    // =========================================================

    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<ClubUpdate>> getClubUpdates(
            @PathVariable Long clubId
    ) {

        return ResponseEntity.ok(
                updateRepository
                        .findByClubIdOrderByCreatedAtDesc(clubId)
        );
    }

    // =========================================================
    // CREATE CLUB UPDATE
    // =========================================================

    @PostMapping
    public ResponseEntity<?> createUpdate(
            @RequestParam Long clubId,
            @RequestParam String title,
            @RequestParam String message
    ) {

        Optional<Club> clubOptional =
                clubRepository.findById(clubId);

        if (clubOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Club not found");
        }

        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Title is required");
        }

        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Message is required");
        }

        Club club = clubOptional.get();

        // =====================================================
        // SAVE CLUB UPDATE
        // =====================================================

        ClubUpdate update = new ClubUpdate(
                club,
                title.trim(),
                message.trim()
        );

        ClubUpdate savedUpdate =
                updateRepository.save(update);

        // =====================================================
        // SEND NOTIFICATION TO ALL STUDENTS
        // =====================================================

        List<User> students =
                userRepository.findByRole("STUDENT");

        List<Notification> notifications =
                new ArrayList<>();

        for (User student : students) {

            Notification notification =
                    new Notification(
                            student,
                            "CLUB_UPDATE",
                            club.getName() + " - " + title.trim(),
                            message.trim()
                    );

            notifications.add(notification);
        }

        if (!notifications.isEmpty()) {

            notificationRepository.saveAll(
                    notifications
            );
        }

        return ResponseEntity.ok(savedUpdate);
    }

    // =========================================================
    // DELETE CLUB UPDATE
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUpdate(
            @PathVariable Long id
    ) {

        if (!updateRepository.existsById(id)) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        updateRepository.deleteById(id);

        return ResponseEntity.ok(
                "Update deleted successfully"
        );
    }
}